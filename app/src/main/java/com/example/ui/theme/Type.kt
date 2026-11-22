package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFamily =
  FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
  )

val ArabicScriptFontFamily = FontFamily(Font(R.font.amiri, FontWeight.Normal))

val CounterDisplayTextStyle =
  TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 56.sp,
    lineHeight = 64.sp,
    letterSpacing = (-1.5).sp,
    fontFeatureSettings = "tnum",
  )

val ArabicDucoTextStyle =
  TextStyle(
    fontFamily = ArabicScriptFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 44.sp,
  )

val AppTypography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1.5).sp,
        fontFeatureSettings = "tnum",
      ),
    headlineLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.25).sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp,
      ),
  )
