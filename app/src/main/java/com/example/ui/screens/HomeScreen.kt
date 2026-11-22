package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.widget.DucoHomeWidgetReceiver
import com.example.data.DucoEntity
import com.example.ui.components.DeleteDucoDialog
import com.example.ui.components.EmptyDucoIllustration
import com.example.ui.components.ResetCounterDialog
import com.example.ui.components.SelectableDucoChipCard
import com.example.ui.components.TasbiixCounterButton
import com.example.ui.components.UnifiedDucoCounterCard
import com.example.ui.viewmodel.DucoUiState
import com.example.ui.viewmodel.SequenceState

@Composable
fun HomeScreen(
  uiState: DucoUiState,
  isDarkTheme: Boolean,
  onToggleDarkMode: () -> Unit,
  onSelectDuco: (Long) -> Unit,
  onStartSequence: () -> Unit,
  onStopSequence: () -> Unit,
  onNextInSequence: () -> Unit,
  onDismissCompletedNotice: () -> Unit,
  onIncrementCounter: () -> Unit,
  onResetCounter: () -> Unit,
  onAddDucoClick: () -> Unit,
  onEditDucoClick: (Long) -> Unit,
  onDeleteDuco: (Long) -> Unit,
  bottomPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  val view = LocalView.current
  var showResetDialog by remember { mutableStateOf(false) }
  var showStreakDialog by remember { mutableStateOf(false) }
  var ducoPendingDelete by remember { mutableStateOf<DucoEntity?>(null) }

  val selectedDuco = uiState.selectedDuco
  val sequence = uiState.sequence

  LaunchedEffect(sequence.autoAdvanceNotice, sequence.allCompletedCount) {
    if (sequence.autoAdvanceNotice != null || sequence.allCompletedCount != null) {
      view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
  }

  if (showStreakDialog) {
    StreakInfoDialog(
      streakDays = uiState.streakDays,
      isDarkTheme = isDarkTheme,
      onDismiss = { showStreakDialog = false },
    )
  }

  if (showResetDialog && selectedDuco != null) {
    ResetCounterDialog(
      ducoTitle = selectedDuco.title,
      onConfirm = {
        showResetDialog = false
        onResetCounter()
      },
      onDismiss = { showResetDialog = false },
    )
  }

  ducoPendingDelete?.let { target ->
    DeleteDucoDialog(
      ducoTitle = target.title,
      onConfirm = {
        ducoPendingDelete = null
        onDeleteDuco(target.id)
      },
      onDismiss = { ducoPendingDelete = null },
    )
  }

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = bottomPadding.calculateBottomPadding()),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      HomeTopHeader(
        streakDays = uiState.streakDays,
        isDarkTheme = isDarkTheme,
        onStreakClick = { showStreakDialog = true },
        onToggleDarkMode = onToggleDarkMode,
      )

      if (!uiState.isLoaded) {
        Box(modifier = Modifier.fillMaxSize())
      } else if (uiState.ducos.isEmpty()) {
        EmptyDucoState(
          onAddDucoClick = onAddDucoClick,
          modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        )
      } else if (selectedDuco != null) {
        val selectedIndex =
          uiState.ducos.indexOfFirst { it.id == selectedDuco.id }.takeIf { it >= 0 }
        val effectiveCountForRing =
          if (sequence.isActive && sequence.stepTargetTotal != null) {
            sequence.stepCurrentProgress
          } else {
            selectedDuco.count
          }
        val effectiveTargetForRing =
          if (sequence.isActive) sequence.stepTargetTotal else selectedDuco.targetCount

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
          val isWide = maxWidth >= 680.dp
          val compactHeight = maxHeight < 540.dp
          val buttonSize = if (compactHeight) 174.dp else 208.dp

          if (isWide) {
            Row(
              modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.spacedBy(24.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(
                modifier = Modifier.weight(1.25f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                MyDucoUnifiedSection(
                  ducos = uiState.ducos,
                  selectedDuco = selectedDuco,
                  selectedIndex = selectedIndex,
                  totalAllRecitations = uiState.totalAllRecitations,
                  sequence = sequence,
                  onSelectDuco = onSelectDuco,
                  onStartSequence = onStartSequence,
                  onStopSequence = onStopSequence,
                  onNextInSequence = onNextInSequence,
                  onEditDucoClick = { onEditDucoClick(selectedDuco.id) },
                  onDeleteDucoClick = { ducoPendingDelete = selectedDuco },
                  onResetClick = { showResetDialog = true },
                )
              }
              Box(
                modifier = Modifier.weight(0.75f),
                contentAlignment = Alignment.Center,
              ) {
                TasbiixCounterButton(
                  count = effectiveCountForRing,
                  targetCount = effectiveTargetForRing,
                  isDarkTheme = isDarkTheme,
                  enabled = true,
                  outerSize = buttonSize,
                  onTap = onIncrementCounter,
                )
              }
            }
          } else {
            Column(
              modifier =
                Modifier.fillMaxSize()
                  .padding(horizontal = 18.dp, vertical = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceBetween,
            ) {
              MyDucoUnifiedSection(
                ducos = uiState.ducos,
                selectedDuco = selectedDuco,
                selectedIndex = selectedIndex,
                totalAllRecitations = uiState.totalAllRecitations,
                sequence = sequence,
                onSelectDuco = onSelectDuco,
                onStartSequence = onStartSequence,
                onStopSequence = onStopSequence,
                onNextInSequence = onNextInSequence,
                onEditDucoClick = { onEditDucoClick(selectedDuco.id) },
                onDeleteDucoClick = { ducoPendingDelete = selectedDuco },
                onResetClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth().widthIn(max = 580.dp),
              )

              Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
              ) {
                TasbiixCounterButton(
                  count = effectiveCountForRing,
                  targetCount = effectiveTargetForRing,
                  isDarkTheme = isDarkTheme,
                  enabled = true,
                  outerSize = buttonSize,
                  onTap = onIncrementCounter,
                )
              }
            }
          }
        }
      }
    }

    // Floating overlay notices for auto-advance / completion
    Column(
      modifier =
        Modifier.align(Alignment.TopCenter)
          .fillMaxWidth()
          .padding(horizontal = 18.dp, vertical = 54.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AnimatedVisibility(
        visible = sequence.autoAdvanceNotice != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
      ) {
        sequence.autoAdvanceNotice?.let { nextTitle ->
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier.testTag("auto_advance_banner"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = stringResource(R.string.sequence_auto_next_notice, nextTitle),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            }
          }
        }
      }

      AnimatedVisibility(
        visible = sequence.allCompletedCount != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
      ) {
        sequence.allCompletedCount?.let { count ->
          Card(
            shape = RoundedCornerShape(20.dp),
            colors =
              CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp).testTag("all_completed_banner"),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.Celebration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(R.string.sequence_all_completed_title),
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                  text = stringResource(R.string.sequence_all_completed_message, count),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
              IconButton(
                onClick = onDismissCompletedNotice,
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Close,
                  contentDescription = stringResource(R.string.action_dismiss),
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun HomeTopHeader(
  streakDays: Int,
  isDarkTheme: Boolean,
  onStreakClick: () -> Unit,
  onToggleDarkMode: () -> Unit,
) {
  val flameTint = if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFFD97706)
  val flameBg =
    if (isDarkTheme) Color(0xFF2B2312) else Color(0xFFFEF3C7)
  val flameBorder =
    if (isDarkTheme) Color(0xFFFBBF24).copy(alpha = 0.45f) else Color(0xFFF59E0B).copy(alpha = 0.45f)
  val streakCd = stringResource(R.string.cd_streak_badge, streakDays)

  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.headlineMedium,
      color = MaterialTheme.colorScheme.onBackground,
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Surface(
        onClick = onStreakClick,
        shape = RoundedCornerShape(999.dp),
        color = flameBg,
        border = BorderStroke(1.dp, flameBorder),
        shadowElevation = 1.dp,
        modifier =
          Modifier.minimumInteractiveComponentSize()
            .testTag("streak_badge_button")
            .semantics { contentDescription = streakCd },
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = null,
            tint = flameTint,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$streakDays",
            style =
              MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum",
              ),
            color = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF92400E),
            maxLines = 1,
          )
        }
      }

      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        shadowElevation = 1.dp,
      ) {
        IconButton(
          onClick = onToggleDarkMode,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("theme_toggle_button"),
        ) {
          Icon(
            imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = stringResource(R.string.cd_toggle_theme),
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }
    }
  }
}

