package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DucoEntity
import com.example.data.DucoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SequenceState(
  val isActive: Boolean = false,
  val currentIndex: Int = 0,
  val stepCurrentProgress: Int = 0,
  val stepTargetTotal: Int? = null,
  val autoAdvanceNotice: String? = null,
  val allCompletedCount: Int? = null,
)

data class DucoUiState(
  val ducos: List<DucoEntity> = emptyList(),
  val selectedDuco: DucoEntity? = null,
  val totalAllRecitations: Int = 0,
  val streakDays: Int = 1,
  val isDarkModePreference: Boolean? = null,
  val isLoaded: Boolean = false,
  val sequence: SequenceState = SequenceState(),
)

/**
 * Manages the active list of [DucoEntity] items, selection, independent counters,
 * daily streak tracking, and automatic progression to the next Duco in the sequence
 * once the current Duco reaches its target count.
 */
class DucoViewModel(private val repository: DucoRepository) : ViewModel() {

  private val localSelectedId = MutableStateFlow<Long?>(null)
  private val sequenceState = MutableStateFlow(SequenceState())
  private val optimisticCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
  private var noticeClearJob: Job? = null

  init {
    recordDailyAppVisit()
  }

  fun recordDailyAppVisit() {
    viewModelScope.launch { repository.recordDailyAppVisit() }
  }

