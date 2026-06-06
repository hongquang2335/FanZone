package com.example.myapplication.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FanZoneTypography,
        content = content
    )
}

