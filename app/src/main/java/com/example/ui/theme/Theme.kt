package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme =
  lightColorScheme(
    primary = DeepEmerald,
    onPrimary = Color.White,
    primaryContainer = SoftSage,
    onPrimaryContainer = DeepestEmerald,
    secondary = ForestAccent,
    onSecondary = Color.White,
    secondaryContainer = SoftSageContainer,
    onSecondaryContainer = DeepEmerald,
    tertiary = VibrantEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD9F9E3),
    onTertiaryContainer = DeepestEmerald,
    background = WarmOffWhite,
    onBackground = DarkCharcoal,
    surface = WarmOffWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceHighLight,
    onSurfaceVariant = MutedForestText,
    surfaceContainerLowest = PristineWhite,
    surfaceContainerLow = SurfaceLowLight,
    surfaceContainer = SurfaceMidLight,
    surfaceContainerHigh = SurfaceHighLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = RadiantMint,
    onPrimary = OnPrimaryDark,
    primaryContainer = DeepEmerald,
    onPrimaryContainer = SoftMint,
    secondary = SoftMint,
    onSecondary = OnPrimaryDark,
    secondaryContainer = DarkEmeraldBorder,
    onSecondaryContainer = SoftSage,
    tertiary = Color(0xFF4AE176),
    onTertiary = OnPrimaryDark,
    tertiaryContainer = Color(0xFF005321),
    onTertiaryContainer = Color(0xFF6BFF8F),
    background = DeepForestBg,
    onBackground = CrispWhite,
    surface = DeepForestBg,
    onSurface = CrispWhite,
    surfaceVariant = SurfaceHighDark,
    onSurfaceVariant = MutedSlate,
    surfaceContainerLowest = MossCharcoal,
    surfaceContainerLow = SurfaceLowDark,
    surfaceContainer = MossCharcoal,
    surfaceContainerHigh = SurfaceHighDark,
    outline = OutlineDark,
    outlineVariant = DarkEmeraldBorder,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
  )

val AppShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun DucoCounterTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        WindowCompat.getInsetsController(window, view).apply {
          isAppearanceLightStatusBars = !darkTheme
          isAppearanceLightNavigationBars = !darkTheme
        }
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    shapes = AppShapes,
    content = content,
  )
}
