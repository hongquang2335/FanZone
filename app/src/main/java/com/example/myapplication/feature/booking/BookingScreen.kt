package com.example.myapplication.feature.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.designsystem.component.formatPrice
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.EvergreenDark
import com.example.myapplication.core.designsystem.theme.Ink
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

private val VipSeatColor = Color(0xFF2DC275)
private val StandardSeatColor = Color(0xFFFCA311)
private val UnavailableSeatColor = Color(0xFFE4E2E1)
private val UnavailableTextColor = Color(0xFFA3A1A0)
private val BookingBackground = Color(0xFFFCF9F8)
private val SelectedCardColor = Color(0xFF18C98B)

private val SeatSize = 28.dp
private val SeatHorizontalGap = 4.dp
private val SeatVerticalGap = 6.dp
private val SeatMapEdgePadding = 16.dp
private const val MinimumSeatMapScale = 0.85f
private const val MaximumSeatMapScale = 2.25f

@Composable
fun BookingScreen(
    event: Event,
    seats: List<EventSeat>,
    selectedSeatIds: Set<String>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onToggleSeat: (String) -> Unit,
    onRemoveSeat: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedSeats = remember(seats, selectedSeatIds) {
        seats.filter { it.id in selectedSeatIds }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { BookingTopBar(event.title, onBack) },
        containerColor = BookingBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 150.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SeatLegend()
                Spacer(Modifier.height(18.dp))
                StageIndicator()
                Spacer(Modifier.height(8.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Evergreen)
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    seats.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sự kiện này chưa có sơ đồ ghế.",
                                color = SoftText
                            )
                        }
                    }

                    else -> {
                        ZoomableSeatMap(
                            seats = seats,
                            selectedSeatIds = selectedSeatIds,
                            selectionLimitReached = selectedSeatIds.size >= MAX_SELECTED_SEATS,
                            onToggleSeat = onToggleSeat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            SelectedSeatsPanel(
                selectedSeats = selectedSeats,
                onRemoveSeat = onRemoveSeat,
                onContinue = onContinue,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingTopBar(title: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Ink
            )
        },
        navigationIcon = {
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp),
                shape = CircleShape,
                color = UnavailableSeatColor
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Ink
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BookingBackground
        )
    )
}

