package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.SplashEmblem

@Composable
fun SplashOverlay(
  visible: Boolean,
  modifier: Modifier = Modifier,
) {
  val emblemScale = remember { Animatable(0.72f) }
  val contentAlpha = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    emblemScale.animateTo(
      targetValue = 1f,
      animationSpec =
        spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessLow,
        ),
    )
  }

  LaunchedEffect(Unit) {
    contentAlpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 420))
  }

  AnimatedVisibility(
    visible = visible,
    exit = fadeOut(animationSpec = tween(durationMillis = 360)),
    modifier = modifier,
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(
            brush =
              Brush.verticalGradient(
                colors =
                  listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    MaterialTheme.colorScheme.background,
                  )
              )
          ),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.graphicsLayer { alpha = contentAlpha.value },
      ) {
        SplashEmblem(
          modifier =
            Modifier.graphicsLayer {
              scaleX = emblemScale.value
              scaleY = emblemScale.value
            }
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text(
          text = stringResource(R.string.app_name),
          style = MaterialTheme.typography.headlineLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = stringResource(R.string.app_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}
