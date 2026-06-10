package com.example.myapplication.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Evergreen,
    onPrimary = MintWash,
    background = SurfaceCard,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    secondary = PeachWash,
    tertiary = LavenderWash,
    outline = SoftLine,
    error = Danger
)

private val DarkColors = darkColorScheme(
    primary = Evergreen,
    onPrimary = MintWash,
    background = Ink,
    onBackground = SurfaceCard,
    surface = Ink,
    onSurface = SurfaceCard,
    outline = SoftText,
    error = Danger
)

@Composable
fun FanZoneTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FanZoneTypography,
        content = content
    )
}

private val ElectricStageColors = darkColorScheme(
    primary = Color(0xFF006D3D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2DC275),
    onPrimaryContainer = Color.Black,
    background = Color(0xFF0A090C),
    onBackground = Color.White,
    surface = Color(0xFF201C28),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF373045),
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    outline = Color(0xFF5F5E5E).copy(alpha = 0.15f),
    error = Danger,
    surfaceContainerLowest = Color(0xFF0F0E13),
    surfaceContainerLow = Color(0xFF16121A),
    surfaceContainer = Color(0xFF201C28),
    surfaceContainerHigh = Color(0xFF2B2536),
    surfaceContainerHighest = Color(0xFF373045)
)

@Composable
fun ElectricStageTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElectricStageColors,
        typography = FanZoneTypography,
        content = content
    )
}

