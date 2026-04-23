package com.example.myapplication.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.toVnd
import com.example.myapplication.ui.components.CategoryPill
import com.example.myapplication.ui.components.EventCard
import com.example.myapplication.ui.components.FlashSaleBanner
import com.example.myapplication.ui.components.IconLine
import com.example.myapplication.ui.components.MediaHero
import com.example.myapplication.ui.components.SectionHeader
import com.example.myapplication.ui.components.StateContent
import com.example.myapplication.ui.components.StockChip
import com.example.myapplication.ui.theme.VibeGreen
import com.example.myapplication.ui.theme.VibeGreenDeep
import com.example.myapplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onEventClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    StateContent(state = state) { data ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomeTabs()
                    FlashSaleBanner()
                }
            }
            item {
                SectionHeader("Khám phá")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { CategoryPill("Art", Icons.Default.Palette) }
                    item { CategoryPill("Cinema", Icons.Default.LocalMovies) }
                    item { CategoryPill("Festivals", Icons.Default.Festival) }
                    item { CategoryPill("Music", Icons.Default.MusicNote) }
                    item { CategoryPill("Gaming", Icons.Default.SportsEsports) }
                }
            }
            item {
                SectionHeader("Sự kiện sắp diễn ra", action = "Xem tất cả")
            }
            items(data.filteredEvents.take(4)) { event ->
                EventCard(
                    event = event,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onEventClick(it.id) }
                )
            }
        }
    }
}

@Composable
private fun HomeTabs() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = VibeGreen
        ) {
            Text(
                "Sự kiện",
                modifier = Modifier.padding(vertical = 12.dp),
                color = VibeGreenDeep,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                "Cộng đồng",
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    onBook: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    StateContent(state = state) { data ->
        val event = data.events.firstOrNull { it.id == eventId } ?: data.events.first()
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MediaHero(title = event.title, compact = true)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StockChip(event.status)
                        event.tags.take(2).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    }
                    Text(event.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconLine(Icons.Default.Schedule, event.time)
                    IconLine(Icons.Default.LocationOn, "${event.venue}, ${event.city}")
                    Text(event.description, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = "Yêu thích") }
                        IconButton(onClick = {}) { Icon(Icons.Default.IosShare, contentDescription = "Chia sẻ") }
                        IconButton(onClick = {}) { Icon(Icons.Default.BookmarkBorder, contentDescription = "Lưu") }
                    }
                }
            }
            item {
                SectionHeader("Loại vé", action = "So sánh")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                    event.tiers.forEach { tier ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tier.name, style = MaterialTheme.typography.titleMedium)
                                Text(tier.price.toVnd(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Button(onClick = { onBook(event.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Đặt vé")
                    }
                }
            }
        }
    }
}
