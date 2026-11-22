package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.DucoRepository
import com.example.data.PreferencesManager
import com.example.ui.components.DucoBottomNavBar
import com.example.ui.components.DucoTab
import com.example.ui.screens.AddEditDucoScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SplashOverlay
import com.example.ui.theme.DucoCounterTheme
import com.example.ui.viewmodel.DucoViewModel
import com.example.widget.DucoWidgetUpdater
import kotlinx.coroutines.delay

const val ROUTE_HOME = "home"
const val ROUTE_ADD_DUCO_BASE = "add_duco"
const val ROUTE_ADD_DUCO_PATTERN = "add_duco?ducoId={ducoId}"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getInstance(applicationContext)
    val preferencesManager = PreferencesManager(applicationContext)
    val repository =
      DucoRepository(
        ducoDao = database.ducoDao(),
        preferencesManager = preferencesManager,
        onWidgetDataChanged = { DucoWidgetUpdater.updateAll(applicationContext) },
      )
    val factory = DucoViewModel.Factory(repository)

    setContent {
      val viewModel: DucoViewModel = viewModel(factory = factory)
      DucoCounterApp(viewModel = viewModel)
    }
  }
}

@Composable
fun DucoCounterApp(viewModel: DucoViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val systemDark = isSystemInDarkTheme()
  val isDarkTheme = uiState.isDarkModePreference ?: systemDark

  var showSplash by remember { mutableStateOf(true) }
  LaunchedEffect(Unit) {
    delay(950)
    showSplash = false
  }

  LifecycleEventEffect(Lifecycle.Event.ON_START) {
    viewModel.recordDailyAppVisit()
  }

  DucoCounterTheme(darkTheme = isDarkTheme) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_HOME
    val currentTab =
      if (currentRoute.startsWith(ROUTE_ADD_DUCO_BASE)) DucoTab.ADD_DUCO else DucoTab.HOME

    Box(modifier = Modifier.fillMaxSize()) {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
          DucoBottomNavBar(
            currentTab = currentTab,
            onSelectHome = {
              navController.navigate(ROUTE_HOME) {
                popUpTo(ROUTE_HOME) { inclusive = true }
                launchSingleTop = true
              }
            },
            onSelectAddDuco = {
              navController.navigate(ROUTE_ADD_DUCO_BASE) {
                popUpTo(ROUTE_HOME) { saveState = false }
                launchSingleTop = true
              }
            },
          )
        },
      ) { innerPadding ->
        NavHost(
          navController = navController,
          startDestination = ROUTE_HOME,
          modifier = Modifier.fillMaxSize(),
        ) {
          composable(route = ROUTE_HOME) {
            HomeScreen(
              uiState = uiState,
              isDarkTheme = isDarkTheme,
              onToggleDarkMode = { viewModel.toggleDarkMode(isDarkTheme) },
              onSelectDuco = { id -> viewModel.selectDuco(id) },
              onStartSequence = { viewModel.startDucoSequence() },
              onStopSequence = { viewModel.stopDucoSequence() },
              onNextInSequence = { viewModel.advanceToNextInSequence() },
              onDismissCompletedNotice = { viewModel.dismissAllCompletedNotice() },
              onIncrementCounter = { viewModel.incrementSelectedDuco() },
              onResetCounter = { viewModel.resetSelectedDucoCounter() },
              onAddDucoClick = {
                navController.navigate(ROUTE_ADD_DUCO_BASE) { launchSingleTop = true }
              },
              onEditDucoClick = { id ->
                navController.navigate("$ROUTE_ADD_DUCO_BASE?ducoId=$id") {
                  launchSingleTop = true
                }
              },
              onDeleteDuco = { id -> viewModel.deleteDuco(id) },
              bottomPadding = innerPadding,
            )
          }

          composable(
            route = ROUTE_ADD_DUCO_PATTERN,
            arguments =
              listOf(
                navArgument("ducoId") {
                  type = NavType.LongType
                  defaultValue = -1L
                }
              ),
          ) { entry ->
            val rawId = entry.arguments?.getLong("ducoId") ?: -1L
            val editingId = if (rawId > 0L) rawId else null
            AddEditDucoScreen(
              editingDucoId = editingId,
              loadDuco = { id -> viewModel.getDucoById(id) },
              onSaveDuco = { id, title, text, targetCount ->
                viewModel.saveDuco(
                  id = id,
                  title = title,
                  text = text,
                  targetCount = targetCount,
                ) {
                  navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_HOME) { inclusive = true }
                    launchSingleTop = true
                  }
                }
              },
              onCancel = {
                navController.navigate(ROUTE_HOME) {
                  popUpTo(ROUTE_HOME) { inclusive = true }
                  launchSingleTop = true
                }
              },
              bottomPadding = innerPadding,
            )
          }
        }
      }

      SplashOverlay(visible = showSplash)
    }
  }
}
