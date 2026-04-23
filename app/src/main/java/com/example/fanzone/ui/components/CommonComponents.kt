package com.example.fanzone.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fanzone.ui.theme.Secondary

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Surface(
        color = Secondary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Secondary.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = "VERIFIED",
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Secondary
        )
    }
}
