package com.example.myapplication.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeStroke
import com.example.myapplication.core.designsystem.theme.VibeText
import com.example.myapplication.core.designsystem.theme.VibeTextMuted
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import kotlinx.coroutines.delay

private val homeCategories = listOf(
    "Nhạc sống",
    "Sân khấu & Nghệ thuật",
    "Thể thao",
    "Hội thảo & Workshop",
    "Tham quan & Trải nghiệm",
    "Khác"
)

@Composable
fun HomeScreen(
    events: List<Event>,
    posts: List<CommunityPost>,
    onOpenEvent: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onViewCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val postCounts = remember(posts) {
        posts.mapNotNull(CommunityPost::eventId).groupingBy { it }.eachCount()
    }
    val featuredEvents = remember(events, postCounts) {
        events
            .shuffled()
            .sortedByDescending { postCounts[it.id] ?: 0 }
            .take(5)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibeCanvas)
    ) {
        HomeHeader(onNavigateToSearch = onNavigateToSearch)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            if (events.isNotEmpty()) {
                item {
                    RotatingEventBanner(
                        events = events,
                        onOpenEvent = onOpenEvent,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                FeaturedEventsSection(
                    events = featuredEvents,
                    onOpenEvent = onOpenEvent
                )
            }

            homeCategories.forEach { category ->
                item(key = category) {
                    val categoryEvents = events
                        .filter { event ->
                            event.category.any { it.equals(category, ignoreCase = true) } ||
                                (category == "Khác" && event.category.isEmpty())
                        }
                        .take(2)

                    CategorySection(
                        title = category,
                        events = categoryEvents,
                        onOpenEvent = onOpenEvent,
                        onViewAll = { onViewCategory(category) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(onNavigateToSearch: () -> Unit) {
    Surface(
        color = VibeCanvas.copy(alpha = 0.96f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = VibeGreenDark,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "FanZone",
                    color = VibeGreenDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }

            IconButton(onClick = onNavigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = VibeText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RotatingEventBanner(
    events: List<Event>,
    onOpenEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentEventId by remember(events) { mutableStateOf(events.first().id) }
    val currentEvent = events.firstOrNull { it.id == currentEventId } ?: events.first()

    LaunchedEffect(events.map(Event::id)) {
        while (true) {
            delay(10_000)
            val candidates = events.filterNot { it.id == currentEventId }
            currentEventId = (candidates.ifEmpty { events }).random().id
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .aspectRatio(4f / 5f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        EventImage(
            event = currentEvent,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Black.copy(alpha = 0.22f),
                        1f to Color.Black.copy(alpha = 0.86f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = currentEvent.title,
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.9).sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = { onOpenEvent(currentEvent.id) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = Modifier.background(
                    brush = Brush.linearGradient(listOf(VibeGreenDark, VibeGreen)),
                    shape = CircleShape
                )
            ) {
                Text("Mua vé ngay", fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturedEventsSection(
    events: List<Event>,
    onOpenEvent: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Sự kiện nổi bật nhất",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = VibeText,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.6).sp
        )

        if (events.isEmpty()) {
            Text(
                text = "Chưa có sự kiện nổi bật.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = VibeTextMuted
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events, key = Event::id) { event ->
                    val rank = events.indexOf(event) + 1
                    FeaturedEventCard(
                        event = event,
                        rank = rank,
                        onClick = { onOpenEvent(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedEventCard(
    event: Event,
    rank: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(VibeStroke)
        ) {
            EventImage(event = event, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Text(
                text = rank.toString(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 2.dp),
                color = Color.White,
                fontSize = 96.sp,
                lineHeight = 88.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-4.8).sp
            )
        }
        Text(
            text = event.title,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = VibeText,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategorySection(
    title: String,
    events: List<Event>,
    onOpenEvent: (String) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = VibeText,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp
            )
            TextButton(
                onClick = onViewAll,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Xem tất cả",
                    color = VibeGreenDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (events.isEmpty()) {
            Text(
                text = "Chưa có sự kiện trong thể loại này.",
                color = VibeTextMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                events.forEach { event ->
                    CategoryEventCard(
                        event = event,
                        onClick = { onOpenEvent(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventImage(
                event = event,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(VibeStroke)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = event.title,
                    color = VibeText,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = VibeTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.schedule.ifBlank { "Đang cập nhật" },
                        color = VibeTextMuted,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val location = event.venue.ifBlank { event.city }
                if (location.isNotBlank()) {
                    Text(
                        text = location,
                        color = VibeGreenDark,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun EventImage(
    event: Event,
    modifier: Modifier = Modifier
) {
    when {
        !event.imageUrl.isNullOrBlank() -> {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }

        event.imageRes != 0 -> {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }

        else -> {
            Box(
                modifier = modifier.background(
                    Brush.linearGradient(listOf(VibeGreenDark, VibeGreen))
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
