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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun EventDetailScreen(
    event: Event,
    tiers: List<TicketTier>,
    onBack: () -> Unit,
    isUserSignedIn: () -> Boolean,
    onNavigateToLogin: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoginRequired by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppTopBar(title = "FanZone", onBack = onBack) },
            containerColor = MaterialTheme.colorScheme.background
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
private fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x661B1C1C))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 351.dp)
                    .shadow(12.dp, RoundedCornerShape(32.dp))
                    .background(VibeCanvas.copy(alpha = 0.96f), RoundedCornerShape(32.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(VibeGreen.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = VibeGreenDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Yêu cầu đăng nhập",
                    color = VibeText,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Vui lòng đăng nhập để sử dụng\nchức năng đặt vé.",
                    color = Color(0xFF3D4A3F),
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibeSurfaceMuted,
                            contentColor = VibeGreenDark
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Để sau",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onLogin,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(6.dp, CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(VibeGreenDark, VibeGreen)
                                ),
                                shape = CircleShape
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Đăng nhập",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
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

                    // Organizer
                    SectionHeader("Ban tổ chức", null)
                    OrganizerSection(event)
                    
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
    // Tự động lấy thông tin từ event.schedule nếu danh sách biểu diễn rỗng
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
                    // Cột chứa Text ngày giờ (tự co giãn)
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
                    
                    // Button được ưu tiên hiển thị
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
                                // 1. MÔ TẢ VÉ: Bên tay trái (chiếm không gian còn lại)
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

                                // 2. GIÁ TIỀN: Bên tay phải, ưu tiên không gian cho 8 chữ số
                                Text(
                                    text = formatPrice(tier.price), 
                                    color = Evergreen, 
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    modifier = Modifier.widthIn(min = 120.dp), // Đủ cho ~8 chữ số + "đ" không bị xuống dòng
                                    textAlign = TextAlign.End,
                                    softWrap = true // Chỉ xuống dòng khi vượt quá ngưỡng (ví dụ 9 chữ số)
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
        // Org logo – circular with border
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

        // Org name + description
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
