package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myapplication.model.EventStatus
import com.example.myapplication.model.FanEvent
import com.example.myapplication.ui.theme.VibeGreen
import com.example.myapplication.ui.theme.VibeGreenDark
import com.example.myapplication.ui.theme.VibeGreenDeep
import com.example.myapplication.ui.theme.VibeMint
import com.example.myapplication.ui.theme.VibeSurfaceMuted

@Composable
fun EventCard(
    event: FanEvent,
    modifier: Modifier = Modifier,
    onClick: (FanEvent) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick(event) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .background(eventBrush(event.title))
            ) {
                DateBadge(
                    month = if (event.time.contains("11")) "THANG 11" else "THANG 10",
                    day = when {
                        event.time.contains("05") -> "05"
                        event.time.contains("12") -> "12"
                        else -> "28"
                    },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconLine(Icons.Default.Schedule, event.time)
                IconLine(Icons.Default.LocationOn, "${event.venue}, ${event.city}")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InterestedStack()
                    Surface(shape = RoundedCornerShape(8.dp), color = VibeGreen) {
                        Text(
                            "Quan tâm",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            color = VibeGreenDeep,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateBadge(month: String, day: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(month, color = VibeGreenDark, style = MaterialTheme.typography.labelSmall)
            Text(day, color = VibeGreenDeep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InterestedStack() {
    Box(modifier = Modifier.width(84.dp).height(32.dp)) {
        AvatarBubble("A", Modifier.align(Alignment.CenterStart))
        AvatarBubble("B", Modifier.align(Alignment.CenterStart).offset(x = 24.dp))
        Surface(
            modifier = Modifier.align(Alignment.CenterStart).offset(x = 48.dp).size(32.dp),
            shape = CircleShape,
            color = VibeSurfaceMuted,
            border = BorderStroke(2.dp, Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("+99", style = MaterialTheme.typography.labelMedium, color = VibeGreenDeep)
            }
        }
    }
}

@Composable
private fun AvatarBubble(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(eventBrush(label))
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun StockChip(status: EventStatus) {
    val (text, color, container) = when (status) {
        EventStatus.Available -> Triple("Còn vé", VibeGreenDark, VibeMint)
        EventStatus.LowStock -> Triple("Sắp hết", Color(0xFF78350F), MaterialTheme.colorScheme.tertiaryContainer)
        EventStatus.SoldOut -> Triple("Hết vé", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
    }
    Surface(shape = RoundedCornerShape(50), color = container, contentColor = color) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
