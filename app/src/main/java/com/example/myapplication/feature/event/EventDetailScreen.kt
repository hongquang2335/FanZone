package com.example.myapplication.feature.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.ArtistRow
import com.example.myapplication.core.designsystem.component.BookingFooter
import com.example.myapplication.core.designsystem.component.InfoPanel
import com.example.myapplication.core.designsystem.component.NoticeCard
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.TimelineColumn
import com.example.myapplication.core.designsystem.component.VenueMapCard
import com.example.myapplication.core.designsystem.component.formatPrice
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.designsystem.theme.SurfaceCard

@Composable
fun EventDetailScreen(
    event: Event,
    tiers: List<TicketTier>,
    onBack: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 720.dp
        val minPrice = tiers.minOfOrNull { it.price }?.let(::formatPrice)

        Scaffold(
            topBar = { AppTopBar(title = event.title, onBack = onBack) },
            bottomBar = { if (!isExpanded) BookingFooter("Dat ve ngay", minPrice, onClick = onBuyNow) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    EventDetailBody(event, tiers, Modifier.weight(1.4f))
                    SideBookingPanel(event, tiers, Modifier.weight(1f), onBuyNow)
                }
            } else {
                EventDetailBody(event, tiers, Modifier.fillMaxSize().padding(innerPadding))
            }
        }
    }
}

@Composable
private fun EventDetailBody(event: Event, tiers: List<TicketTier>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(30.dp))
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Nhac song") })
                AssistChip(onClick = {}, label = { Text("Mua he") })
                AssistChip(onClick = {}, label = { Text("Tu ${formatPrice(tiers.minOfOrNull { it.price } ?: 0)}") })
            }
        }
        item { Text(event.title, style = MaterialTheme.typography.headlineMedium) }
        item {
            InfoPanel(
                rows = listOf(
                    Icons.Default.Schedule to event.schedule,
                    Icons.Default.LocationOn to "${event.venue}, ${event.city}"
                )
            )
        }
        item {
            SectionHeader("Gioi thieu su kien", null)
            Text(event.description, color = SoftText, style = MaterialTheme.typography.bodyLarge)
        }
        item {
            SectionHeader("Nghe si tham gia", null)
            ArtistRow(event.artists)
        }
        item {
            SectionHeader("Lich trinh su kien", null)
            TimelineColumn(event.timeline)
        }
        item {
            SectionHeader("So do khu vuc", "Xem chi tiet")
            VenueMapCard()
        }
        item {
            SectionHeader("Thong tin quan trong", null)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                event.notices.forEach { note -> NoticeCard(note) }
            }
        }
    }
}

@Composable
private fun SideBookingPanel(
    event: Event,
    tiers: List<TicketTier>,
    modifier: Modifier,
    onBuyNow: () -> Unit
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White), shape = RoundedCornerShape(30.dp)) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Booking panel", style = MaterialTheme.typography.titleLarge)
            Text(event.subtitle, color = SoftText)
            tiers.forEach { tier ->
                Surface(shape = RoundedCornerShape(22.dp), color = SurfaceCard) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(tier.name, fontWeight = FontWeight.SemiBold)
                        Text(tier.benefits, color = SoftText, style = MaterialTheme.typography.bodyMedium)
                        Text(formatPrice(tier.price), color = Evergreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Button(onClick = onBuyNow, modifier = Modifier.fillMaxWidth()) {
                Text("Di toi dat ve")
            }
        }
    }
}

