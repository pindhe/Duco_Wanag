package com.example.data

import kotlinx.coroutines.flow.Flow

class DucoRepository(
  private val ducoDao: DucoDao,
  private val preferencesManager: PreferencesManager,
  private val onWidgetDataChanged: (suspend () -> Unit)? = null,
) {
  val allDucos: Flow<List<DucoEntity>> = ducoDao.observeAllDucos()
  val selectedDucoId: Flow<Long?> = preferencesManager.selectedDucoId
  val isDarkMode: Flow<Boolean?> = preferencesManager.isDarkMode
  val streakCount: Flow<Int> = preferencesManager.streakCount

  suspend fun recordDailyAppVisit(): Int {
    val updated = preferencesManager.recordDailyAppVisit()
    onWidgetDataChanged?.invoke()
    return updated
  }

  suspend fun getDucoById(id: Long): DucoEntity? = ducoDao.getDucoById(id)

  suspend fun saveDuco(
    id: Long?,
    title: String,
    text: String,
    targetCount: Int?,
  ): Long {
    val trimmedTitle = title.trim()
    val trimmedText = text.trim()
    val resultId =
      if (id == null) {
        val newId =
          ducoDao.insertDuco(
            DucoEntity(
              title = trimmedTitle,
              text = trimmedText,
              count = 0,
              targetCount = targetCount,
              createdAtMillis = System.currentTimeMillis(),
            )
          )
        preferencesManager.setSelectedDucoId(newId)
        newId
      } else {
        val existing = ducoDao.getDucoById(id)
        if (existing != null) {
          ducoDao.updateDuco(
            existing.copy(
              title = trimmedTitle,
              text = trimmedText,
              targetCount = targetCount,
            )
          )
          id
        } else {
          val newId =
            ducoDao.insertDuco(
              DucoEntity(
                title = trimmedTitle,
                text = trimmedText,
                count = 0,
                targetCount = targetCount,
                createdAtMillis = System.currentTimeMillis(),
              )
            )
          preferencesManager.setSelectedDucoId(newId)
          newId
        }
      }
    onWidgetDataChanged?.invoke()
    return resultId
  }

  suspend fun deleteDuco(id: Long) {
    ducoDao.deleteDucoById(id)
    onWidgetDataChanged?.invoke()
  }

  suspend fun incrementDucoCount(id: Long) {
    ducoDao.incrementCount(id)
    onWidgetDataChanged?.invoke()
  }

  suspend fun resetDucoCount(id: Long) {
    ducoDao.resetCount(id)
    onWidgetDataChanged?.invoke()
  }

  suspend fun selectDuco(id: Long?) {
    preferencesManager.setSelectedDucoId(id)
    onWidgetDataChanged?.invoke()
  }

  suspend fun setDarkMode(enabled: Boolean) {
    preferencesManager.setDarkMode(enabled)
    onWidgetDataChanged?.invoke()
  }
}
