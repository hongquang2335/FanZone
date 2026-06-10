package com.example.myapplication.feature.tickets

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeGreenDeep
import com.example.myapplication.core.designsystem.theme.VibeStroke
import com.example.myapplication.core.designsystem.theme.VibeText
import com.example.myapplication.core.designsystem.theme.VibeTextMuted
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun TicketWalletRoute(
    modifier: Modifier = Modifier,
    viewModel: TicketWalletViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketWalletScreen(
        uiState = uiState,
        onSelectTab = viewModel::selectTab,
        modifier = modifier
    )
}

@Composable
fun TicketWalletScreen(
    uiState: TicketWalletUiState,
    onSelectTab: (TicketTimeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    val selectedGroup = uiState.eventGroups.firstOrNull {
        it.eventId == selectedEventId
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibeCanvas)
            .statusBarsPadding()
    ) {
        WalletHeader()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 20.dp,
                end = 24.dp,
                bottom = 112.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                TicketTabs(
                    selectedTab = uiState.selectedTab,
                    onSelectTab = onSelectTab
                )
            }

            when {
                uiState.isLoading -> item { LoadingTickets() }
                uiState.error != null -> item { TicketLoadError(uiState.error) }
                uiState.visibleGroups.isEmpty() -> item {
                    EmptyTickets(tab = uiState.selectedTab)
                }
                else -> items(
                    items = uiState.visibleGroups,
                    key = OwnedEventTickets::eventId
                ) { group ->
                    OwnedEventCard(
                        group = group,
                        onClick = { selectedEventId = group.eventId }
                    )
                }
            }
        }
    }

    if (selectedGroup != null) {
        TicketQrDialog(
            group = selectedGroup,
            onDismiss = { selectedEventId = null }
        )
    }
}

