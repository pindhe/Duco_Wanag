package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DucoEntity
import com.example.ui.theme.ArabicDucoTextStyle
import com.example.ui.theme.CounterDisplayTextStyle
import com.example.ui.theme.SoftMint
import com.example.ui.viewmodel.SequenceState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun containsArabicScript(text: String): Boolean {
  return text.any { ch -> ch in '\u0600'..'\u06FF' || ch in '\u0750'..'\u077F' }
}

private fun formatCreationDate(millis: Long): String {
  val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
  return formatter.format(Date(millis))
}

@Composable
fun SelectableDucoChipCard(
  duco: DucoEntity,
  indexNumber: Int,
  isSelected: Boolean,
  onSelect: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val containerColor =
    if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerLowest
  val borderColor =
    if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant

  Surface(
    onClick = onSelect,
    modifier =
      modifier
        .widthIn(min = 112.dp, max = 184.dp)
        .minimumInteractiveComponentSize()
        .testTag("duco_selector_card_${duco.id}"),
    shape = RoundedCornerShape(16.dp),
    color = containerColor,
    border = BorderStroke(width = if (isSelected) 1.5.dp else 1.dp, color = borderColor),
    shadowElevation = if (isSelected) 1.dp else 0.dp,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
    ) {
      Surface(
        shape = CircleShape,
        color =
          if (isSelected) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(22.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "$indexNumber",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color =
              if (isSelected) MaterialTheme.colorScheme.onPrimary
              else MaterialTheme.colorScheme.onSecondaryContainer,
          )
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column(modifier = Modifier.weight(1f, fill = false)) {
        Text(
          text = duco.title,
          style = MaterialTheme.typography.labelLarge,
          color =
            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        val countText =
          if (duco.targetCount != null) "${duco.count}/${duco.targetCount}" else "${duco.count}"
        Text(
          text = countText,
          style =
            MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              fontFeatureSettings = "tnum",
            ),
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
        )
      }
    }
  }
}

