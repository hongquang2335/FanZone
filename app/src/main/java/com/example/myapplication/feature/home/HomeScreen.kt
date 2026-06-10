package com.example.myapplication.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.Event
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.EventCard
import com.example.myapplication.core.designsystem.component.HeroBanner
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.theme.SoftText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    event: Event,
    events: List<Event>,
    categories: List<Category>, // Giữ lại tạm thời để file NavHost không bị lỗi
    onOpenEvent: (String) -> Unit,
    onOpenCommunity: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onSelectCategory: (String) -> Unit, // Giữ lại tạm thời để file NavHost không bị lỗi
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 720.dp
        val gridColumns = if (maxWidth >= 1024.dp) 3 else 2

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- HEADER ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        CircleAvatar()
                        Column {
                            Text(
                                text = "Event-Hub",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Khám phá sự kiện quanh bạn",
                                color = SoftText,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Mở tìm kiếm",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // --- BANNER SLIDE ---
            item { HeroBanner(event = event, onOpenEvent = { onOpenEvent(event.id) }) }

            // ĐÃ XÓA MỤC DANH MỤC (CATEGORIES) TẠI ĐÂY

            // --- SỰ KIỆN NỔI BẬT (TRENDING) ---
            item { SectionHeader("Sự kiện nổi bật (Trending)", "Hot nhất") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(events.take(3)) { trendingEvent ->
                        Box(modifier = Modifier.width(280.dp)) {
                            EventCard(trendingEvent, Modifier.fillMaxWidth(), onOpen = { onOpenEvent(trendingEvent.id) })
                        }
                    }
                }
            }

            // --- SỰ KIỆN SẮP DIỄN RA ---
            item { SectionHeader("Sự kiện sắp diễn ra", "Xem tất cả") }
            if (isExpanded) {
                items(events.chunked(gridColumns)) { rowEvents ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowEvents.forEach { featured ->
                            Box(modifier = Modifier.weight(1f)) {
                                EventCard(featured, Modifier.fillMaxWidth(), onOpen = { onOpenEvent(featured.id) })
                            }
                        }
                        repeat(gridColumns - rowEvents.size) {
                            Spacer(modifier = Modifier.weight(1f))
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