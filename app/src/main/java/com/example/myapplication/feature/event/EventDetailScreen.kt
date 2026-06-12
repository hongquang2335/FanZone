package com.example.myapplication.feature.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.formatPrice
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeSurfaceMuted
import com.example.myapplication.core.designsystem.theme.VibeText
import com.example.myapplication.domain.model.Artist
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PerformanceSchedule
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.core.designsystem.component.LoginRequiredDialog

@Composable
fun EventDetailScreen(
    event: Event,
    tiers: List<TicketTier>,
    onBack: () -> Unit,
    isUserSignedIn: () -> Boolean,
    onNavigateToLogin: () -> Unit,
    onBuyNow: () -> Unit,
    onOpenCommunity: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoginRequired by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppTopBar(title = "FanZone", onBack = onBack) },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            EventDetailBody(
                event = event,
                tiers = tiers,
                onBuyNow = {
                    if (isUserSignedIn()) {
                        onBuyNow()
                    } else {
                        showLoginRequired = true
                    }
                },
                onOpenCommunity = onOpenCommunity,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            )
        }

        if (showLoginRequired) {
            LoginRequiredDialog(
                onDismiss = { showLoginRequired = false },
                onLogin = {
                    showLoginRequired = false
                    onNavigateToLogin()
                }
            )
        }
    }
}

@Composable
private fun EventDetailBody(
    event: Event,
    tiers: List<TicketTier>,
    onBuyNow: () -> Unit,
    onOpenCommunity: () -> Unit,
    modifier: Modifier
) {
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

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    val categories = event.category.ifEmpty { listOf("Khác") }
                    items(categories) { cat ->
                        BadgeItem(cat)
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp
                        ),
                        color = Color(0xFF1E293B)
                    )

                    InfoGrid(event)

                    FanCommunityButton(onClick = onOpenCommunity)

                    SectionHeader("Giới thiệu sự kiện", null)
                    Text(
                        text = event.description,
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
                    )

                    SectionHeader("Nghệ sĩ tham gia", null)
                    ArtistAvatars(event.artists)

                    SectionHeader("Lịch biểu diễn & Đặt vé", null)
                    PerformanceList(event, tiers, onBuyNow)

                    SectionHeader("Ban tổ chức", null)
                    OrganizerSection(event)

                    if (event.resaleTickets.isNotEmpty()) {
                        SectionHeader("Danh sách vé pass lại", "Cập nhật liên tục")
                    }
                }
            }
        }
    }
}

@Composable
private fun FanCommunityButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Tham gia cộng đồng",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Chia sẻ khoảnh khắc về sự kiện",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                val scheduleParts = event.schedule.split("|")
                if (scheduleParts.size >= 2) {
                    Text(
                        text = scheduleParts[0],
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = scheduleParts[1],
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = event.schedule,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
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

private fun abbreviateDayOfWeek(text: String): String {
    return text.replace("Thứ hai", "T2")
        .replace("Thứ ba", "T3")
        .replace("Thứ tư", "T4")
        .replace("Thứ năm", "T5")
        .replace("Thứ sáu", "T6")
        .replace("Thứ bảy", "T7")
        .replace("Chủ nhật", "CN")
}

@Composable
private fun PerformanceList(event: Event, tiers: List<TicketTier>, onBuyNow: () -> Unit) {

    val performances = event.performances.ifEmpty {
        val scheduleParts = event.schedule.split("|")
        listOf(
            PerformanceSchedule(
                id = "default",
                time = scheduleParts.getOrNull(0) ?: "Chưa rõ giờ",
                date = scheduleParts.getOrNull(1) ?: "Chưa rõ ngày",
                ticketTiers = tiers
            )
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
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF94A3B8))
                        }
                        Column {
                            Text(
                                text = abbreviateDayOfWeek(perf.time),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = perf.date,
                                color = Evergreen,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onBuyNow,
                        colors = ButtonDefaults.buttonColors(containerColor = Evergreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Mua vé ngay", maxLines = 1, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tier.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1E293B)
                                    )
                                    if (tier.benefits.isNotEmpty()) {
                                        Text(
                                            text = tier.benefits,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = formatPrice(tier.price),
                                    color = Evergreen,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    modifier = Modifier.widthIn(min = 120.dp),
                                    textAlign = TextAlign.End,
                                    softWrap = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizerSection(event: Event) {
    val displayName = if (event.orgName.isNotEmpty()) event.orgName
                      else if (event.subtitle.isNotEmpty()) event.subtitle
                      else "Ban tổ chức"

    val displayDesc = if (event.orgDescription.isNotEmpty()) event.orgDescription
                      else "Đơn vị tổ chức sự kiện."

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!event.orgLogo.isNullOrEmpty()) {
            AsyncImage(
                model = event.orgLogo,
                contentDescription = displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFF1F5F9), CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Business,
                    contentDescription = displayName,
                    tint = Evergreen,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF1E293B)
            )
            Text(
                text = displayDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )
        }
    }
}