@Composable
private fun StreakInfoDialog(
  streakDays: Int,
  isDarkTheme: Boolean,
  onDismiss: () -> Unit,
) {
  val flameTint = if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFFD97706)
  val flameHalo =
    if (isDarkTheme) Color(0xFF3A2D11) else Color(0xFFFEF3C7)

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(26.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    icon = {
      Surface(
        shape = CircleShape,
        color = Color.Transparent,
        modifier = Modifier.size(64.dp),
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.fillMaxSize()
              .padding(2.dp),
        ) {
          Surface(
            shape = CircleShape,
            color = flameHalo,
            border = BorderStroke(1.5.dp, flameTint.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxSize(),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = flameTint,
                modifier = Modifier.size(34.dp),
              )
            }
          }
        }
      }
    },
    title = {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
          text = stringResource(R.string.streak_dialog_title),
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = stringResource(R.string.streak_dialog_count, streakDays),
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFeatureSettings = "tnum",
            ),
          color = flameTint,
          textAlign = TextAlign.Center,
        )
      }
    },
    text = {
      val context = LocalContext.current
      val canPinWidget =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
          remember(context) { AppWidgetManager.getInstance(context).isRequestPinAppWidgetSupported }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = stringResource(R.string.streak_dialog_message),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )
        if (canPinWidget) {
          Spacer(modifier = Modifier.height(14.dp))
          OutlinedButton(
            onClick = {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = AppWidgetManager.getInstance(context)
                val provider = ComponentName(context, DucoHomeWidgetReceiver::class.java)
                manager.requestPinAppWidget(provider, null, null)
                onDismiss()
              }
            },
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.testTag("pin_widget_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Widgets,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.action_pin_widget))
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(999.dp),
      ) {
        Text(text = stringResource(R.string.action_dismiss))
      }
    },
  )
}

