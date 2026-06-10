package com.example.myapplication.feature.tickets

import java.util.Date

data class TicketWalletUiState(
    val selectedTab: TicketTimeTab = TicketTimeTab.UPCOMING,
    val eventGroups: List<OwnedEventTickets> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val visibleGroups: List<OwnedEventTickets>
        get() = eventGroups.filter { group ->
            when (selectedTab) {
                TicketTimeTab.UPCOMING -> !group.hasEnded
                TicketTimeTab.ENDED -> group.hasEnded
            }
        }
}

enum class TicketTimeTab {
    UPCOMING,
    ENDED
}

data class OwnedEventTickets(
    val eventId: String,
    val eventTitle: String,
    val venueName: String,
    val address: String,
    val imageUrl: String?,
    val startTime: Date?,
    val endTime: Date?,
    val tickets: List<OwnedTicket>,
    val hasEnded: Boolean
) {
    val ticketCount: Int
        get() = tickets.size

    val seatNames: List<String>
        get() = tickets.map(OwnedTicket::seatName)

    val totalPrice: Int
        get() = tickets.sumOf(OwnedTicket::price)
}

data class OwnedTicket(
    val ticketId: String,
    val eventId: String,
    val ownerId: String,
    val seatName: String,
    val price: Int
)
