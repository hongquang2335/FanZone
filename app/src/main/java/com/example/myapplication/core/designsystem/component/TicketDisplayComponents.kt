package com.example.myapplication.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.core.designsystem.theme.Danger
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.EvergreenDark
import com.example.myapplication.core.designsystem.theme.Ink
import com.example.myapplication.core.designsystem.theme.LavenderWash
import com.example.myapplication.core.designsystem.theme.MintWash
import com.example.myapplication.core.designsystem.theme.PeachWash
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.designsystem.theme.SurfaceCard
import com.example.myapplication.core.designsystem.theme.Warning
import com.example.myapplication.core.util.formatVnd
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.TicketStatus
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus
import com.example.myapplication.domain.model.MyTicket
import com.example.myapplication.domain.model.Order
import com.example.myapplication.domain.model.OrderItem


@Composable
fun OrderTicketCard(
    order: Order,
    onClick: () -> Unit
) {
    val backgroundColor = Color.White
    val ticketShape = RoundedCornerShape(16.dp)

    // Extracting day and month from startTime "20:00, Thu Bay 15/06/2024"
    // For mock purposes, we'll simplify. In real app, use a formatter.
    val dateParts = order.startTime.split(" ").last().split("/")
    val day = dateParts.getOrNull(0) ?: "01"
    val month = dateParts.getOrNull(1) ?: "01"
    val year = dateParts.getOrNull(2) ?: "2026"

    Card(
        shape = ticketShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 4.dp)
            .border(1.dp, SoftLine, ticketShape)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side: Date (Vertical)
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    day,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    "Tháng $month",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )
                Text(
                    year,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )
            }

            // Dotted Vertical Line with cut-outs
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        var currentY = 0f
                        while (currentY < size.height) {
                            moveTo(0f, currentY)
                            lineTo(0f, currentY + 10f)
                            currentY += 20f
                        }
                    }
                    drawPath(path, color = SoftLine, style = Stroke(width = 2f))
                }
            }

            // Right Side: Event Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    order.eventTitle.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    maxLines = 2
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Evergreen,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            "Thành công",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = LavenderWash,
                        border = BorderStroke(1.dp, SoftLine),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            "Vé điện tử",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink,
                            fontSize = 10.sp
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    InfoLine(Icons.Default.ConfirmationNumber, "Mã đơn hàng: ${order.bookingId}")
                    InfoLine(Icons.Default.Schedule, order.startTime)
                    InfoLine(Icons.Default.LocationOn, order.venue)
                }
            }
        }
    }
}

@Composable
fun InfoLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = SoftText)
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = SoftText,
            maxLines = 1,
            fontSize = 11.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailBottomSheet(
    tickets: List<MyTicket>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val groupedTickets = tickets.groupBy { it.ticketType }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Chi tiết vé của bạn",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            groupedTickets.forEach { (type, ticketsInGroup) ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Evergreen,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Text(
                            "$type (${ticketsInGroup.size} vé)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink
                        )
                    }
                    
                    ticketsInGroup.forEach { ticket ->
                        IndividualTicketItem(ticket)
                    }
                }
            }
        }
    }
}

@Composable
fun IndividualTicketItem(ticket: MyTicket) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, SoftLine),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(ticket.ticketType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(ticket.zoneName, color = SoftText)
                }
                StatusChip(ticket.status)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LavenderWash)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(120.dp), tint = Ink)
                    Text("MA CHECK-IN: ${ticket.qrCodeData}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("ID Ve", style = MaterialTheme.typography.bodySmall, color = SoftText)
                    Text(ticket.ticketId, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gia mua", style = MaterialTheme.typography.bodySmall, color = SoftText)
                    Text(ticket.purchasePrice.formatVnd(), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: com.example.myapplication.domain.model.TicketStatus) {
    val color = when (status) {
        com.example.myapplication.domain.model.TicketStatus.UPCOMING -> Evergreen
        com.example.myapplication.domain.model.TicketStatus.RESELLING -> Warning
        com.example.myapplication.domain.model.TicketStatus.COMPLETED -> SoftText
        com.example.myapplication.domain.model.TicketStatus.CANCELLED -> Danger
        else -> SoftText
    }

    val bgColor = when (status) {
        com.example.myapplication.domain.model.TicketStatus.UPCOMING -> MintWash
        com.example.myapplication.domain.model.TicketStatus.RESELLING -> PeachWash
        com.example.myapplication.domain.model.TicketStatus.COMPLETED -> LavenderWash
        else -> PeachWash
    }

    Surface(
        shape = CircleShape,
        color = bgColor,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RecommendedEventCard(event: Event, onOpenEvent: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(200.dp)
            .clickable { onOpenEvent(event.id) }
    ) {
        Column {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(event.title, maxLines = 1, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(event.city, color = SoftText, style = MaterialTheme.typography.labelSmall)
                Text("Tu 500.000d", color = Evergreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun TicketCard(item: TicketWalletItem, onOpenEvent: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.clickable { onOpenEvent(item.eventId) }
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(item.eventTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(item.schedule, color = SoftText)
                    Text(item.venue, color = SoftText)
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MintWash),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = Evergreen, modifier = Modifier.size(34.dp))
                }
            }
            Surface(shape = RoundedCornerShape(20.dp), color = LavenderWash, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(item.seatLabel, fontWeight = FontWeight.SemiBold)
                    Text("Ma QR: ${item.qrCode}", style = MaterialTheme.typography.bodyMedium, color = SoftText)
                }
            }
            Button(onClick = { onOpenEvent(item.eventId) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                Text("Xem chi tiet su kien")
            }
        }
    }
}

@Composable
fun ProfileActionRow(title: String, subtitle: String, clickable: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.clickable(enabled = clickable, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = SoftText)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = SoftText)
        }
    }
}

@Composable
fun MessageBubble(author: String, body: String, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isMine) MintWash else Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(author, fontWeight = FontWeight.SemiBold, color = if (isMine) Evergreen else Ink)
                Text(body)
            }
        }
    }
}

