package com.example.myapplication.feature.success

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeStroke
import com.example.myapplication.core.designsystem.theme.VibeText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val SuccessMetaText = Color(0xFF3D4A3F)
private val SuccessTeal = Color(0xFF32BBC5)

@Composable
fun PurchaseSuccessScreen(
    ticket: TicketWalletItem?,
    event: Event?,
    onOpenWallet: () -> Unit,
    onGoHome: () -> Unit,
    onOpenEvent: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var customerName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        customerName = loadCustomerDisplayName()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibeCanvas)
    ) {
        ConfettiBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SuccessHeader()
            Spacer(Modifier.height(32.dp))
            SuccessTicketCard(
                ticket = ticket,
                event = event,
                customerName = customerName.ifBlank { "Khách hàng" }
            )
            Spacer(Modifier.height(32.dp))
            SuccessActions(
                canOpenEvent = ticket?.eventId != null,
                onOpenWallet = onOpenWallet,
                onOpenEvent = { ticket?.eventId?.let(onOpenEvent) },
                onGoHome = onGoHome
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ConfettiBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(start = 40.dp, top = 40.dp)
                .size(32.dp)
                .background(VibeGreen.copy(alpha = 0.50f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 150.dp, end = 70.dp)
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SuccessTeal.copy(alpha = 0.40f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 98.dp, bottom = 160.dp)
                .size(40.dp)
                .background(VibeGreenDark.copy(alpha = 0.30f), CircleShape)
        )
    }
}

@Composable
private fun SuccessHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(40.dp, CircleShape, ambientColor = VibeGreen.copy(alpha = 0.30f))
                .background(VibeGreen.copy(alpha = 0.20f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(VibeGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Text(
            text = "Mua vé thành công!",
            color = VibeGreenDark,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Sẵn sàng cháy hết mình tại sự kiện.",
            color = SuccessMetaText,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SuccessTicketCard(
    ticket: TicketWalletItem?,
    event: Event?,
    customerName: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 448.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(32.dp), ambientColor = Color.Black.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(32.dp),
            color = VibeStroke.copy(alpha = 0.80f),
            border = BorderStroke(1.dp, Color(0x26BCCABC))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                EventHeaderImage(event)
                Column(
                    modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = event?.title ?: ticket?.eventTitle.orEmpty(),
                        color = VibeText,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    DetailField("THỜI GIAN", event.successSchedule(ticket))
                    DetailField("ĐỊA ĐIỂM", event.successVenue(ticket))
                    DetailField("KHÁCH HÀNG", customerName)
                }
            }
        }

        Notch(modifier = Modifier.align(Alignment.CenterStart).offset(x = (-16).dp, y = 56.dp))
        Notch(modifier = Modifier.align(Alignment.CenterEnd).offset(x = 16.dp, y = 56.dp))
    }
}

@Composable
private fun EventHeaderImage(event: Event?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(Color(0xFFDCD9D9))
    ) {
        when {
            !event?.imageUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = event?.imageUrl,
                    contentDescription = event?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            event != null && event.imageRes != 0 -> {
                Image(
                    painter = painterResource(event.imageRes),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, VibeStroke.copy(alpha = 0.92f))
                    )
                )
        )
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = SuccessMetaText,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.6.sp
        )
        Text(
            text = value,
            color = VibeText,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Notch(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(VibeCanvas, CircleShape)
            .border(1.dp, Color.Black.copy(alpha = 0.03f), CircleShape)
    )
}

@Composable
private fun SuccessActions(
    canOpenEvent: Boolean,
    onOpenWallet: () -> Unit,
    onOpenEvent: () -> Unit,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 448.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onOpenWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(15.dp, CircleShape, ambientColor = VibeGreen.copy(alpha = 0.25f)),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(VibeGreenDark, VibeGreen)), CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Xem vé của tôi", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        SecondarySuccessButton(
            text = "Xem chi tiết sự kiện",
            enabled = canOpenEvent,
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = VibeText, modifier = Modifier.size(20.dp)) },
            onClick = onOpenEvent
        )
        SecondarySuccessButton(
            text = "Về trang chủ",
            enabled = true,
            icon = null,
            onClick = onGoHome
        )
    }
}

@Composable
private fun SecondarySuccessButton(
    text: String,
    enabled: Boolean,
    icon: (@Composable () -> Unit)?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color(0x26BCCABC)),
        colors = ButtonDefaults.buttonColors(
            containerColor = VibeStroke,
            contentColor = VibeText,
            disabledContainerColor = VibeStroke.copy(alpha = 0.55f),
            disabledContentColor = SuccessMetaText
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.size(8.dp))
        }
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

private suspend fun loadCustomerDisplayName(): String {
    val user = FirebaseAuth.getInstance().currentUser ?: return ""
    val fallback = user.displayName?.takeIf { it.isNotBlank() } ?: user.email.orEmpty()
    return try {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("users")
            .whereEqualTo("uid", user.uid)
            .limit(1)
            .get()
            .await()
        val fromQuery = query.documents.firstOrNull()?.getString("displayName")
        if (!fromQuery.isNullOrBlank()) return fromQuery

        val directDoc = firestore.collection("users").document(user.uid).get().await()
        directDoc.getString("displayName")?.takeIf { it.isNotBlank() } ?: fallback
    } catch (_: Exception) {
        fallback
    }
}

private fun Event?.successSchedule(ticket: TicketWalletItem?): String {
    if (this == null) return ticket?.schedule?.replace("|", ", ").orEmpty()
    val start = startTime.toVietnamDate() ?: return schedule.replace("|", ", ")
    val end = endTime.toVietnamDate()
    val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    val timeFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN")).apply {
        timeZone = vietnamTimeZone
    }
    val dateFormat = SimpleDateFormat("dd 'tháng' M, yyyy", Locale("vi", "VN")).apply {
        timeZone = vietnamTimeZone
    }
    val timeRange = if (end != null) {
        "${timeFormat.format(start)}-${timeFormat.format(end)}"
    } else {
        timeFormat.format(start)
    }
    return "$timeRange, ${dateFormat.format(start)}"
}

private fun Event?.successVenue(ticket: TicketWalletItem?): String {
    if (this == null) return ticket?.venue.orEmpty()
    return venue.ifBlank { city }
}

private fun String.toVietnamDate(): java.util.Date? {
    if (isBlank()) return null
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).parse(trim())
    } catch (_: Exception) {
        null
    }
}