@Composable
private fun MyDucoUnifiedSection(
  ducos: List<DucoEntity>,
  selectedDuco: DucoEntity,
  selectedIndex: Int?,
  totalAllRecitations: Int,
  sequence: SequenceState,
  onSelectDuco: (Long) -> Unit,
  onStartSequence: () -> Unit,
  onStopSequence: () -> Unit,
  onNextInSequence: () -> Unit,
  onEditDucoClick: () -> Unit,
  onDeleteDucoClick: () -> Unit,
  onResetClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()

  LaunchedEffect(selectedIndex) {
    if (selectedIndex != null && selectedIndex >= 0 && ducos.size > 1) {
      listState.animateScrollToItem(selectedIndex)
    }
  }

  Column(modifier = modifier) {
    if (!sequence.isActive) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = stringResource(R.string.section_my_duco),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Text(
              text = "${ducos.size}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }
        }

        Button(
          onClick = onStartSequence,
          shape = RoundedCornerShape(999.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.minimumInteractiveComponentSize().testTag("start_my_ducos_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = stringResource(R.string.action_start),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
          )
        }
      }
    } else {
      ActiveSequenceCompactBar(
        currentIndex = sequence.currentIndex,
        totalDucos = ducos.size,
        onNextClick = onNextInSequence,
        onStopClick = onStopSequence,
      )
    }

    if (ducos.size > 1) {
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        itemsIndexed(items = ducos, key = { _, item -> item.id }) { index, duco ->
          SelectableDucoChipCard(
            duco = duco,
            indexNumber = index + 1,
            isSelected = duco.id == selectedDuco.id,
            onSelect = { onSelectDuco(duco.id) },
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    UnifiedDucoCounterCard(
      duco = selectedDuco,
      indexNumber = selectedIndex?.plus(1),
      totalAllRecitations = totalAllRecitations,
      sequence = sequence,
      onEdit = onEditDucoClick,
      onDelete = onDeleteDucoClick,
      onResetClick = onResetClick,
    )
  }
}

@Composable
private fun ActiveSequenceCompactBar(
  currentIndex: Int,
  totalDucos: Int,
  onNextClick: () -> Unit,
  onStopClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.primaryContainer,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
    modifier = Modifier.fillMaxWidth().testTag("sequence_active_banner"),
  ) {
    Row(
      modifier =
        Modifier.fillMaxWidth().padding(start = 14.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = stringResource(R.string.sequence_step_format, currentIndex + 1, totalDucos),
        style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(
          onClick = onNextClick,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("sequence_next_button"),
        ) {
          Text(
            text = stringResource(R.string.action_next_duco),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          )
          Spacer(modifier = Modifier.width(3.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
          )
        }
        TextButton(
          onClick = onStopClick,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("sequence_stop_button"),
        ) {
          Text(
            text = stringResource(R.string.action_stop_sequence),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.error,
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyDucoState(
  onAddDucoClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    EmptyDucoIllustration(
      contentDescriptionText = stringResource(R.string.cd_empty_illustration),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.empty_state_title),
      style = MaterialTheme.typography.headlineMedium,
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = stringResource(R.string.empty_state_subtitle),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.widthIn(max = 320.dp),
    )
    Spacer(modifier = Modifier.height(20.dp))
    Button(
      onClick = onAddDucoClick,
      shape = RoundedCornerShape(999.dp),
      contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
      modifier = Modifier.minimumInteractiveComponentSize().testTag("empty_add_duco_button"),
    ) {
      Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(R.string.nav_add_duco),
        style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}
