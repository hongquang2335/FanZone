package com.example.myapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
internal fun eventBrush(seed: String): Brush {
    val palettes = listOf(
        listOf(Color(0xFF004927), Color(0xFF2DC275), Color(0xFF72FCA9)),
        listOf(Color(0xFF1B1C1C), Color(0xFF006D3D), Color(0xFFF59E0B)),
        listOf(Color(0xFF1976D2), Color(0xFF2DC275), Color(0xFFD1FAE5)),
        listOf(Color(0xFF7B1FA2), Color(0xFFC2185B), Color(0xFFFDE68A))
    )
    return Brush.linearGradient(palettes[kotlin.math.abs(seed.hashCode()) % palettes.size])
}
