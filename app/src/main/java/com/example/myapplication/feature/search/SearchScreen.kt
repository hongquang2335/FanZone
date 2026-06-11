package com.example.myapplication.feature.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeGreenDeep
import com.example.myapplication.core.designsystem.theme.VibeStroke
import com.example.myapplication.core.designsystem.theme.VibeSurfaceMuted
import com.example.myapplication.core.designsystem.theme.VibeText
import com.example.myapplication.core.designsystem.theme.VibeTextMuted
import com.example.myapplication.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

private val searchCategories = listOf(
    "Nhạc sống",
    "Sân khấu & Nghệ thuật",
    "Thể thao",
    "Hội thảo & Workshop",
    "Tham quan & Trải nghiệm",
    "Khác"
)

private data class SearchDateRange(
    val startMillis: Long,
    val endMillis: Long? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    events: List<Event>,
    searchHistory: List<String>,
    isSignedIn: Boolean,
    initialCategory: String? = null,
    onSearchSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenEvent: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var appliedDateRange by remember { mutableStateOf<SearchDateRange?>(null) }
    var appliedCategories by remember(initialCategory) {
        mutableStateOf(
            initialCategory
                ?.takeIf { category ->
                    searchCategories.any { it.equals(category, ignoreCase = true) }
                }
                ?.let(::setOf)
                .orEmpty()
        )
    }
    var showDateFilter by remember { mutableStateOf(false) }
    var showCategoryFilter by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val initialSuggestions = remember(events) { events.shuffled().take(4) }

    val displayedEvents = remember(
        searchQuery,
        events,
        initialSuggestions,
        appliedDateRange,
        appliedCategories
    ) {
        val hasActiveFilters = appliedDateRange != null || appliedCategories.isNotEmpty()
        val source = if (searchQuery.isBlank() && !hasActiveFilters) {
            initialSuggestions
        } else {
            events
        }

        source.filter { event ->
            val matchesQuery = searchQuery.isBlank() ||
                event.title.contains(searchQuery.trim(), ignoreCase = true)
            val matchesDate = appliedDateRange?.let { event.matchesDateRange(it) } ?: true
            val matchesCategory = appliedCategories.isEmpty() ||
                appliedCategories.any { selectedCategory ->
                    event.category.any { it.equals(selectedCategory, ignoreCase = true) } ||
                        (selectedCategory == "Khác" && event.category.isEmpty())
                }
            matchesQuery && matchesDate && matchesCategory
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibeCanvas)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchHeader(onBackClick = onBackClick)

            SearchInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onSearch = {
                    if (isSignedIn && searchQuery.isNotBlank()) {
                        onSearchSubmit(searchQuery)
                    }
                    focusManager.clearFocus()
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (
                    searchQuery.isBlank() &&
                    appliedDateRange == null &&
                    appliedCategories.isEmpty() &&
                    isSignedIn &&
                    searchHistory.isNotEmpty()
                ) {
                    item {
                        SearchHistorySection(
                            history = searchHistory.take(5),
                            onSelect = { searchQuery = it }
                        )
                    }
                }

                item {
                    SearchFilterBar(
                        dateLabel = appliedDateRange?.toDisplayLabel() ?: "Tất cả các ngày",
                        isDateActive = appliedDateRange != null,
                        isCategoryActive = appliedCategories.isNotEmpty(),
                        onOpenDateFilter = { showDateFilter = true },
                        onOpenCategoryFilter = { showCategoryFilter = true }
                    )
                }

                if (appliedCategories.isNotEmpty()) {
                    item {
                        AppliedCategoryChips(
                            categories = appliedCategories.toList(),
                            onRemove = { category ->
                                appliedCategories = appliedCategories - category
                            }
                        )
                    }
                }

                item {
                    Text(
                        text = if (
                            searchQuery.isBlank() &&
                            appliedDateRange == null &&
                            appliedCategories.isEmpty()
                        ) {
                            "Gợi ý dành cho bạn"
                        } else {
                            "Kết quả tìm kiếm (${displayedEvents.size})"
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = VibeText,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (displayedEvents.isEmpty()) {
                    item {
                        EmptySearchResult()
                    }
                } else {
                    items(
                        items = displayedEvents.chunked(2),
                        key = { row -> row.joinToString("|") { it.id } }
                    ) { rowEvents ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowEvents.forEach { event ->
                                SearchEventCard(
                                    event = event,
                                    onClick = { onOpenEvent(event.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowEvents.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        if (showDateFilter) {
            DateFilterDialog(
                appliedRange = appliedDateRange,
                onDismiss = { showDateFilter = false },
                onApply = { range ->
                    appliedDateRange = range
                    showDateFilter = false
                }
            )
        }

        if (showCategoryFilter) {
            CategoryFilterDialog(
                appliedCategories = appliedCategories,
                onDismiss = { showCategoryFilter = false },
                onApply = { categories ->
                    appliedCategories = categories
                    showCategoryFilter = false
                }
            )
        }
    }
}

@Composable
private fun SearchHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = VibeText
            )
        }
        Text(
            text = "Tìm kiếm",
            color = VibeText,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Nhập từ khóa", color = VibeTextMuted) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = VibeTextMuted)
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = VibeTextMuted)
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = VibeGreenDark,
            unfocusedIndicatorColor = VibeStroke,
            cursorColor = VibeGreenDark
        )
    )
}

@Composable
private fun SearchHistorySection(
    history: List<String>,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Tìm kiếm gần đây",
            color = VibeText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        history.forEach { query ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(query) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = VibeTextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = query,
                    modifier = Modifier.weight(1f),
                    color = VibeText,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VibeTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchFilterBar(
    dateLabel: String,
    isDateActive: Boolean,
    isCategoryActive: Boolean,
    onOpenDateFilter: () -> Unit,
    onOpenCategoryFilter: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            FilterButton(
                label = dateLabel,
                icon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                active = isDateActive,
                onClick = onOpenDateFilter
            )
        }
        item {
            FilterButton(
                label = "Bộ lọc",
                icon = {
                    Icon(
                        Icons.Default.FilterAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                active = isCategoryActive,
                onClick = onOpenCategoryFilter
            )
        }
    }
}

@Composable
private fun FilterButton(
    label: String,
    icon: @Composable () -> Unit,
    active: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) VibeGreen else VibeSurfaceMuted,
            contentColor = if (active) VibeGreenDeep else VibeTextMuted
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
    ) {
        icon()
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun AppliedCategoryChips(
    categories: List<String>,
    onRemove: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it }) { category ->
            Surface(
                shape = CircleShape,
                color = VibeGreen,
                contentColor = VibeGreenDeep
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 7.dp, bottom = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(category, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = { onRemove(category) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Xóa $category",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchEventImage(
            event = event,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Text(
            text = event.title,
            color = VibeText,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = VibeTextMuted,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = event.schedule.ifBlank { "Đang cập nhật" },
                color = VibeTextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchEventImage(
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
                )
            )
        }
    }
}

@Composable
private fun EmptySearchResult() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = VibeTextMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(52.dp)
        )
        Text(
            text = "Không tìm thấy sự kiện",
            color = VibeText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hãy thử từ khóa hoặc điều kiện lọc khác.",
            color = VibeTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DateFilterDialog(
    appliedRange: SearchDateRange?,
    onDismiss: () -> Unit,
    onApply: (SearchDateRange?) -> Unit
) {
    val now = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(now.get(Calendar.MONTH)) }
    var draftStart by remember(appliedRange) { mutableStateOf(appliedRange?.startMillis) }
    var draftEnd by remember(appliedRange) { mutableStateOf(appliedRange?.endMillis) }

    SearchOverlay(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 720.dp)
                .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .navigationBarsPadding()
        ) {
            OverlayHeader(title = "Chọn thời gian", onClose = onDismiss)
            HorizontalDivider(color = VibeStroke)

            CalendarMonthPicker(
                year = displayedYear,
                month = displayedMonth,
                selectedStart = draftStart,
                selectedEnd = draftEnd,
                onPreviousMonth = {
                    if (displayedMonth == Calendar.JANUARY) {
                        displayedMonth = Calendar.DECEMBER
                        displayedYear -= 1
                    } else {
                        displayedMonth -= 1
                    }
                },
                onNextMonth = {
                    if (displayedMonth == Calendar.DECEMBER) {
                        displayedMonth = Calendar.JANUARY
                        displayedYear += 1
                    } else {
                        displayedMonth += 1
                    }
                },
                onSelectDay = { selectedDay ->
                    when {
                        draftStart == null || draftEnd != null -> {
                            draftStart = selectedDay
                            draftEnd = null
                        }

                        else -> {
                            val firstDay = draftStart ?: selectedDay
                            if (selectedDay == firstDay) {
                                draftStart = selectedDay
                                draftEnd = null
                            } else {
                                draftStart = min(firstDay, selectedDay)
                                draftEnd = max(firstDay, selectedDay)
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            OverlayActions(
                onReset = {
                    draftStart = null
                    draftEnd = null
                },
                onApply = {
                    onApply(draftStart?.let { SearchDateRange(it, draftEnd) })
                }
            )
        }
    }
}

@Composable
private fun CalendarMonthPicker(
    year: Int,
    month: Int,
    selectedStart: Long?,
    selectedEnd: Long?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (Long) -> Unit
) {
    val monthName = remember(year, month) {
        SimpleDateFormat("MMMM, yyyy", Locale("vi", "VN")).format(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, 1)
            }.time
        ).replaceFirstChar { it.uppercase(Locale("vi", "VN")) }
    }
    val firstDayOffset = remember(year, month) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }
    val daysInMonth = remember(year, month) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tháng trước",
                    tint = VibeGreenDark
                )
            }
            Text(monthName, color = VibeText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Tháng sau",
                    tint = VibeGreenDark
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { weekday ->
                Text(
                    text = weekday,
                    modifier = Modifier.weight(1f),
                    color = VibeTextMuted,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        repeat(6) { rowIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { columnIndex ->
                    val cellIndex = rowIndex * 7 + columnIndex
                    val dayNumber = cellIndex - firstDayOffset + 1
                    if (dayNumber in 1..daysInMonth) {
                        val dayMillis = dayStartMillis(year, month, dayNumber)
                        CalendarDayCell(
                            day = dayNumber,
                            dayMillis = dayMillis,
                            selectedStart = selectedStart,
                            selectedEnd = selectedEnd,
                            onClick = { onSelectDay(dayMillis) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    dayMillis: Long,
    selectedStart: Long?,
    selectedEnd: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { startOfDay(System.currentTimeMillis()) }
    val isToday = dayMillis == today
    val isEndpoint = dayMillis == selectedStart || dayMillis == selectedEnd
    val isInRange = selectedStart != null && selectedEnd != null &&
        dayMillis in selectedStart..selectedEnd

    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                color = if (isInRange) VibeGreen.copy(alpha = 0.22f) else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = if (isEndpoint) VibeGreen else Color.Transparent,
            contentColor = if (isEndpoint) Color.White else VibeText,
            border = if (isToday && !isEndpoint) BorderStroke(1.5.dp, VibeGreenDark) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = day.toString(),
                    fontSize = 15.sp,
                    fontWeight = if (isEndpoint || isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterDialog(
    appliedCategories: Set<String>,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit
) {
    var draftCategories by remember(appliedCategories) {
        mutableStateOf(appliedCategories)
    }

    SearchOverlay(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .navigationBarsPadding()
        ) {
            OverlayHeader(title = "Bộ lọc", onClose = onDismiss)
            HorizontalDivider(color = VibeStroke)
            Text(
                text = "Thể loại",
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
                color = VibeText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                searchCategories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowCategories.forEach { category ->
                            val selected = category in draftCategories
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        draftCategories = if (selected) {
                                            draftCategories - category
                                        } else {
                                            draftCategories + category
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) VibeGreen else VibeSurfaceMuted,
                                contentColor = if (selected) VibeGreenDeep else VibeText,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) VibeGreenDark else VibeStroke
                                )
                            ) {
                                Text(
                                    text = category,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            OverlayActions(
                onReset = { draftCategories = emptySet() },
                onApply = { onApply(draftCategories) }
            )
        }
    }
}

@Composable
private fun SearchOverlay(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
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
                .background(Color.Black.copy(alpha = 0.52f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun OverlayHeader(
    title: String,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = VibeText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = VibeText)
        }
    }
}

@Composable
private fun OverlayActions(
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, VibeGreen),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibeGreenDark)
        ) {
            Text("Thiết lập lại", fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onApply,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VibeGreen,
                contentColor = Color.White
            )
        ) {
            Text("Áp dụng", fontWeight = FontWeight.Bold)
        }
    }
}

private fun Event.matchesDateRange(range: SearchDateRange): Boolean {
    val start = parseEventDate(startTime) ?: parseEventDate(schedule) ?: return false
    val end = parseEventDate(endTime) ?: start
    val selectedEnd = range.endMillis ?: range.startMillis
    return start <= selectedEnd && end >= range.startMillis
}

private fun parseEventDate(value: String): Long? {
    if (value.isBlank()) return null

    Regex("""(\d{4})-(\d{1,2})-(\d{1,2})""")
        .find(value)
        ?.destructured
        ?.let { (year, month, day) ->
            return dayStartMillis(year.toInt(), month.toInt() - 1, day.toInt())
        }

    Regex("""(\d{1,2})/(\d{1,2})/(\d{4})""")
        .find(value)
        ?.destructured
        ?.let { (day, month, year) ->
            return dayStartMillis(year.toInt(), month.toInt() - 1, day.toInt())
        }

    return null
}

private fun SearchDateRange.toDisplayLabel(): String {
    val formatter = SimpleDateFormat("dd 'Tháng' MM, yyyy", Locale("vi", "VN"))
    val startLabel = formatter.format(Date(startMillis))
    val endLabel = endMillis?.let { formatter.format(Date(it)) }
    return if (endLabel == null) startLabel else "$startLabel - $endLabel"
}

private fun dayStartMillis(year: Int, month: Int, day: Int): Long {
    return Calendar.getInstance().apply {
        clear()
        set(year, month, day, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun startOfDay(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