@Composable
private fun SeatLegend() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF6F3F2)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("VIP", VipSeatColor)
            LegendItem("Thường", StandardSeatColor)
            LegendItem("Hết chỗ", UnavailableSeatColor, SoftText)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color, textColor: Color = Ink) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color)
        )
        Text(label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StageIndicator() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "MÀN HÌNH",
            color = SoftText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 20.dp)
        ) {
            val inset = size.width * 0.08f
            drawArc(
                color = VipSeatColor.copy(alpha = 0.12f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(inset, 10.dp.toPx()),
                size = Size(size.width - inset * 2, 66.dp.toPx())
            )
            drawArc(
                color = VipSeatColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(inset, 10.dp.toPx()),
                size = Size(size.width - inset * 2, 66.dp.toPx()),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun ZoomableSeatMap(
    seats: List<EventSeat>,
    selectedSeatIds: Set<String>,
    selectionLimitReached: Boolean,
    onToggleSeat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowNames = remember(seats) {
        seats.map { seatRow(it.seatId) }
            .distinct()
            .sortedBy(::rowIndex)
    }
    val seatsByRow = remember(seats) {
        seats.groupBy { seatRow(it.seatId) }
            .mapValues { (_, rowSeats) ->
                rowSeats.associateBy { seatColumn(it.seatId) }
            }
    }
    val maxColumn = remember(seats) {
        seats.maxOfOrNull { seatColumn(it.seatId) }?.coerceAtLeast(1) ?: 1
    }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val seatGridWidth = SeatSize * maxColumn + SeatHorizontalGap * (maxColumn - 1)
        val seatGridHeight = SeatSize * rowNames.size + SeatVerticalGap * (rowNames.size - 1)
        val contentWidth = seatGridWidth + SeatMapEdgePadding * 2
        val contentHeight = seatGridHeight + SeatMapEdgePadding * 2
        val seatPitch = SeatSize + SeatHorizontalGap
        val wholeVisibleColumns = floor(
            (
                (maxWidth - SeatMapEdgePadding * 2 + SeatHorizontalGap) /
                    seatPitch
                ).toDouble()
        ).toInt().coerceIn(1, maxColumn)
        val viewportWidth = if (contentWidth <= maxWidth) {
            contentWidth
        } else {
            seatPitch * wholeVisibleColumns -
                SeatHorizontalGap +
                SeatMapEdgePadding * 2
        }

        BoxWithConstraints(
            modifier = Modifier
                .width(viewportWidth)
                .fillMaxHeight()
                .align(Alignment.Center)
                .clipToBounds()
        ) {
            val viewportWidthPx = with(density) { maxWidth.toPx() }
            val viewportHeightPx = with(density) { maxHeight.toPx() }
            val contentWidthPx = with(density) { contentWidth.toPx() }
            val contentHeightPx = with(density) { contentHeight.toPx() }
            val seatPitchXPx = with(density) { (SeatSize + SeatHorizontalGap).toPx() }
            val seatPitchYPx = with(density) { (SeatSize + SeatVerticalGap).toPx() }
            val edgePaddingPx = with(density) { SeatMapEdgePadding.toPx() }
            val minimumScale = min(
                1f,
                maxOf(
                    MinimumSeatMapScale,
                    min(
                        viewportWidthPx / contentWidthPx,
                        viewportHeightPx / contentHeightPx
                    )
                )
            )

            var scale by remember(rowNames, maxColumn) { mutableFloatStateOf(1f) }
            var translation by remember(rowNames, maxColumn) { mutableStateOf(Offset.Zero) }

            fun clampTranslation(candidate: Offset, targetScale: Float): Offset {
                fun clampAxis(value: Float, contentSize: Float, viewportSize: Float): Float {
                    val scaledContentSize = contentSize * targetScale
                    return if (scaledContentSize <= viewportSize) {
                        (viewportSize - scaledContentSize) / 2f
                    } else {
                        value.coerceIn(viewportSize - scaledContentSize, 0f)
                    }
                }

                return Offset(
                    x = clampAxis(candidate.x, contentWidthPx, viewportWidthPx),
                    y = clampAxis(candidate.y, contentHeightPx, viewportHeightPx)
                )
            }

            LaunchedEffect(viewportWidthPx, viewportHeightPx) {
                translation = clampTranslation(translation, scale)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(
                        minimumScale,
                        contentWidthPx,
                        contentHeightPx,
                        viewportWidthPx,
                        viewportHeightPx
                    ) {
                        awaitEachGesture {
                            var accumulatedZoom = 1f
                            var accumulatedPan = Offset.Zero
                            var transformStarted = false

                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.changes.none { it.pressed }) break

                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()
                                val centroid = event.calculateCentroid(useCurrent = true)
                                if (!centroid.x.isFinite() || !centroid.y.isFinite()) continue

                                if (!transformStarted) {
                                    accumulatedZoom *= zoomChange
                                    accumulatedPan += panChange
                                    val zoomMotion = abs(1f - accumulatedZoom) *
                                        event.calculateCentroidSize(useCurrent = false)
                                    val panMotion = accumulatedPan.getDistance()
                                    transformStarted =
                                        zoomMotion > viewConfiguration.touchSlop ||
                                        panMotion > viewConfiguration.touchSlop
                                }

                                if (transformStarted) {
                                    val nextScale = (scale * zoomChange)
                                        .coerceIn(minimumScale, MaximumSeatMapScale)
                                    val effectiveZoom = nextScale / scale
                                    val zoomAroundCentroid =
                                        translation * effectiveZoom +
                                        centroid * (1f - effectiveZoom)
                                    translation = clampTranslation(
                                        zoomAroundCentroid + panChange,
                                        nextScale
                                    )
                                    scale = nextScale

                                    event.changes.forEach { change ->
                                        if (change.positionChanged()) change.consume()
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.TopStart
            ) {
                rowNames.forEachIndexed { rowIndex, rowName ->
                    val rowSeats = seatsByRow[rowName].orEmpty()
                    for (column in 1..maxColumn) {
                        val seat = rowSeats[column] ?: continue
                        val selected = seat.id in selectedSeatIds
                        val seatX = translation.x +
                            (edgePaddingPx + (column - 1) * seatPitchXPx) * scale
                        val seatY = translation.y +
                            (edgePaddingPx + rowIndex * seatPitchYPx) * scale

                        SeatCell(
                            seat = seat,
                            selected = selected,
                            dimmed = selectionLimitReached && !selected,
                            size = SeatSize,
                            onClick = { onToggleSeat(seat.id) },
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        seatX.roundToInt(),
                                        seatY.roundToInt()
                                    )
                                }
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    transformOrigin = TransformOrigin(0f, 0f)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatCell(
    seat: EventSeat,
    selected: Boolean,
    dimmed: Boolean,
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val availableColor = if (seat.isVip) VipSeatColor else StandardSeatColor
    val background = when {
        selected -> Color.White
        seat.isAvailable -> availableColor
        else -> UnavailableSeatColor
    }
    val contentColor = when {
        selected -> availableColor
        seat.isAvailable -> Color.White
        else -> UnavailableTextColor
    }
    val enabled = seat.isAvailable && (!dimmed || selected)

    Box(
        modifier = modifier
            .size(size)
            .alpha(if (dimmed) 0.25f else 1f)
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .then(
                if (selected) Modifier.border(1.dp, availableColor, RoundedCornerShape(6.dp))
                else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "${seat.seatId} đã chọn",
                modifier = Modifier.size(size * 0.62f),
                tint = contentColor
            )
        } else {
            Text(
                text = seat.seatId,
                color = contentColor,
                fontSize = 8.sp,
                lineHeight = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SelectedSeatsPanel(
    selectedSeats: List<EventSeat>,
    onRemoveSeat: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val total = selectedSeats.sumOf(EventSeat::price)

    LaunchedEffect(selectedSeats.isEmpty()) {
        if (selectedSeats.isEmpty()) expanded = false
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 14.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = selectedSeats.isNotEmpty()) {
                        expanded = !expanded
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Danh sách ghế đã chọn (${selectedSeats.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Ink
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = if (expanded) "Thu gọn" else "Mở danh sách",
                        tint = SoftText
                    )
                }
                Text(
                    text = "Chỉnh sửa vị trí ghế bên trên hoặc tại đây",
                    color = SoftText,
                    fontSize = 13.sp
                )
            }

            AnimatedVisibility(visible = expanded && selectedSeats.isNotEmpty()) {
                SelectedSeatGrid(
                    seats = selectedSeats,
                    onRemoveSeat = onRemoveSeat
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, SoftLine.copy(alpha = 0.7f)))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tổng tiền vé", color = SoftText, fontSize = 14.sp)
                    Text(
                        text = formatPrice(total),
                        color = EvergreenDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Button(
                    onClick = onContinue,
                    enabled = selectedSeats.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EvergreenDark,
                        disabledContainerColor = SoftLine
                    ),
                    modifier = Modifier.height(54.dp)
                ) {
                    Text(
                        text = "Tiếp tục",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedSeatGrid(
    seats: List<EventSeat>,
    onRemoveSeat: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        seats.chunked(3).forEach { rowSeats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowSeats.forEach { seat ->
                    SelectedSeatCard(
                        seat = seat,
                        onRemoveSeat = onRemoveSeat,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowSeats.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SelectedSeatCard(
    seat: EventSeat,
    onRemoveSeat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(78.dp)
            .padding(top = 6.dp, end = 4.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(10.dp),
            color = SelectedCardColor
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 10.dp,
                    end = 10.dp,
                    bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Ghế ${seat.seatId}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    maxLines = 1
                )
                Text(
                    text = formatPrice(seat.price),
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .clickable { onRemoveSeat(seat.id) },
            shape = CircleShape,
            color = Color(0xFFF1F1F1),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Bỏ ghế ${seat.seatId}",
                    modifier = Modifier.size(17.dp),
                    tint = SoftText
                )
            }
        }
    }
}

private fun seatRow(seatId: String): String =
    seatId.takeWhile(Char::isLetter).uppercase()

private fun seatColumn(seatId: String): Int =
    seatId.dropWhile(Char::isLetter).toIntOrNull() ?: 0

private fun rowIndex(row: String): Int =
    row.fold(0) { value, letter -> value * 26 + (letter - 'A' + 1) }
