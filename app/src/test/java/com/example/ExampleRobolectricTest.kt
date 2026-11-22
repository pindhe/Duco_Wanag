package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.assertHasText
import androidx.glance.testing.unit.hasTestTag
import com.example.data.AppDatabase
import com.example.data.DucoEntity
import com.example.data.DucoRepository
import com.example.data.PreferencesManager
import com.example.ui.viewmodel.DucoViewModel
import com.example.widget.DucoHomeWidget
import com.example.widget.DucoWidgetContent
import com.example.widget.DucoWidgetDataSource
import com.example.widget.DucoWidgetSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var context: Context
  private lateinit var db: AppDatabase

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `app name matches Duco Wanag`() {
    val appName = context.getString(R.string.app_name)
    assertEquals("Duco Wanag", appName)
  }

  @Test
  fun `duco counters increment and reset independently`() = runBlocking {
    val dao = db.ducoDao()
    val id1 =
      dao.insertDuco(
        DucoEntity(title = "Subxaanallaah", text = "سبحان الله", targetCount = 33, createdAtMillis = 1L)
      )
    val id2 =
      dao.insertDuco(
        DucoEntity(title = "Alxamdulillaah", text = "الحمد لله", targetCount = 33, createdAtMillis = 2L)
      )

    dao.incrementCount(id1)
    dao.incrementCount(id1)
    dao.incrementCount(id2)

    val listAfterIncrement = dao.observeAllDucos().first()
    val first = listAfterIncrement.first { it.id == id1 }
    val second = listAfterIncrement.first { it.id == id2 }

    assertEquals(2, first.count)
    assertEquals(1, second.count)

    dao.resetCount(id1)
    val listAfterReset = dao.observeAllDucos().first()
    assertEquals(0, listAfterReset.first { it.id == id1 }.count)
    assertEquals(1, listAfterReset.first { it.id == id2 }.count)

    dao.deleteDucoById(id1)
    val listAfterDelete = dao.observeAllDucos().first()
    assertEquals(1, listAfterDelete.size)
    assertTrue(listAfterDelete.none { it.id == id1 })
  }

  @Test
  fun `select specific duco and sequence mode auto-advances from 1 to N`() = runTest {
    val testDispatcher = UnconfinedTestDispatcher(testScheduler)
    Dispatchers.setMain(testDispatcher)
    try {
      val dao = db.ducoDao()
      val id1 =
        dao.insertDuco(
          DucoEntity(title = "Duco 1", text = "Subxaanallaah", targetCount = 2, createdAtMillis = 10L)
        )
      val id2 =
        dao.insertDuco(
          DucoEntity(title = "Duco 2", text = "Alxamdulillaah", targetCount = 2, createdAtMillis = 20L)
        )
      val id3 =
        dao.insertDuco(
          DucoEntity(title = "Duco 3", text = "Allaahu Akbar", targetCount = 2, createdAtMillis = 30L)
        )

      val prefs = PreferencesManager(context)
      val repository = DucoRepository(dao, prefs)
      val viewModel = DucoViewModel(repository)

      val collectJob = launch(testDispatcher) { viewModel.uiState.collect() }
      viewModel.uiState.first { it.isLoaded && it.ducos.size == 3 }

      // User selects Duco 2 directly and increments it
      viewModel.selectDuco(id2)
      viewModel.uiState.first { it.selectedDuco?.id == id2 }
      viewModel.incrementSelectedDuco()
      viewModel.uiState.first { state ->
        state.ducos.firstOrNull { it.id == id2 }?.count == 1
      }

      // Start Ducooyinkayga sequence from #1
      viewModel.startDucoSequence()
      viewModel.uiState.first { it.sequence.isActive && it.selectedDuco?.id == id1 }

      // Complete Duco 1 (target = 2) -> auto-advances to Duco 2
      viewModel.incrementSelectedDuco()
      viewModel.incrementSelectedDuco()
      viewModel.uiState.first { it.selectedDuco?.id == id2 }

      // Complete Duco 2 (already had 1, needs 1 more to hit target = 2) -> auto-advances to Duco 3
      viewModel.incrementSelectedDuco()
      viewModel.uiState.first { it.selectedDuco?.id == id3 }

      // Complete Duco 3 (target = 2) -> finishes sequence
      viewModel.incrementSelectedDuco()
      viewModel.incrementSelectedDuco()
      val finalState =
        viewModel.uiState.first { !it.sequence.isActive && it.sequence.allCompletedCount == 3 }
      assertFalse(finalState.sequence.isActive)
      assertEquals(3, finalState.sequence.allCompletedCount)

      viewModel.stopDucoSequence()
      collectJob.cancel()
    } finally {
      Dispatchers.resetMain()
    }
  }

  @Test
  fun `active duco list automatically switches to next duco when target count is reached`() = runTest {
    val testDispatcher = UnconfinedTestDispatcher(testScheduler)
    Dispatchers.setMain(testDispatcher)
    try {
      val dao = db.ducoDao()
      val id1 =
        dao.insertDuco(
          DucoEntity(title = "Duco A", text = "Subxaanallaah", targetCount = 2, createdAtMillis = 100L)
        )
      val id2 =
        dao.insertDuco(
          DucoEntity(title = "Duco B", text = "Alxamdulillaah", targetCount = 3, createdAtMillis = 200L)
        )

      val prefs = PreferencesManager(context)
      val repository = DucoRepository(dao, prefs)
      val viewModel = DucoViewModel(repository)

      val collectJob = launch(testDispatcher) { viewModel.uiState.collect() }
      viewModel.uiState.first { it.isLoaded && it.ducos.size == 2 }

      viewModel.selectDuco(id1)
      viewModel.uiState.first { it.selectedDuco?.id == id1 }

      // Tap once (1 / 2) -> stays on Duco A
      viewModel.incrementSelectedDuco()
      assertEquals(id1, viewModel.uiState.value.selectedDuco?.id)

      // Tap second time (2 / 2) -> reaches target and auto-switches to Duco B
      viewModel.incrementSelectedDuco()
      val switchedState = viewModel.uiState.first { it.selectedDuco?.id == id2 }
      assertEquals(id2, switchedState.selectedDuco?.id)
      assertEquals("Duco B", switchedState.sequence.autoAdvanceNotice)

      viewModel.stopDucoSequence()
      collectJob.cancel()
    } finally {
      Dispatchers.resetMain()
    }
  }

  @Test
  fun `device daily streak increments on consecutive days and resets after missed day`() = runBlocking {
    val prefs = PreferencesManager(context)
    val startDay = 20_500L

    assertEquals(1, prefs.recordDailyAppVisit(startDay))
    assertEquals(1, prefs.recordDailyAppVisit(startDay))
    assertEquals(2, prefs.recordDailyAppVisit(startDay + 1L))
    assertEquals(3, prefs.recordDailyAppVisit(startDay + 2L))
    assertEquals(1, prefs.recordDailyAppVisit(startDay + 5L))
    assertEquals(1, prefs.streakCount.first())
  }

  @Test
  fun `home screen widget displays streak on one side and current active duco on the other`() =
    runGlanceAppWidgetUnitTest {
      val ducos =
        listOf(
          DucoEntity(id = 1L, title = "Subxaanallaah", text = "سبحان الله", count = 33, targetCount = 33),
          DucoEntity(id = 2L, title = "Alxamdulillaah", text = "الحمد لله", count = 12, targetCount = 33),
        )
      val snapshot: DucoWidgetSnapshot =
        DucoWidgetDataSource.buildSnapshot(
          ducos = ducos,
          selectedId = 2L,
          streakDays = 5,
          isDarkModePreference = false,
        )

      val bgBitmap = DucoHomeWidget.decodeWidgetBackgroundBitmap(context)
      assertNotNull(bgBitmap)

      setContext(context)
      setAppWidgetSize(DpSize(280.dp, 120.dp))
      provideComposable {
        DucoWidgetContent(
          snapshot = snapshot,
          isDark = false,
          size = DpSize(280.dp, 120.dp),
          backgroundBitmap = bgBitmap,
        )
      }

      onNode(hasTestTag("widget_streak_count")).assertHasText("5")
      onNode(hasTestTag("widget_duco_title")).assertHasText("#2 · Alxamdulillaah")
      onNode(hasTestTag("widget_duco_count")).assertHasText("12 / 33")
    }
}