  val uiState: StateFlow<DucoUiState> =
    combine(
        repository.allDucos,
        repository.selectedDucoId,
        localSelectedId,
        repository.isDarkMode,
        repository.streakCount,
        sequenceState,
        optimisticCounts,
      ) { values ->
        @Suppress("UNCHECKED_CAST")
        val rawDucos = values[0] as List<DucoEntity>
        val persistedSelectedId = values[1] as Long?
        val overrideId = values[2] as Long?
        val darkModePref = values[3] as Boolean?
        val streakDays = values[4] as Int
        val seq = values[5] as SequenceState
        @Suppress("UNCHECKED_CAST")
        val pendingCounts = values[6] as Map<Long, Int>

        val ducos =
          rawDucos.map { entity ->
            val pending = pendingCounts[entity.id]
            if (pending != null && pending > entity.count) {
              entity.copy(count = pending)
            } else {
              entity
            }
          }

        val effectiveId = overrideId ?: persistedSelectedId
        val resolvedSelected =
          ducos.firstOrNull { it.id == effectiveId } ?: ducos.firstOrNull()
        val resolvedSeq =
          if (seq.isActive && ducos.isEmpty()) {
            SequenceState()
          } else if (seq.isActive && resolvedSelected != null) {
            val actualIndex = ducos.indexOfFirst { it.id == resolvedSelected.id }.coerceAtLeast(0)
            seq.copy(currentIndex = actualIndex)
          } else {
            seq
          }
        DucoUiState(
          ducos = ducos,
          selectedDuco = resolvedSelected,
          totalAllRecitations = ducos.sumOf { it.count },
          streakDays = streakDays,
          isDarkModePreference = darkModePref,
          isLoaded = true,
          sequence = resolvedSeq,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DucoUiState(isLoaded = false),
      )

  fun selectDuco(id: Long) {
    localSelectedId.value = id
    val state = uiState.value
    if (state.sequence.isActive) {
      val idx = state.ducos.indexOfFirst { it.id == id }
      if (idx >= 0) {
        sequenceState.value = buildStepState(idx, state.ducos[idx])
      }
    }
    viewModelScope.launch { repository.selectDuco(id) }
  }

  fun startDucoSequence() {
    val ducos = uiState.value.ducos
    if (ducos.isEmpty()) return
    noticeClearJob?.cancel()
    val firstDuco = ducos.first()
    localSelectedId.value = firstDuco.id
    sequenceState.value = buildStepState(index = 0, duco = firstDuco)
    viewModelScope.launch { repository.selectDuco(firstDuco.id) }
  }

  fun stopDucoSequence() {
    noticeClearJob?.cancel()
    sequenceState.value = SequenceState()
  }

  fun dismissAllCompletedNotice() {
    sequenceState.update { it.copy(allCompletedCount = null) }
  }

  fun advanceToNextInSequence() {
    val state = uiState.value
    if (!state.sequence.isActive || state.ducos.isEmpty()) return
    val currentIndex = state.sequence.currentIndex
    val nextIndex = currentIndex + 1
    if (nextIndex < state.ducos.size) {
      val nextDuco = state.ducos[nextIndex]
      localSelectedId.value = nextDuco.id
      sequenceState.value =
        buildStepState(index = nextIndex, duco = nextDuco)
          .copy(autoAdvanceNotice = nextDuco.title)
      scheduleAutoAdvanceNoticeClear()
      viewModelScope.launch { repository.selectDuco(nextDuco.id) }
    } else {
      noticeClearJob?.cancel()
      sequenceState.value = SequenceState(allCompletedCount = state.ducos.size)
    }
  }

  fun incrementSelectedDuco() {
    val state = uiState.value
    val ducos = state.ducos
    val active = state.selectedDuco ?: return
    val activeId = active.id
    val previousPersistedOrOptimistic =
      maxOf(active.count, optimisticCounts.value[activeId] ?: 0)
    val nextPersistedOrOptimistic = previousPersistedOrOptimistic + 1

    optimisticCounts.update { current -> current + (activeId to nextPersistedOrOptimistic) }

    val seq = sequenceState.value
    val currentIdx = ducos.indexOfFirst { it.id == activeId }.coerceAtLeast(0)
    val nextIdx = currentIdx + 1

    if (seq.isActive) {
      val nextProgress = seq.stepCurrentProgress + 1
      val target = seq.stepTargetTotal
      if (target != null && target > 0 && nextProgress >= target) {
        if (nextIdx < ducos.size) {
          val nextDuco = ducos[nextIdx]
          localSelectedId.value = nextDuco.id
          sequenceState.value =
            buildStepState(index = nextIdx, duco = nextDuco)
              .copy(autoAdvanceNotice = nextDuco.title)
          scheduleAutoAdvanceNoticeClear()
          viewModelScope.launch { repository.selectDuco(nextDuco.id) }
        } else {
          noticeClearJob?.cancel()
          sequenceState.value = SequenceState(allCompletedCount = ducos.size)
        }
      } else {
        sequenceState.value = seq.copy(stepCurrentProgress = nextProgress)
      }
    } else {
      if (seq.allCompletedCount != null) {
        sequenceState.update { it.copy(allCompletedCount = null) }
      }
      // Automatically switch to the next Duco in the active list when the current Duco
      // crosses its target count for the first time.
      val target = active.targetCount?.takeIf { it > 0 }
      if (
        target != null &&
          previousPersistedOrOptimistic < target &&
          nextPersistedOrOptimistic >= target &&
          nextIdx < ducos.size
      ) {
        val nextDuco = ducos[nextIdx]
        localSelectedId.value = nextDuco.id
        sequenceState.update { it.copy(autoAdvanceNotice = nextDuco.title) }
        scheduleAutoAdvanceNoticeClear()
        viewModelScope.launch { repository.selectDuco(nextDuco.id) }
      }
    }

    viewModelScope.launch { repository.incrementDucoCount(activeId) }
  }

  fun resetSelectedDucoCounter() {
    val active = uiState.value.selectedDuco ?: return
    optimisticCounts.update { it - active.id }
    if (sequenceState.value.isActive) {
      sequenceState.update { it.copy(stepCurrentProgress = 0) }
    }
    viewModelScope.launch { repository.resetDucoCount(active.id) }
  }

  fun saveDuco(
    id: Long?,
    title: String,
    text: String,
    targetCount: Int?,
    onSaved: () -> Unit,
  ) {
    viewModelScope.launch {
      if (id != null) {
        optimisticCounts.update { it - id }
      }
      val savedId = repository.saveDuco(id = id, title = title, text = text, targetCount = targetCount)
      localSelectedId.value = savedId
      repository.selectDuco(savedId)
      if (sequenceState.value.isActive) {
        sequenceState.value = SequenceState()
      }
      onSaved()
    }
  }

  fun deleteDuco(id: Long) {
    viewModelScope.launch {
      optimisticCounts.update { it - id }
      val remaining = uiState.value.ducos.filter { it.id != id }
      repository.deleteDuco(id)
      val currentSelectedId = localSelectedId.value ?: uiState.value.selectedDuco?.id
      if (currentSelectedId == id) {
        val nextSelected = remaining.firstOrNull()
        localSelectedId.value = nextSelected?.id
        repository.selectDuco(nextSelected?.id)
        if (sequenceState.value.isActive) {
          if (nextSelected == null) {
            sequenceState.value = SequenceState()
          } else {
            sequenceState.value = buildStepState(index = 0, duco = nextSelected)
          }
        }
      }
    }
  }

  fun toggleDarkMode(currentlyDark: Boolean) {
    viewModelScope.launch { repository.setDarkMode(!currentlyDark) }
  }

  suspend fun getDucoById(id: Long): DucoEntity? = repository.getDucoById(id)

  private fun buildStepState(index: Int, duco: DucoEntity): SequenceState {
    val target = duco.targetCount?.takeIf { it > 0 }
    val initialProgress =
      if (target != null && duco.count < target) {
        duco.count
      } else {
        0
      }
    return SequenceState(
      isActive = true,
      currentIndex = index,
      stepCurrentProgress = initialProgress,
      stepTargetTotal = target,
      autoAdvanceNotice = null,
      allCompletedCount = null,
    )
  }

  private fun scheduleAutoAdvanceNoticeClear() {
    noticeClearJob?.cancel()
    noticeClearJob =
      viewModelScope.launch {
        delay(2600)
        sequenceState.update { it.copy(autoAdvanceNotice = null) }
      }
  }

  class Factory(private val repository: DucoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(DucoViewModel::class.java)) {
        return DucoViewModel(repository) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
  }
}
