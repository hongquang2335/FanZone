package com.example.myapplication.feature.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.core.designsystem.component.EmptyStateCard
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.TicketCard
import com.example.myapplication.core.designsystem.component.TicketStatusFilter

@Composable
fun TicketWalletRoute(
    tickets: List<TicketWalletItem>,
    onOpenEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TicketWalletViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketWalletScreen(
        tickets = tickets,
        selectedStatus = uiState.selectedStatus,
        onSelectStatus = viewModel::selectStatus,
        onOpenEvent = onOpenEvent,
        modifier = modifier
    )
}

@Composable
fun TicketWalletScreen(
    tickets: List<TicketWalletItem>,
    selectedStatus: TicketStatus,
    onSelectStatus: (TicketStatus) -> Unit,
    onOpenEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp
        val filtered = tickets.filter { it.status == selectedStatus }

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { SectionHeader("Ve cua toi", "Quan ly ve va QR check-in") }
            item { TicketStatusFilter(selectedStatus, onSelect = onSelectStatus) }

            if (filtered.isEmpty()) {
                item { EmptyStateCard("Khong co ve o trang thai nay", "Mua mot su kien moi de lap day vi ve.") }
            } else if (isExpanded) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        filtered.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) { TicketCard(item, onOpenEvent) }
                        }
                    }
                }
            } else {
                items(filtered) { item -> TicketCard(item, onOpenEvent) }
            }
        }
    }
}

