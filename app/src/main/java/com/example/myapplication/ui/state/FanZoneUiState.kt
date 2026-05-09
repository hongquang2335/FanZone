package com.example.myapplication.ui.state

import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.SupportShortcut
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.UserProfile

data class FanZoneUiState(
    val user: UserProfile,
    val categories: List<Category>,
    val events: List<Event>,
    val tiers: List<TicketTier>,
    val posts: List<CommunityPost>,
    val walletItems: List<TicketWalletItem>,
    val paymentMethods: List<PaymentMethod>,
    val supportShortcuts: List<SupportShortcut>,
    val selectedEventId: String,
    val selectedPaymentMethod: String,
    val unreadSupportCount: Int,
    val tierQuantities: Map<String, Int>,
    val latestPurchasedTicketId: String? = null
) {
    val selectedEvent: Event
        get() = events.firstOrNull { it.id == selectedEventId } ?: events.first()

    val tiersForSelectedEvent: List<TicketTier>
        get() = tiers.filter { it.eventId == selectedEvent.id }

    val latestPurchasedTicket: TicketWalletItem?
        get() = latestPurchasedTicketId?.let { ticketId -> walletItems.find { it.id == ticketId } }
}

