package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DucoEntity
import com.example.ui.theme.ArabicDucoTextStyle
import com.example.ui.theme.SoftMint

private data class DucoTemplate(
  val id: String,
  val title: String,
  val text: String,
  val target: Int,
)

private val READY_DUCO_TEMPLATES =
  listOf(
    DucoTemplate(
      id = "subhanallah",
      title = "Subxaanallaah",
      text = "سُبْحَانَ اللَّهِ",
      target = 33,
    ),
    DucoTemplate(
      id = "alhamdulillah",
      title = "Alxamdulillaah",
      text = "الْحَمْدُ لِلَّهِ",
      target = 33,
    ),
    DucoTemplate(
      id = "allahu_akbar",
      title = "Allaahu Akbar",
      text = "اللَّهُ أَكْبَرُ",
      target = 33,
    ),
    DucoTemplate(
      id = "istighfar",
      title = "Istiqfaar",
      text = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ",
      target = 100,
    ),
    DucoTemplate(
      id = "salawat",
      title = "Salawaat",
      text = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
      target = 100,
    ),
    DucoTemplate(
      id = "tahlil",
      title = "Laa ilaaha illallaah",
      text = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
      target = 100,
    ),
    DucoTemplate(
      id = "morning_dua",
      title = "Ducada Subaxda",
      text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
      target = 3,
    ),
  )

private fun containsArabic(text: String): Boolean {
  return text.any { ch -> ch in '\u0600'..'\u06FF' || ch in '\u0750'..'\u077F' }
}

@Composable
private fun rememberFastFlingBehavior(multiplier: Float = 1.9f): FlingBehavior {
  val baseFling = ScrollableDefaults.flingBehavior()
  return remember(baseFling, multiplier) {
    object : FlingBehavior {
      override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        return with(baseFling) { performFling(initialVelocity * multiplier) }
      }
    }
  }
}

