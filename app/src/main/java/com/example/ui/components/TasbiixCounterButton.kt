package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private data class TapBurst(val id: Long)

@Composable
fun TasbiixCounterButton(
  count: Int,
  targetCount: Int?,
  isDarkTheme: Boolean,
  enabled: Boolean,
  onTap: () -> Unit,
  modifier: Modifier = Modifier,
  outerSize: Dp = 186.dp,
) {
  val view = LocalView.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val pressScale by
    animateFloatAsState(
      targetValue = if (isPressed && enabled) 0.94f else 1f,
      animationSpec =
        spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMedium,
        ),
      label = "tasbiix_press_scale",
    )

  val targetFraction =
    if (targetCount != null && targetCount > 0) {
      (count.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
    } else {
      0f
    }

  val animatedProgress by
    animateFloatAsState(
      targetValue = targetFraction,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "tasbiix_ring_progress",
    )

  val rippleProgress = remember { Animatable(0f) }
  val bursts = remember { mutableStateListOf<TapBurst>() }

  LaunchedEffect(count) {
    if (count > 0) {
      rippleProgress.snapTo(0f)
      rippleProgress.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 420))
    }
  }

  val primary = MaterialTheme.colorScheme.primary
  val containerBg =
    if (isDarkTheme) MaterialTheme.colorScheme.surfaceContainerLowest
    else MaterialTheme.colorScheme.primaryContainer
  val innerGlow =
    if (isDarkTheme) primary.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.55f)
  val rimColor = if (isDarkTheme) primary.copy(alpha = 0.55f) else primary.copy(alpha = 0.35f)
  val cdButton = stringResource(R.string.cd_tasbiix_button)
  val innerSize = outerSize * 0.80f

  Box(
    modifier = modifier.size(outerSize),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val rp = rippleProgress.value
      if (rp > 0f && rp < 1f) {
        val baseRadius = size.minDimension * 0.40f
        val maxRadius = size.minDimension * 0.50f
        val currentRadius = baseRadius + (maxRadius - baseRadius) * rp
        drawCircle(
          color = primary.copy(alpha = (1f - rp) * 0.38f),
          radius = currentRadius,
          style = Stroke(width = (3.dp.toPx()) * (1f - rp * 0.5f)),
        )
      }

      val ringStroke = 7.dp.toPx()
      val ringDiameter = size.minDimension - ringStroke - 10.dp.toPx()
      val topLeftOffset =
        Offset(
          (size.width - ringDiameter) / 2f,
          (size.height - ringDiameter) / 2f,
        )
      val ringSize = Size(ringDiameter, ringDiameter)

      if (targetCount != null && targetCount > 0) {
        drawArc(
          color = primary.copy(alpha = 0.15f),
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeftOffset,
          size = ringSize,
          style = Stroke(width = ringStroke, cap = StrokeCap.Round),
        )
        if (animatedProgress > 0f) {
          drawArc(
            color = primary,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = topLeftOffset,
            size = ringSize,
            style = Stroke(width = ringStroke, cap = StrokeCap.Round),
          )
        }
      } else {
        drawArc(
          color = primary.copy(alpha = 0.14f),
          startAngle = 0f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeftOffset,
          size = ringSize,
          style = Stroke(width = 2.5.dp.toPx()),
        )
      }
    }

    Box(
      modifier =
        Modifier.size(innerSize)
          .graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
          }
          .shadow(
            elevation = if (isDarkTheme) 12.dp else 8.dp,
            shape = CircleShape,
            ambientColor = primary.copy(alpha = 0.35f),
            spotColor = primary.copy(alpha = 0.35f),
          )
          .clip(CircleShape)
          .background(brush = Brush.radialGradient(colors = listOf(innerGlow, containerBg)))
          .border(width = 2.dp, color = rimColor, shape = CircleShape)
          .clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true, color = primary),
            enabled = enabled,
            role = Role.Button,
          ) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            bursts.add(TapBurst(id = System.nanoTime()))
            onTap()
          }
          .testTag("tasbiix_counter_button")
          .semantics { contentDescription = cdButton },
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val side = size.minDimension * 0.56f
        val left = (size.width - side) / 2f
        val top = (size.height - side) / 2f
        val stroke = Stroke(width = 1.5.dp.toPx())
        val motifColor = primary.copy(alpha = if (isDarkTheme) 0.14f else 0.10f)
        drawRect(
          color = motifColor,
          topLeft = Offset(left, top),
          size = Size(side, side),
          style = stroke,
        )
        rotate(degrees = 45f, pivot = center) {
          drawRect(
            color = motifColor,
            topLeft = Offset(left, top),
            size = Size(side, side),
            style = stroke,
          )
        }
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(12.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.AutoAwesome,
          contentDescription = null,
          tint = primary,
          modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "+1",
          style =
            MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFeatureSettings = "tnum",
            ),
          color = if (isDarkTheme) primary else MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    }

    bursts.forEach { burst ->
      FloatingPlusOne(
        key = burst.id,
        color = primary,
        onFinished = { bursts.remove(burst) },
      )
    }
  }
}

@Composable
private fun FloatingPlusOne(
  key: Long,
  color: Color,
  onFinished: () -> Unit,
) {
  val animProgress = remember(key) { Animatable(0f) }

  LaunchedEffect(key) {
    animProgress.animateTo(1f, animationSpec = tween(durationMillis = 520))
    delay(20)
    onFinished()
  }

  val progress = animProgress.value
  val yOffsetPx = (-64f * progress).roundToInt()
  val alpha = (1f - progress).coerceIn(0f, 1f)
  val scale = 0.9f + 0.35f * (1f - progress)

  Text(
    text = "+1",
    style =
      MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
      ),
    color = color.copy(alpha = alpha),
    modifier =
      Modifier.offset { IntOffset(x = 0, y = -82 + yOffsetPx) }
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        },
  )
}
