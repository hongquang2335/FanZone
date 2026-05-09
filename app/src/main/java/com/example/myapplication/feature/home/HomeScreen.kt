package com.example.myapplication.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.Event
import com.example.myapplication.core.designsystem.component.CategoryPill
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.EventCard
import com.example.myapplication.core.designsystem.component.FlowCategoryRow
import com.example.myapplication.core.designsystem.component.HeroBanner
import com.example.myapplication.core.designsystem.component.HomeSwitchChip
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.theme.SoftText

@Composable
fun HomeScreen(
    event: Event,
    events: List<Event>,
    categories: List<Category>,
    onOpenEvent: (String) -> Unit,
    onOpenCommunity: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircleAvatar()
                        androidx.compose.foundation.layout.Column {
                            Text("FanZone", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("Dong fan thong minh cho su kien", color = SoftText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Tim kiem")
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeSwitchChip("Su kien", true, Modifier.weight(1f))
                    HomeSwitchChip("Cong dong", false, Modifier.weight(1f), onClick = onOpenCommunity)
                }
            }
            item { HeroBanner(event = event, onOpenEvent = { onOpenEvent(event.id) }) }
            item { SectionHeader("Kham pha", "Khoi dong theo mood cua ban") }
            item {
                if (isExpanded) {
                    FlowCategoryRow(categories.map { it.emoji to it.label })
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(categories) { category -> CategoryPill(category.emoji, category.label) }
                    }
                }
            }
            item { SectionHeader("Su kien sap dien ra", "Xem tat ca") }
            if (isExpanded) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        events.forEach { featured ->
                            Box(modifier = Modifier.weight(1f)) {
                                EventCard(featured, Modifier.fillMaxWidth(), onOpen = { onOpenEvent(featured.id) })
                            }
                        }
                    }
                }
            } else {
                items(events) { featured ->
                    EventCard(featured, Modifier.fillMaxWidth(), onOpen = { onOpenEvent(featured.id) })
                }
            }
        }
    }
}

