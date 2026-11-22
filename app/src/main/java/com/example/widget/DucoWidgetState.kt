package com.example.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.data.AppDatabase
import com.example.data.DucoEntity
import com.example.data.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

data class DucoWidgetSnapshot(
  val streakDays: Int = 1,
  val activeDuco: DucoEntity? = null,
  val activeIndex: Int = 0,
  val totalDucos: Int = 0,
  val isDarkModePreference: Boolean? = null,
)

object DucoWidgetUpdater {
  suspend fun updateAll(context: Context) {
    runCatching { DucoHomeWidget().updateAll(context.applicationContext) }
  }
}

object DucoWidgetDataSource {
  suspend fun loadSnapshot(context: Context): DucoWidgetSnapshot {
    val appContext = context.applicationContext
    val dao = AppDatabase.getInstance(appContext).ducoDao()
    val prefs = PreferencesManager(appContext)
    val ducos = dao.getAllDucosOnce()
    val selectedId = prefs.selectedDucoId.first()
    val streak = prefs.streakCount.first()
    val darkMode = prefs.isDarkMode.first()
    return buildSnapshot(
      ducos = ducos,
      selectedId = selectedId,
      streakDays = streak,
      isDarkModePreference = darkMode,
    )
  }

  fun observeSnapshot(context: Context): Flow<DucoWidgetSnapshot> {
    val appContext = context.applicationContext
    val dao = AppDatabase.getInstance(appContext).ducoDao()
    val prefs = PreferencesManager(appContext)
    return combine(
      dao.observeAllDucos(),
      prefs.selectedDucoId,
      prefs.streakCount,
      prefs.isDarkMode,
    ) { ducos, selectedId, streak, darkMode ->
      buildSnapshot(
        ducos = ducos,
        selectedId = selectedId,
        streakDays = streak,
        isDarkModePreference = darkMode,
      )
    }
  }

  suspend fun incrementActiveDucoFromWidget(context: Context) {
    val appContext = context.applicationContext
    val dao = AppDatabase.getInstance(appContext).ducoDao()
    val prefs = PreferencesManager(appContext)
    val ducos = dao.getAllDucosOnce()
    if (ducos.isEmpty()) return

    val selectedId = prefs.selectedDucoId.first()
    val currentIndex =
      ducos.indexOfFirst { it.id == selectedId }.let { idx -> if (idx >= 0) idx else 0 }
    val active = ducos[currentIndex]

    prefs.recordDailyAppVisit()
    dao.incrementCount(active.id)

    val target = active.targetCount?.takeIf { it > 0 }
    val nextCount = active.count + 1
    if (target != null && active.count < target && nextCount >= target) {
      val nextIndex = currentIndex + 1
      if (nextIndex < ducos.size) {
        prefs.setSelectedDucoId(ducos[nextIndex].id)
      }
    }
    DucoWidgetUpdater.updateAll(appContext)
  }

  internal fun buildSnapshot(
    ducos: List<DucoEntity>,
    selectedId: Long?,
    streakDays: Int,
    isDarkModePreference: Boolean?,
  ): DucoWidgetSnapshot {
    val activeIndexZeroBased =
      ducos.indexOfFirst { it.id == selectedId }.let { idx ->
        if (idx >= 0) idx else if (ducos.isNotEmpty()) 0 else -1
      }
    val activeDuco = if (activeIndexZeroBased >= 0) ducos[activeIndexZeroBased] else null
    return DucoWidgetSnapshot(
      streakDays = streakDays,
      activeDuco = activeDuco,
      activeIndex = if (activeIndexZeroBased >= 0) activeIndexZeroBased + 1 else 0,
      totalDucos = ducos.size,
      isDarkModePreference = isDarkModePreference,
    )
  }
}