@Composable
fun AddEditDucoScreen(
  editingDucoId: Long?,
  loadDuco: suspend (Long) -> DucoEntity?,
  onSaveDuco: (id: Long?, title: String, text: String, targetCount: Int?) -> Unit,
  onCancel: () -> Unit,
  bottomPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onCancel)

  val view = LocalView.current
  val clipboardManager = LocalClipboardManager.current

  var title by remember(editingDucoId) { mutableStateOf("") }
  var ducoText by remember(editingDucoId) { mutableStateOf("") }
  var hasTarget by remember(editingDucoId) { mutableStateOf(true) }
  var targetInput by remember(editingDucoId) { mutableStateOf("33") }
  var selectedTemplateId by remember(editingDucoId) { mutableStateOf<String?>(null) }

  var titleError by remember { mutableStateOf<String?>(null) }
  var textError by remember { mutableStateOf<String?>(null) }
  var targetError by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(editingDucoId) {
    if (editingDucoId != null) {
      val existing = loadDuco(editingDucoId)
      if (existing != null) {
        title = existing.title
        ducoText = existing.text
        hasTarget = existing.targetCount != null
        targetInput = existing.targetCount?.toString() ?: "33"
      }
    }
  }

  val errEmptyTitle = stringResource(R.string.error_empty_title)
  val errEmptyText = stringResource(R.string.error_empty_text)
  val errInvalidTarget = stringResource(R.string.error_invalid_target)

  val isEditing = editingDucoId != null
  val screenTitle =
    if (isEditing) stringResource(R.string.edit_duco_title)
    else stringResource(R.string.add_duco_title)
  val screenSubtitle =
    if (isEditing) stringResource(R.string.edit_duco_subtitle)
    else stringResource(R.string.add_duco_subtitle)

  val fieldColors =
    OutlinedTextFieldDefaults.colors(
      focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
      unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    )

  val isArabicInput = remember(ducoText) { containsArabic(ducoText) }
  val fastFling = rememberFastFlingBehavior()

  Column(
    modifier =
      modifier
        .fillMaxSize()
        .statusBarsPadding()
        .imePadding()
        .padding(bottom = bottomPadding.calculateBottomPadding()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // Compact Header
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
      ) {
        IconButton(
          onClick = onCancel,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("back_from_add_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.action_cancel),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = screenTitle,
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = screenSubtitle,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      if (title.isNotEmpty() || ducoText.isNotEmpty()) {
        TextButton(
          onClick = {
            title = ""
            ducoText = ""
            selectedTemplateId = null
            titleError = null
            textError = null
            targetError = null
          },
          modifier = Modifier.minimumInteractiveComponentSize().testTag("clear_form_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.DeleteSweep,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = stringResource(R.string.action_clear_form),
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }
    }

    // Fast, smooth LazyColumn body
    LazyColumn(
      modifier = Modifier.weight(1f).fillMaxWidth(),
      flingBehavior = fastFling,
      contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      if (!isEditing) {
        item(key = "templates_section") {
          Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 580.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.section_ready_templates),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
              )
            }

            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              items(items = READY_DUCO_TEMPLATES, key = { it.id }) { template ->
                val isSelected = selectedTemplateId == template.id
                Surface(
                  onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    selectedTemplateId = template.id
                    title = template.title
                    ducoText = template.text
                    hasTarget = true
                    targetInput = template.target.toString()
                    titleError = null
                    textError = null
                    targetError = null
                  },
                  shape = RoundedCornerShape(14.dp),
                  color =
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainerLowest,
                  border =
                    BorderStroke(
                      width = 1.dp,
                      color =
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                    ),
                  modifier =
                    Modifier.minimumInteractiveComponentSize().testTag("template_${template.id}"),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                  ) {
                    Text(
                      text = template.title,
                      style =
                        MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      color =
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Surface(
                      shape = CircleShape,
                      color =
                        if (isSelected) Color.White.copy(alpha = 0.22f)
                        else MaterialTheme.colorScheme.primaryContainer,
                    ) {
                      Text(
                        text = "${template.target}",
                        style =
                          MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum",
                          ),
                        color =
                          if (isSelected) MaterialTheme.colorScheme.onPrimary
                          else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      item(key = "live_preview") {
        LiveDucoPreviewCard(
          title = title,
          text = ducoText,
          targetText = if (hasTarget) targetInput.toIntOrNull()?.takeIf { it > 0 } else null,
          isArabic = isArabicInput,
          modifier = Modifier.fillMaxWidth().widthIn(max = 580.dp),
        )
      }

      item(key = "form_card") {
        Card(
          modifier = Modifier.fillMaxWidth().widthIn(max = 580.dp),
          shape = RoundedCornerShape(24.dp),
          colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            OutlinedTextField(
              value = title,
              onValueChange = {
                if (it.length <= 60) {
                  title = it
                  selectedTemplateId = null
                  titleError = null
                }
              },
              label = { Text(stringResource(R.string.field_duco_name_label)) },
              placeholder = { Text(stringResource(R.string.field_duco_name_hint)) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Outlined.BookmarkBorder,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
              },
              trailingIcon = {
                if (title.isNotEmpty()) {
                  IconButton(onClick = { title = "" }) {
                    Icon(
                      imageVector = Icons.Rounded.Close,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp),
                    )
                  }
                }
              },
              isError = titleError != null,
              supportingText =
                if (titleError != null || title.isNotEmpty()) {
                  {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                      Text(text = titleError ?: "")
                      Text(
                        text = "${title.length}/60",
                        style =
                          MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                      )
                    }
                  }
                } else null,
              singleLine = true,
              shape = RoundedCornerShape(16.dp),
              colors = fieldColors,
              keyboardOptions =
                KeyboardOptions(
                  capitalization = KeyboardCapitalization.Sentences,
                  imeAction = ImeAction.Next,
                ),
              modifier = Modifier.fillMaxWidth().testTag("duco_title_input"),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                  Text(
                    text =
                      if (isArabicInput) stringResource(R.string.badge_arabic_script)
                      else stringResource(R.string.badge_latin_script),
                    style =
                      MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                  )
                }

                Surface(
                  onClick = {
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                      ducoText = clip.trim()
                      textError = null
                    }
                  },
                  shape = RoundedCornerShape(999.dp),
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                  modifier = Modifier.minimumInteractiveComponentSize().testTag("paste_duco_button"),
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.ContentPaste,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      text = stringResource(R.string.action_paste),
                      style =
                        MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.primary,
                    )
                  }
                }
              }

              OutlinedTextField(
                value = ducoText,
                onValueChange = {
                  ducoText = it
                  selectedTemplateId = null
                  textError = null
                },
                label = { Text(stringResource(R.string.field_duco_text_label)) },
                placeholder = { Text(stringResource(R.string.field_duco_text_hint)) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                  )
                },
                trailingIcon = {
                  if (ducoText.isNotEmpty()) {
                    IconButton(onClick = { ducoText = "" }) {
                      Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                      )
                    }
                  }
                },
                isError = textError != null,
                supportingText = textError?.let { { Text(it) } },
                minLines = 2,
                maxLines = 5,
                textStyle =
                  if (isArabicInput) ArabicDucoTextStyle.copy(fontSize = 19.sp, lineHeight = 32.sp)
                  else MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors,
                keyboardOptions =
                  KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().testTag("duco_text_input"),
              )
            }

            HorizontalDivider(
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(R.string.label_target_toggle),
                  style = MaterialTheme.typography.titleSmall,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                if (!hasTarget) {
                  Text(
                    text = stringResource(R.string.label_unlimited_target),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
              Switch(
                checked = hasTarget,
                onCheckedChange = { enabled ->
                  hasTarget = enabled
                  targetError = null
                  if (enabled && targetInput.isBlank()) {
                    targetInput = "33"
                  }
                },
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                  ),
                modifier = Modifier.testTag("target_toggle_switch"),
              )
            }

            AnimatedVisibility(
              visible = hasTarget,
              enter = expandVertically() + fadeIn(),
              exit = shrinkVertically() + fadeOut(),
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Surface(
                    onClick = {
                      val current = targetInput.toIntOrNull() ?: 33
                      targetInput = (current - 1).coerceAtLeast(1).toString()
                      targetError = null
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp).minimumInteractiveComponentSize(),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                      )
                    }
                  }

                  OutlinedTextField(
                    value = targetInput,
                    onValueChange = { raw ->
                      if (raw.all { it.isDigit() } && raw.length <= 6) {
                        targetInput = raw
                        targetError = null
                      }
                    },
                    label = { Text(stringResource(R.string.field_target_label)) },
                    placeholder = { Text(stringResource(R.string.field_target_hint)) },
                    leadingIcon = {
                      Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    },
                    isError = targetError != null,
                    supportingText = targetError?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors,
                    keyboardOptions =
                      KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                      ),
                    modifier = Modifier.weight(1f).testTag("duco_target_input"),
                  )

                  Surface(
                    onClick = {
                      val current = targetInput.toIntOrNull() ?: 0
                      targetInput = (current + 1).coerceAtMost(999999).toString()
                      targetError = null
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp).minimumInteractiveComponentSize(),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                      )
                    }
                  }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  items(items = listOf(7, 33, 99, 100, 1000)) { preset ->
                    val isSelected = targetInput == preset.toString()
                    FilterChip(
                      selected = isSelected,
                      onClick = {
                        targetInput = preset.toString()
                        targetError = null
                      },
                      label = {
                        Text(
                          text = "$preset",
                          style =
                            MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                        )
                      },
                      shape = RoundedCornerShape(999.dp),
                      colors =
                        FilterChipDefaults.filterChipColors(
                          selectedContainerColor = MaterialTheme.colorScheme.primary,
                          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Grounded Action Footer
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.background,
    ) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .widthIn(max = 580.dp)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = onCancel,
          shape = RoundedCornerShape(999.dp),
          modifier =
            Modifier.weight(1f).height(48.dp).minimumInteractiveComponentSize().testTag(
              "cancel_duco_button"
            ),
        ) {
          Text(
            text = stringResource(R.string.action_cancel),
            style = MaterialTheme.typography.labelLarge,
          )
        }

        Button(
          onClick = {
            var valid = true
            if (title.trim().isEmpty()) {
              titleError = errEmptyTitle
              valid = false
            }
            if (ducoText.trim().isEmpty()) {
              textError = errEmptyText
              valid = false
            }
            val parsedTarget =
              if (!hasTarget) {
                null
              } else {
                val num = targetInput.toIntOrNull()
                if (num == null || num <= 0) {
                  targetError = errInvalidTarget
                  valid = false
                  null
                } else {
                  num
                }
              }
            if (valid) {
              onSaveDuco(editingDucoId, title, ducoText, parsedTarget)
            }
          },
          shape = RoundedCornerShape(999.dp),
          modifier =
            Modifier.weight(1.5f).height(48.dp).minimumInteractiveComponentSize().testTag(
              "save_duco_button"
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.action_save),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun LiveDucoPreviewCard(
  title: String,
  text: String,
  targetText: Int?,
  isArabic: Boolean,
  modifier: Modifier = Modifier,
) {
  val displayTitle =
    title.trim().ifEmpty { stringResource(R.string.preview_placeholder_title) }
  val displayText =
    text.trim().ifEmpty { stringResource(R.string.preview_placeholder_text) }
  val isPlaceholderText = text.trim().isEmpty()

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A331C)),
    border = BorderStroke(1.dp, SoftMint.copy(alpha = 0.28f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    modifier = modifier.testTag("live_duco_preview_card"),
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Image(
        painter = painterResource(id = R.drawable.img_duco_card_bg),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.matchParentSize(),
      )
      Box(
        modifier =
          Modifier.matchParentSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(Color(0xD4062815), Color(0xBD09351D))
              )
            )
      )

      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
          Text(
            text = displayTitle,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = displayText,
            style =
              if (isArabic && !isPlaceholderText) {
                ArabicDucoTextStyle.copy(fontSize = 18.sp, lineHeight = 28.sp)
              } else {
                MaterialTheme.typography.bodySmall
              },
            color =
              if (isPlaceholderText) Color.White.copy(alpha = 0.55f)
              else Color.White.copy(alpha = 0.92f),
            textAlign = if (isArabic && !isPlaceholderText) TextAlign.End else TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color.White.copy(alpha = 0.14f),
          border = BorderStroke(1.dp, SoftMint.copy(alpha = 0.35f)),
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          ) {
            Text(
              text = if (targetText != null) "0 / $targetText" else "0",
              style =
                MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontFeatureSettings = "tnum",
                ),
              color = Color.White,
            )
            Text(
              text =
                if (targetText != null) stringResource(R.string.label_preview_target)
                else stringResource(R.string.label_preview_open),
              style = MaterialTheme.typography.labelSmall,
              color = SoftMint,
            )
          }
        }
      }
    }
  }
}
