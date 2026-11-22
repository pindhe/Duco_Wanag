package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.ducoPreferencesDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "duco_preferences")

class PreferencesManager(private val context: Context) {

  private object Keys {
    val SELECTED_DUCO_ID = longPreferencesKey("selected_duco_id")
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val STREAK_COUNT = intPreferencesKey("streak_count")
    val LAST_ACTIVE_LOCAL_DAY = longPreferencesKey("last_active_local_day")
  }

  val selectedDucoId: Flow<Long?> =
    context.ducoPreferencesDataStore.data.map { prefs -> prefs[Keys.SELECTED_DUCO_ID] }

  val isDarkMode: Flow<Boolean?> =
    context.ducoPreferencesDataStore.data.map { prefs -> prefs[Keys.DARK_MODE_ENABLED] }

  val streakCount: Flow<Int> =
    context.ducoPreferencesDataStore.data.map { prefs -> prefs[Keys.STREAK_COUNT] ?: 1 }

  suspend fun setSelectedDucoId(id: Long?) {
    context.ducoPreferencesDataStore.edit { prefs ->
      if (id == null) {
        prefs.remove(Keys.SELECTED_DUCO_ID)
      } else {
        prefs[Keys.SELECTED_DUCO_ID] = id
      }
    }
  }

  suspend fun setDarkMode(enabled: Boolean) {
    context.ducoPreferencesDataStore.edit { prefs -> prefs[Keys.DARK_MODE_ENABLED] = enabled }
  }

  suspend fun recordDailyAppVisit(todayDayNumber: Long = currentLocalDayNumber()): Int {
    var updatedStreak = 1
    context.ducoPreferencesDataStore.edit { prefs ->
      val lastDay = prefs[Keys.LAST_ACTIVE_LOCAL_DAY]
      val currentStreak = prefs[Keys.STREAK_COUNT] ?: 0

      updatedStreak =
        when {
          lastDay == null -> 1
          lastDay == todayDayNumber -> currentStreak.coerceAtLeast(1)
          todayDayNumber - lastDay == 1L -> currentStreak.coerceAtLeast(1) + 1
          todayDayNumber > lastDay -> 1
          else -> currentStreak.coerceAtLeast(1)
        }

      prefs[Keys.STREAK_COUNT] = updatedStreak
      prefs[Keys.LAST_ACTIVE_LOCAL_DAY] = todayDayNumber
    }
    return updatedStreak
  }

  companion object {
    fun currentLocalDayNumber(nowMillis: Long = System.currentTimeMillis()): Long {
      val cal =
        Calendar.getInstance().apply {
          timeInMillis = nowMillis
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
      val offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
      return (cal.timeInMillis + offset) / 86_400_000L
    }
  }
}
