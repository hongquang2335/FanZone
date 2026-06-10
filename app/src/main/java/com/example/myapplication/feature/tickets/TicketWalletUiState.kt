package com.example.myapplication.feature.tickets

import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.MyTicket
import com.example.myapplication.domain.model.Order
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem

data class TicketWalletUiState(
    val selectedTab: WalletTab = WalletTab.PURCHASED,
    val selectedStatus: TicketStatus = TicketStatus.UPCOMING,
    val orders: List<Order> = emptyList(),
    val myTickets: List<MyTicket> = emptyList(),
    val recommendations: List<Event> = emptyList(),
    val isLoading: Boolean = false
) {
    fun filteredTickets(walletSeed: kotlin.collections.List<com.example.myapplication.domain.model.TicketWalletItem>) {}
}

enum class WalletTab {
    PURCHASED,
    RESALE
}
