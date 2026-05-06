package com.example.myapplication.feature.tickets

import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem

data class TicketWalletUiState(
    val selectedStatus: TicketStatus = TicketStatus.UPCOMING
) {
    fun filteredTickets(tickets: List<TicketWalletItem>): List<TicketWalletItem> =
        tickets.filter { it.status == selectedStatus }
}
