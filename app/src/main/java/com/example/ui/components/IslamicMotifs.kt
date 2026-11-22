package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.rubElHizbWatermark(
  color: Color,
  alpha: Float = 0.045f,
): Modifier = drawBehind {
  val side = min(size.width, size.height) * 0.56f
  val center = Offset(size.width - side * 0.35f, size.height * 0.5f)
  val tint = color.copy(alpha = alpha)
  val stroke = Stroke(width = 2.dp.toPx())

  drawRect(
    color = tint,
    topLeft = Offset(center.x - side / 2f, center.y - side / 2f),
    size = Size(side, side),
    style = stroke,
  )
  rotate(degrees = 45f, pivot = center) {
    drawRect(
      color = tint,
      topLeft = Offset(center.x - side / 2f, center.y - side / 2f),
      size = Size(side, side),
      style = stroke,
    )
  }
  drawCircle(
    color = tint,
    radius = side * 0.24f,
    center = center,
    style = stroke,
  )
}

@Composable
fun EmptyDucoIllustration(
  contentDescriptionText: String,
  modifier: Modifier = Modifier,
) {
  val primary = MaterialTheme.colorScheme.primary
  val container = MaterialTheme.colorScheme.primaryContainer
  val secondary = MaterialTheme.colorScheme.secondary
  val surfaceCard = MaterialTheme.colorScheme.surfaceContainerLowest

  Canvas(
    modifier =
      modifier
        .size(180.dp)
        .semantics { contentDescription = contentDescriptionText }
  ) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    // Soft halo background
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(container.copy(alpha = 0.75f), container.copy(alpha = 0f)),
          center = Offset(cx, cy),
          radius = w * 0.48f,
        ),
      radius = w * 0.48f,
      center = Offset(cx, cy),
    )

    // Subtle 8-pointed Rub el Hizb behind arch
    val starSide = w * 0.46f
    drawRect(
      color = primary.copy(alpha = 0.12f),
      topLeft = Offset(cx - starSide / 2f, cy - starSide / 2f),
      size = Size(starSide, starSide),
      style = Stroke(width = 2.dp.toPx()),
    )
    rotate(degrees = 45f, pivot = Offset(cx, cy)) {
      drawRect(
        color = primary.copy(alpha = 0.12f),
        topLeft = Offset(cx - starSide / 2f, cy - starSide / 2f),
        size = Size(starSide, starSide),
        style = Stroke(width = 2.dp.toPx()),
      )
    }

    // Islamic pointed arch card
    val archPath =
      Path().apply {
        moveTo(cx - w * 0.26f, cy + h * 0.26f)
        lineTo(cx - w * 0.26f, cy - h * 0.04f)
        quadraticTo(cx - w * 0.26f, cy - h * 0.22f, cx, cy - h * 0.31f)
        quadraticTo(cx + w * 0.26f, cy - h * 0.22f, cx + w * 0.26f, cy - h * 0.04f)
        lineTo(cx + w * 0.26f, cy + h * 0.26f)
        close()
      }
    drawPath(path = archPath, color = surfaceCard)
    drawPath(
      path = archPath,
      color = primary.copy(alpha = 0.35f),
      style = Stroke(width = 2.dp.toPx()),
    )

    // Stylized open book / Duco lines inside arch
    val lineColor = primary.copy(alpha = 0.65f)
    drawLine(
      color = lineColor,
      start = Offset(cx - w * 0.13f, cy - h * 0.02f),
      end = Offset(cx + w * 0.13f, cy - h * 0.02f),
      strokeWidth = 3.dp.toPx(),
      cap = StrokeCap.Round,
    )
    drawLine(
      color = secondary.copy(alpha = 0.45f),
      start = Offset(cx - w * 0.10f, cy + h * 0.06f),
      end = Offset(cx + w * 0.10f, cy + h * 0.06f),
      strokeWidth = 2.5.dp.toPx(),
      cap = StrokeCap.Round,
    )
    drawLine(
      color = secondary.copy(alpha = 0.35f),
      start = Offset(cx - w * 0.08f, cy + h * 0.13f),
      end = Offset(cx + w * 0.08f, cy + h * 0.13f),
      strokeWidth = 2.5.dp.toPx(),
      cap = StrokeCap.Round,
    )

    // Crescent accent at top of arch
    drawCircle(
      color = primary,
      radius = w * 0.055f,
      center = Offset(cx, cy - h * 0.16f),
    )
    drawCircle(
      color = surfaceCard,
      radius = w * 0.045f,
      center = Offset(cx + w * 0.022f, cy - h * 0.172f),
    )

    // Tasbih beads along the base
    val beadRadius = w * 0.026f
    val beadOffsets = listOf(-0.16f, -0.08f, 0f, 0.08f, 0.16f)
    beadOffsets.forEachIndexed { idx, xFrac ->
      drawCircle(
        color = if (idx == 2) primary else primary.copy(alpha = 0.45f),
        radius = if (idx == 2) beadRadius * 1.25f else beadRadius,
        center = Offset(cx + w * xFrac, cy + h * 0.34f),
      )
    }
  }
}

@Composable
fun SplashEmblem(modifier: Modifier = Modifier) {
  val primary = MaterialTheme.colorScheme.primary
  val container = MaterialTheme.colorScheme.primaryContainer
  val onContainer = MaterialTheme.colorScheme.onPrimaryContainer

  Canvas(modifier = modifier.size(116.dp)) {
    val s = size.minDimension
    val cx = s / 2f
    val cy = s / 2f
    val square = s * 0.62f

    drawRoundRect(
      color = container,
      topLeft = Offset(cx - square / 2f, cy - square / 2f),
      size = Size(square, square),
      cornerRadius = CornerRadius(12.dp.toPx()),
    )
    rotate(degrees = 45f, pivot = Offset(cx, cy)) {
      drawRoundRect(
        color = primary,
        topLeft = Offset(cx - square / 2f, cy - square / 2f),
        size = Size(square, square),
        cornerRadius = CornerRadius(12.dp.toPx()),
      )
    }
    drawCircle(
      color = container,
      radius = s * 0.21f,
      center = Offset(cx, cy),
    )
    drawCircle(
      color = onContainer,
      radius = s * 0.11f,
      center = Offset(cx, cy),
    )
  }
}
