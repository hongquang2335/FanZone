package com.example.myapplication.ui.state

import androidx.lifecycle.ViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.repository.FanZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FanZoneViewModel(
    private val repository: FanZoneRepository = AppDependencies.fanZoneRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        FanZoneUiState(
            user = repository.user,
            categories = repository.categories,
            events = repository.events,
            tiers = repository.tiers,
            posts = repository.posts,
            walletItems = repository.walletSeed,
            paymentMethods = repository.paymentMethods,
            supportShortcuts = repository.supportShortcuts,
            selectedEventId = repository.events.first().id,
            selectedPaymentMethod = repository.paymentMethods.first().id,
            unreadSupportCount = 2,
            tierQuantities = emptyMap()
        )
    )
    val uiState: StateFlow<FanZoneUiState> = _uiState.asStateFlow()

    fun selectEvent(eventId: String) {
        _uiState.update { state ->
            if (state.events.any { it.id == eventId }) {
                state.copy(selectedEventId = eventId)
            } else {
                state
            }
        }
    }

    fun selectPaymentMethod(methodId: String) {
        _uiState.update { it.copy(selectedPaymentMethod = methodId) }
    }

    fun setTierQuantity(tierId: String, quantity: Int) {
        _uiState.update { state ->
            state.copy(
                tierQuantities = state.tierQuantities.toMutableMap().apply {
                    put(tierId, quantity.coerceAtLeast(0))
                }
            )
        }
    }

    fun setTierQuantities(quantities: Map<String, Int>) {
        _uiState.update { state ->
            state.copy(tierQuantities = quantities.mapValues { it.value.coerceAtLeast(0) })
        }
    }

    fun clearTicketSelection() {
        _uiState.update { it.copy(tierQuantities = emptyMap()) }
    }

    fun confirmPurchase(): TicketWalletItem {
        val state = _uiState.value
        val seatLabel = state.tiersForSelectedEvent
            .filter { (state.tierQuantities[it.id] ?: 0) > 0 }
            .joinToString { tier -> "${tier.name} x${state.tierQuantities[tier.id] ?: 0}" }

        val ticket = TicketWalletItem(
            id = "ticket-${state.walletItems.size + 1}",
            eventId = state.selectedEvent.id,
            eventTitle = state.selectedEvent.title,
            seatLabel = seatLabel,
            schedule = state.selectedEvent.schedule,
            venue = "${state.selectedEvent.venue}, ${state.selectedEvent.city}",
            qrCode = "QR-${state.selectedEvent.id.uppercase()}-${state.walletItems.size + 1}",
            status = TicketStatus.UPCOMING
        )

        _uiState.update {
            it.copy(
                walletItems = listOf(ticket) + it.walletItems,
                latestPurchasedTicketId = ticket.id,
                tierQuantities = emptyMap()
            )
        }
        return ticket
    }
}

