package com.example.myapplication.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.Danger
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.EvergreenDark
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.util.AppStrings
import com.example.myapplication.domain.model.Event

@Composable
fun HeroBanner(event: Event, onOpenEvent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF24B66B), EvergreenDark, Color(0xFF053B27))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 210.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AssistChip(onClick = {}, label = { Text(AppStrings.Community.FLASH_SALE_TITLE) })
            Text(AppStrings.Community.FLASH_SALE_SUB, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(AppStrings.Community.FLASH_SALE_BODY, color = Color.White.copy(alpha = 0.88f))
            OutlinedButton(
                onClick = onOpenEvent,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.36f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(AppStrings.Community.VIEW_NOW, color = Color.White)
            }
        }
        if (event.imageUrl != null) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        } else {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        }
    }
}

@Composable
fun EventCard(event: Event, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column {
            Box {
                if (event.imageUrl != null) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(event.imageRes),
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("THÁNG 10", style = MaterialTheme.typography.labelLarge, color = Danger)
                        Text("28", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(event.title, style = MaterialTheme.typography.titleLarge)
                MetaRow(Icons.Default.Schedule, event.schedule)
                MetaRow(Icons.Default.LocationOn, "${event.venue}, ${event.city}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.Community.INTERESTED_COUNT, color = SoftText, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onOpen, shape = RoundedCornerShape(20.dp)) {
                        Text(AppStrings.Community.INTERESTED)
                    }
                }
            }
        }
    }
}

@Composable
fun GradientPanel(title: String, body: String, action: String, onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF052C1D), EvergreenDark, Color(0xFF4AD38E))))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 220.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(body, color = Color.White.copy(alpha = 0.88f))
            Button(onClick = onAction, shape = RoundedCornerShape(20.dp)) {
                Text(action)
            }
        }
    }
}
