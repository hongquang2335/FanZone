package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = VibeGreen,
    onPrimary = VibeGreenDeep,
    primaryContainer = VibeGreenDark,
    onPrimaryContainer = Color.White,
    secondary = VibeStrokeStrong,
    onSecondary = VibeText,
    secondaryContainer = Color(0xFF22332A),
    onSecondaryContainer = VibeMint,
    tertiary = VibeAmber,
    onTertiary = Color(0xFF3F2A00),
    background = Color(0xFF111412),
    onBackground = Color(0xFFF2F4F0),
    surface = Color(0xFF1A1D1B),
    onSurface = Color(0xFFF2F4F0),
    surfaceVariant = Color(0xFF27302A),
    onSurfaceVariant = Color(0xFFC9D1C8),
    outline = Color(0xFF48564D),
    error = VibeError
)

private val LightColorScheme = lightColorScheme(
    primary = VibeGreen,
    onPrimary = VibeGreenDeep,
    primaryContainer = VibeMint,
    onPrimaryContainer = VibeGreenDeep,
    secondary = VibeGreenDark,
    onSecondary = Color.White,
    secondaryContainer = VibeMintSoft,
    onSecondaryContainer = VibeGreenDeep,
    tertiary = VibeAmber,
    onTertiary = Color(0xFF3F2A00),
    tertiaryContainer = VibeAmberSoft,
    onTertiaryContainer = Color(0xFF78350F),
    background = VibeCanvas,
    onBackground = VibeText,
    surface = VibeSurface,
    onSurface = VibeText,
    surfaceVariant = VibeSurfaceMuted,
    onSurfaceVariant = VibeTextMuted,
    outline = VibeStroke,
    outlineVariant = VibeStroke,
    error = VibeError
)

private val VibeShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = VibeShapes,
        content = content
    )
}
