package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PremPinkDarkTheme,
    onPrimary = Color(0xFF5A0025),
    primaryContainer = PremPinkDark,
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = PremPurpleDarkTheme,
    onSecondary = Color(0xFF4A0072),
    secondaryContainer = PremPurpleDark,
    onSecondaryContainer = Color(0xFFF3E5F5),
    tertiary = PremRoseSoft,
    background = PremBackgroundDark,
    surface = PremSurfaceDark,
    surfaceVariant = PremSurfaceVariantDark,
    onBackground = PremOnBackgroundDark,
    onSurface = PremOnSurfaceDark,
    outline = PremOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PremPinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PremBlush,
    onPrimaryContainer = PremPinkDark,
    secondary = PremPurpleSecondary,
    onSecondary = Color.White,
    secondaryContainer = PremLavender,
    onSecondaryContainer = PremPurpleDark,
    tertiary = PremRoseTertiary,
    background = PremBackgroundLight,
    surface = PremSurfaceLight,
    surfaceVariant = PremSurfaceVariantLight,
    onBackground = PremOnBackgroundLight,
    onSurface = PremOnSurfaceLight,
    outline = PremOutlineLight
)

@Composable
fun PremTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PremTheme(darkTheme = darkTheme, content = content)
}