@Composable
fun UnifiedDucoCounterCard(
  duco: DucoEntity,
  indexNumber: Int?,
  totalAllRecitations: Int,
  sequence: SequenceState,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onResetClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isArabic = remember(duco.text) { containsArabicScript(duco.text) }
  val formattedDate = remember(duco.createdAtMillis) { formatCreationDate(duco.createdAtMillis) }
  var showFullDucoDialog by remember(duco.id) { mutableStateOf(false) }

  val target = duco.targetCount
  val displayProgressCount =
    if (sequence.isActive && sequence.stepTargetTotal != null) {
      sequence.stepCurrentProgress
    } else {
      duco.count
    }
  val isTargetReached = target != null && target > 0 && displayProgressCount >= target
  val progressFraction =
    if (target != null && target > 0) {
      (displayProgressCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    } else {
      0f
    }
  val animatedProgress by
    animateFloatAsState(
      targetValue = progressFraction,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "unified_card_progress",
    )

  if (showFullDucoDialog) {
    AlertDialog(
      onDismissRequest = { showFullDucoDialog = false },
      shape = RoundedCornerShape(24.dp),
      containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
      title = { Text(text = duco.title, style = MaterialTheme.typography.titleLarge) },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = duco.text,
            style =
              if (isArabic) ArabicDucoTextStyle
              else MaterialTheme.typography.bodyLarge.copy(lineHeight = 25.sp),
            textAlign = if (isArabic) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { showFullDucoDialog = false }) {
          Text(text = stringResource(R.string.action_dismiss))
        }
      },
    )
  }

  val cardMintAccent = SoftMint
  val subtleWhite = Color.White.copy(alpha = 0.80f)
  val hairlineColor = Color.White.copy(alpha = 0.18f)

  Card(
    onClick = { showFullDucoDialog = true },
    modifier = modifier.fillMaxWidth().testTag("selected_duco_card"),
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A331C)),
    border = BorderStroke(1.dp, cardMintAccent.copy(alpha = 0.28f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
      // Generated serene Islamic emerald arch & arabesque background artwork
      Image(
        painter = painterResource(id = R.drawable.img_duco_card_bg),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.matchParentSize(),
      )

      // Translucent deep emerald scrim ensuring crisp legibility in both Light & Dark modes
      Box(
        modifier =
          Modifier.matchParentSize()
            .background(
              brush =
                Brush.verticalGradient(
                  colors =
                    listOf(
                      Color(0xCC062815),
                      Color(0xBA09351D),
                      Color(0xD9052112),
                    )
                )
            )
      )

      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
      ) {
        // Top Header: #Index + Title + Date + Edit/Delete
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (indexNumber != null) {
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.16f),
              border = BorderStroke(1.dp, cardMintAccent.copy(alpha = 0.4f)),
              modifier = Modifier.size(30.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "#$indexNumber",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = duco.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = formattedDate,
              style = MaterialTheme.typography.labelSmall,
              color = subtleWhite,
            )
          }

          IconButton(
            onClick = onEdit,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("edit_duco_button"),
          ) {
            Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = stringResource(R.string.cd_edit_duco),
              tint = cardMintAccent,
              modifier = Modifier.size(20.dp),
            )
          }
          IconButton(
            onClick = onDelete,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("delete_duco_button"),
          ) {
            Icon(
              imageVector = Icons.Outlined.Delete,
              contentDescription = stringResource(R.string.cd_delete_duco),
              tint = Color(0xFFFFB4AB),
              modifier = Modifier.size(20.dp),
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = hairlineColor)
        Spacer(modifier = Modifier.height(12.dp))

        // Side-by-side body: Duco text on the left, live Counter & Reset on the right
        Row(
          modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Left pane: Duco text
          Box(
            modifier = Modifier.weight(1.25f).padding(end = 12.dp),
            contentAlignment = if (isArabic) Alignment.Center else Alignment.CenterStart,
          ) {
            Text(
              text = duco.text,
              style =
                if (isArabic) {
                  ArabicDucoTextStyle.copy(fontSize = 20.sp, lineHeight = 34.sp)
                } else {
                  MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                  )
                },
              color = Color.White.copy(alpha = 0.96f),
              textAlign = if (isArabic) TextAlign.Center else TextAlign.Start,
              maxLines = 4,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.fillMaxWidth(),
            )
          }

          VerticalDivider(
            thickness = 1.dp,
            color = hairlineColor,
            modifier = Modifier.fillMaxHeight().padding(vertical = 4.dp),
          )

          // Right pane: Live Counter + Total + Reset
          Column(
            modifier = Modifier.weight(0.95f).padding(start = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Text(
              text = stringResource(R.string.label_read_count),
              style = MaterialTheme.typography.labelSmall,
              color = cardMintAccent,
              textAlign = TextAlign.Center,
            )

            AnimatedContent(
              targetState = duco.count,
              transitionSpec = {
                (slideInVertically { height -> height / 3 } + fadeIn()) togetherWith
                  (slideOutVertically { height -> -height / 3 } + fadeOut())
              },
              label = "unified_counter_number",
            ) { animatedCount ->
              Text(
                text = "$animatedCount",
                style = CounterDisplayTextStyle.copy(fontSize = 44.sp, lineHeight = 50.sp),
                color = Color.White,
                modifier = Modifier.testTag("counter_value_text"),
              )
            }

            Text(
              text = "${stringResource(R.string.label_total_count)}: $totalAllRecitations",
              style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
              color = subtleWhite,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = onResetClick,
              enabled = duco.count > 0,
              shape = RoundedCornerShape(999.dp),
              border =
                BorderStroke(
                  1.dp,
                  if (duco.count > 0) cardMintAccent.copy(alpha = 0.55f)
                  else Color.White.copy(alpha = 0.18f),
                ),
              colors =
                ButtonDefaults.outlinedButtonColors(
                  containerColor = Color.White.copy(alpha = 0.10f),
                  contentColor = Color.White,
                  disabledContainerColor = Color.White.copy(alpha = 0.04f),
                  disabledContentColor = Color.White.copy(alpha = 0.35f),
                ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.minimumInteractiveComponentSize().testTag("reset_counter_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = stringResource(R.string.action_reset),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
              )
            }
          }
        }

        // Bottom target progress strip (when target is configured)
        if (target != null && target > 0) {
          Spacer(modifier = Modifier.height(12.dp))
          LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(999.dp)),
            color = cardMintAccent,
            trackColor = Color.White.copy(alpha = 0.18f),
            strokeCap = StrokeCap.Round,
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text =
                if (sequence.isActive && duco.count > displayProgressCount) {
                  stringResource(R.string.sequence_lap_format, displayProgressCount, target)
                } else {
                  stringResource(R.string.label_target_format, duco.count, target)
                },
              style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
              color = subtleWhite,
            )
            if (isTargetReached) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.CheckCircle,
                  contentDescription = null,
                  tint = cardMintAccent,
                  modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(R.string.label_target_completed),
                  style =
                    MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = cardMintAccent,
                )
              }
            }
          }
        }
      }
    }
  }
}
