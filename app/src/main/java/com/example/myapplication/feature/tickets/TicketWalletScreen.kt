package com.example.myapplication.feature.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.core.designsystem.component.EmptyStateCard
import com.example.myapplication.core.designsystem.component.OrderTicketCard
import com.example.myapplication.core.designsystem.component.RecommendedEventCard
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.TicketDetailBottomSheet
import com.example.myapplication.core.designsystem.component.TicketStatusFilter
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.MyTicket
import com.example.myapplication.domain.model.Order
import com.example.myapplication.domain.model.TicketStatus

@Composable
fun TicketWalletRoute(
    onOpenEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TicketWalletViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketWalletScreen(
        uiState = uiState,
        onSelectTab = viewModel::selectTab,
        onSelectStatus = viewModel::selectStatus,
        onOpenEvent = onOpenEvent,
        modifier = modifier
    )
}

@Composable
fun TicketWalletScreen(
    uiState: TicketWalletUiState,
    onSelectTab: (WalletTab) -> Unit,
    onSelectStatus: (TicketStatus) -> Unit,
    onOpenEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOrderForDetail by remember { mutableStateOf<Order?>(null) }
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Green Background - Optimized for UI/UX
            item {
                Surface(
                    color = Evergreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(52.dp), // Standard compact top bar height
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Vé của tôi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Main Tabs (Pill style)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WalletTabPill(
                        label = "Vé đã mua",
                        selected = uiState.selectedTab == WalletTab.PURCHASED,
                        onClick = { onSelectTab(WalletTab.PURCHASED) },
                        modifier = Modifier.weight(1f)
                    )
                    WalletTabPill(
                        label = "Vé bán lại",
                        selected = uiState.selectedTab == WalletTab.RESALE,
                        onClick = { onSelectTab(WalletTab.RESALE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sub-tabs (Underline style)
            if (uiState.selectedTab == WalletTab.PURCHASED) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SubTabUnderline(
                            label = "Sắp diễn ra",
                            selected = uiState.selectedStatus == TicketStatus.UPCOMING,
                            onClick = { onSelectStatus(TicketStatus.UPCOMING) }
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        SubTabUnderline(
                            label = "Đã kết thúc",
                            selected = uiState.selectedStatus == TicketStatus.COMPLETED,
                            onClick = { onSelectStatus(TicketStatus.COMPLETED) }
                        )
                    }
                }

                val filteredOrders = uiState.orders.filter { order ->
                    if (uiState.selectedStatus == TicketStatus.UPCOMING) {
                        order.paymentStatus == "success" // Active orders
                    } else {
                        false // Past orders mock
                    }
                }

                if (filteredOrders.isEmpty()) {
                    item {
                        Box(modifier = Modifier.padding(16.dp)) {
                            EmptyStateCard("Không có vé nào", "Hãy bắt đầu khám phá các sự kiện.")
                        }
                    }
                } else {
                    items(filteredOrders) { order ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            OrderTicketCard(order = order, onClick = { selectedOrderForDetail = order })
                        }
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.padding(16.dp)) {
                        EmptyStateCard("Chưa có vé đăng bán", "Bạn có thể đăng bán lại vé của mình tại đây.")
                    }
                }
            }

            // Recommendations Section (Keeping your recommendation system)
            item {
                Column(modifier = Modifier.padding(top = 32.dp)) {
                    Text(
                        "Có thể bạn cũng thích",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recommendations) { event ->
                            RecommendedEventCard(event = event, onOpenEvent = onOpenEvent)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        OutlinedButton(
                            onClick = { /* See more */ },
                            border = BorderStroke(1.dp, SoftText.copy(alpha = 0.3f))
                        ) {
                            Text("Xem thêm", color = SoftText)
                        }
                    }
                }
            }
        }

        selectedOrderForDetail?.let { order ->
            val orderTickets = uiState.myTickets.filter { it.bookingId == order.bookingId }
            TicketDetailBottomSheet(
                tickets = orderTickets,
                onDismiss = { selectedOrderForDetail = null }
            )
        }
    }
}

@Composable
fun WalletTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = if (selected) Evergreen else Evergreen.copy(alpha = 0.1f),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Evergreen
            )
        }
    }
}

@Composable
fun SubTabUnderline(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Evergreen else SoftText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .background(if (selected) Evergreen else Color.Transparent)
        )
    }
}