@Composable
private fun WalletHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(VibeCanvas)
            .shadow(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Vé của tôi",
            color = VibeText,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TicketTabs(
    selectedTab: TicketTimeTab,
    onSelectTab: (TicketTimeTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TicketTabButton(
            text = "Sắp diễn ra",
            selected = selectedTab == TicketTimeTab.UPCOMING,
            onClick = { onSelectTab(TicketTimeTab.UPCOMING) }
        )
        Spacer(Modifier.width(8.dp))
        TicketTabButton(
            text = "Đã kết thúc",
            selected = selectedTab == TicketTimeTab.ENDED,
            onClick = { onSelectTab(TicketTimeTab.ENDED) }
        )
    }
}

@Composable
private fun TicketTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) VibeGreen else Color.White,
        border = BorderStroke(1.dp, Color(0x4DBCCABC))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            color = if (selected) VibeGreenDeep else VibeTextMuted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OwnedEventCard(
    group: OwnedEventTickets,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            VenueBlock(group)
            EventBlock(group)
            SessionDetails(group)
            DashedDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng tiền thanh toán",
                    color = VibeTextMuted,
                    fontSize = 14.sp
                )
                Text(
                    text = group.totalPrice.toVnd(),
                    color = VibeGreenDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VenueBlock(group: OwnedEventTickets) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = VibeGreen,
            modifier = Modifier.size(36.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = group.venueName.ifBlank { "Địa điểm đang cập nhật" },
                color = VibeText,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold
            )
            if (group.address.isNotBlank()) {
                Text(
                    text = group.address,
                    color = VibeTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EventBlock(group: OwnedEventTickets) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .height(144.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEAE7E7)),
            contentAlignment = Alignment.Center
        ) {
            if (!group.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = group.imageUrl,
                    contentDescription = group.eventTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = VibeGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Text(
            text = group.eventTitle.ifBlank { "Sự kiện" }.uppercase(),
            modifier = Modifier.weight(1f),
            color = VibeText,
            fontSize = 18.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SessionDetails(group: OwnedEventTickets) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailValue(
                label = "Ngày diễn ra",
                value = group.startTime.formatDate(),
                modifier = Modifier.weight(1f)
            )
            DetailValue(
                label = "Bắt đầu lúc",
                value = group.startTime.formatTime(),
                modifier = Modifier.weight(1f)
            )
            DetailValue(
                label = "Số lượng vé",
                value = "${group.ticketCount} vé",
                modifier = Modifier.weight(1f)
            )
        }
        DetailValue(
            label = "Số ghế",
            value = group.seatNames.joinToString(", ").ifBlank { "Đang cập nhật" },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DetailValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = VibeTextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
        Text(
            text = value,
            color = VibeText,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
    ) {
        drawLine(
            color = VibeStroke,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        )
    }
}

@Composable
private fun EmptyTickets(tab: TicketTimeTab) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 96.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .background(Color(0xFFE5F8EE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = null,
                tint = VibeGreen,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = if (tab == TicketTimeTab.UPCOMING) {
                "Bạn chưa có vé sắp diễn ra"
            } else {
                "Bạn chưa có vé đã kết thúc"
            },
            color = VibeText,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (tab == TicketTimeTab.UPCOMING) {
                "Những vé bạn mua sẽ xuất hiện tại đây."
            } else {
                "Các sự kiện đã tham gia sẽ được lưu tại đây."
            },
            color = VibeTextMuted,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingTickets() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = VibeGreen)
    }
}

@Composable
private fun TicketLoadError(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            color = VibeTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TicketQrDialog(
    group: OwnedEventTickets,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.58f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 20.dp, end = 12.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Vé của bạn",
                                color = VibeText,
                                fontSize = 24.sp,
                                lineHeight = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = group.eventTitle,
                                color = VibeTextMuted,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        androidx.compose.material3.IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng chi tiết vé",
                                tint = VibeText
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 28.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(
                            items = group.tickets,
                            key = OwnedTicket::ticketId
                        ) { ticket ->
                            TicketQrCard(
                                ticket = ticket,
                                position = group.tickets.indexOf(ticket) + 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketQrCard(
    ticket: OwnedTicket,
    position: Int
) {
    val payload = remember(ticket) {
        JSONObject()
            .put("ticketId", ticket.ticketId)
            .put("eventId", ticket.eventId)
            .put("owner", ticket.ownerId)
            .toString()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF7F7F7),
        border = BorderStroke(1.dp, VibeStroke)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Vé $position",
                        color = VibeTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "Ghế ${ticket.seatName}",
                        color = VibeText,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = VibeGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            GeneratedQrCode(
                payload = payload,
                contentDescription = "Mã QR cho ghế ${ticket.seatName}"
            )
            Spacer(Modifier.height(18.dp))

            Text(
                text = "Mã vé",
                color = VibeTextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Text(
                text = ticket.ticketId,
                color = VibeText,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GeneratedQrCode(
    payload: String,
    contentDescription: String
) {
    val qrImage by produceState<ImageBitmap?>(initialValue = null, key1 = payload) {
        value = withContext(Dispatchers.Default) {
            generateQrBitmap(payload, QR_BITMAP_SIZE).asImageBitmap()
        }
    }

    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (qrImage == null) {
            CircularProgressIndicator(
                color = VibeGreen,
                modifier = Modifier.size(36.dp)
            )
        } else {
            Image(
                bitmap = qrImage!!,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun generateQrBitmap(
    payload: String,
    size: Int
): Bitmap {
    val hints = mapOf(
        EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name(),
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 2
    )
    val matrix = QRCodeWriter().encode(
        payload,
        BarcodeFormat.QR_CODE,
        size,
        size,
        hints
    )
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        val rowOffset = y * size
        for (x in 0 until size) {
            pixels[rowOffset + x] = if (matrix[x, y]) {
                android.graphics.Color.BLACK
            } else {
                android.graphics.Color.WHITE
            }
        }
    }
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}

private val VietnamTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
private const val QR_BITMAP_SIZE = 768

private fun java.util.Date?.formatDate(): String {
    if (this == null) return "Đang cập nhật"
    return SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
        timeZone = VietnamTimeZone
    }.format(this)
}

private fun java.util.Date?.formatTime(): String {
    if (this == null) return "Đang cập nhật"
    return SimpleDateFormat("HH:mm", Locale("vi", "VN")).apply {
        timeZone = VietnamTimeZone
    }.format(this)
}

private fun Int.toVnd(): String =
    "${NumberFormat.getNumberInstance(Locale.US).format(this)} VND"
