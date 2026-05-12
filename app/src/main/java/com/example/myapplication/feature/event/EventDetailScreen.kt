package com.example.myapplication.feature.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.formatPrice
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.domain.model.Artist
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PerformanceSchedule
import com.example.myapplication.domain.model.TicketTier

@Composable
fun EventDetailScreen(
    event: Event,
    tiers: List<TicketTier>,
    onBack: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppTopBar(title = "FanZone", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        EventDetailBody(event, tiers, onBuyNow, Modifier.fillMaxSize().padding(innerPadding))
    }
}

@Composable
private fun EventDetailBody(event: Event, tiers: List<TicketTier>, onBuyNow: () -> Unit, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            EventHeroBanner(event)
        }
        
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Categories với thanh cuộn ngang
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nếu category trống, hiển thị nhãn mặc định là "Khác"
                    val categories = event.category.ifEmpty { listOf("Khác") }
                    items(categories) { cat ->
                        BadgeItem(cat)
                    }
                }
                
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Title
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp
                        ),
                        color = Color(0xFF1E293B)
                    )

                    // Info Grid
                    InfoGrid(event)

                    // Description
                    SectionHeader("Giới thiệu sự kiện", null)
                    Text(
                        text = event.description,
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
                    )

                    // Artists
                    SectionHeader("Nghệ sĩ tham gia", null)
                    ArtistAvatars(event.artists)
                    
                    // Performances & Tickets
                    SectionHeader("Lịch biểu diễn & Đặt vé", null)
                    PerformanceList(event, tiers, onBuyNow)

                    // Important Info
                    SectionHeader("Thông tin quan trọng", null)
                    ImportantInfo(event)
                    
                    // Resale
                    if (event.resaleTickets.isNotEmpty()) {
                        SectionHeader("Danh sách vé pass lại", "Cập nhật liên tục")
                    }
                }
            }
        }
    }
}

@Composable
private fun EventHeroBanner(event: Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        if (!event.imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        startY = 400f
                    )
                )
        )
    }
}

@Composable
private fun BadgeItem(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF475569)
        )
    }
}

@Composable
private fun InfoGrid(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Evergreen)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(event.schedule, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("15 Tháng 10, 2024", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("+ 2 ngày khác", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
            }
        }
        
        HorizontalDivider(color = Color(0xFFE2E8F0))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFFEF4444))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(event.venue, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("${event.venue}, ${event.city}", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ArtistAvatars(artists: List<Artist>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(artists) { artist ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.width(80.dp)
            ) {
                AsyncImage(
                    model = artist.image,
                    contentDescription = artist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .border(2.dp, Evergreen, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PerformanceList(event: Event, tiers: List<TicketTier>, onBuyNow: () -> Unit) {
    val performances = event.performances.ifEmpty {
        listOf(
            PerformanceSchedule("1", "14:00 - 16:00, T3", "12 Tháng 05, 2026", tiers),
            PerformanceSchedule("2", "14:00 - 16:00, T3", "19 Tháng 05, 2026", tiers)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        performances.forEachIndexed { index, perf ->
            var expanded by remember { mutableStateOf(index == 0) }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF94A3B8))
                        }
                        Column {
                            Text(perf.time, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text(perf.date, color = Evergreen, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                        }
                    }
                    Button(
                        onClick = onBuyNow,
                        colors = ButtonDefaults.buttonColors(containerColor = Evergreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mua vé ngay")
                    }
                }
                
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Thông tin vé", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        val displayTiers = perf.ticketTiers.ifEmpty { tiers }
                        displayTiers.forEach { tier ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tier.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                                Text(formatPrice(tier.price), color = Evergreen, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportantInfo(event: Event) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val notices = event.notices.ifEmpty {
            listOf(
                "Sự kiện dành cho người từ 18 tuổi trở lên. Vui lòng mang theo CMND/CCCD.",
                "Chất cấm, vật nhọn, thú cưng và các thiết bị ghi hình chuyên nghiệp."
            )
        }
        
        // Age limit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
            }
            Column {
                Text("Quy định độ tuổi", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(notices.firstOrNull() ?: "", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        // Prohibited items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DoNotDisturb, contentDescription = null, tint = Evergreen)
            }
            Column {
                Text("Vật dụng cấm mang vào", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(notices.drop(1).firstOrNull() ?: "", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
