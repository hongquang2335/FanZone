package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.VibeGreen
import com.example.myapplication.ui.theme.VibeGreenDeep

@Composable
fun MediaHero(
    title: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (compact) 1.86f else 1.41f)
            .clip(RoundedCornerShape(8.dp))
            .background(eventBrush(title))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd),
            tint = Color.White.copy(alpha = 0.82f)
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.BottomStart),
            color = Color.White,
            style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FlashSaleBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = VibeGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(254.dp).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(eventBrush("flash sale")),
                contentAlignment = Alignment.Center
            ) {
                Text("50%", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(shape = RoundedCornerShape(4.dp), color = VibeGreenDeep.copy(alpha = 0.16f)) {
                    Text(
                        "GIẢM CHỚP NHOÁNG",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = VibeGreenDeep,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    "Flash Sale\nCuối Tuần!",
                    color = VibeGreenDeep,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Giảm giá lên đến 50% cho các sự kiện hot nhất tuần này.",
                    color = VibeGreenDeep.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(shape = RoundedCornerShape(8.dp), color = VibeGreenDeep) {
                    Text(
                        "Xem ngay",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
