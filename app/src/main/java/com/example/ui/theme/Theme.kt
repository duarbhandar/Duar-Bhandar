package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = EmeraldDarkOnPrimary,
    primaryContainer = EmeraldDarkPrimaryContainer,
    onPrimaryContainer = EmeraldDarkOnPrimaryContainer,
    secondary = EmeraldDarkSecondary,
    onSecondary = EmeraldDarkOnSecondary,
    secondaryContainer = EmeraldDarkSecondaryContainer,
    onSecondaryContainer = EmeraldDarkOnSecondaryContainer,
    tertiary = EmeraldDarkTertiary,
    onTertiary = EmeraldDarkOnTertiary,
    tertiaryContainer = EmeraldDarkTertiaryContainer,
    onTertiaryContainer = EmeraldDarkOnTertiaryContainer,
    background = EmeraldDarkBackground,
    onBackground = EmeraldDarkOnBackground,
    surface = EmeraldDarkSurface,
    onSurface = EmeraldDarkOnSurface,
    surfaceVariant = EmeraldDarkSurfaceVariant,
    onSurfaceVariant = EmeraldDarkOnSurfaceVariant
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldLightPrimary,
    onPrimary = EmeraldLightOnPrimary,
    primaryContainer = EmeraldLightPrimaryContainer,
    onPrimaryContainer = EmeraldLightOnPrimaryContainer,
    secondary = EmeraldLightSecondary,
    onSecondary = EmeraldLightOnSecondary,
    secondaryContainer = EmeraldLightSecondaryContainer,
    onSecondaryContainer = EmeraldLightOnSecondaryContainer,
    tertiary = EmeraldLightTertiary,
    onTertiary = EmeraldLightOnTertiary,
    tertiaryContainer = EmeraldLightTertiaryContainer,
    onTertiaryContainer = EmeraldLightOnTertiaryContainer,
    background = EmeraldLightBackground,
    onBackground = EmeraldLightOnBackground,
    surface = EmeraldLightSurface,
    onSurface = EmeraldLightOnSurface,
    surfaceVariant = EmeraldLightSurfaceVariant,
    onSurfaceVariant = EmeraldLightOnSurfaceVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+ (we disable by default for brand consistency)
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
