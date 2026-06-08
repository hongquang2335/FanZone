package com.example.myapplication.ui.state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.Artist
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus
import com.example.myapplication.domain.repository.FanZoneRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FanZoneViewModel(
    private val repository: FanZoneRepository = AppDependencies.fanZoneRepository,
    loadRemoteEvents: Boolean = true
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
            selectedEventId = repository.events.firstOrNull()?.id ?: "",
            selectedPaymentMethod = repository.paymentMethods.firstOrNull()?.id ?: "",
            unreadSupportCount = 2,
            tierQuantities = emptyMap()
        )
    )
    val uiState: StateFlow<FanZoneUiState> = _uiState.asStateFlow()

    init {
        if (loadRemoteEvents) {
            fetchEvents()
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            try {
                Log.d("FanZoneVM", "Bắt đầu tải danh sách sự kiện từ Firestore...")
                val snapshot = Firebase.firestore.collection("event").get().await()
                
                if (snapshot.isEmpty) {
                    Log.w("FanZoneVM", "Collection 'event' đang trống trên Firestore!")
                    return@launch
                }

                val fetchedEvents = snapshot.documents.mapNotNull { doc ->
                    try {
                        val rawStartTime = doc.getString("startTime") ?: ""
                        val rawEndTime = doc.getString("endTime") ?: ""
                        
                        val formattedSchedule = if (rawStartTime.isNotEmpty()) {
                            formatDate(rawStartTime) + (if (rawEndTime.isNotEmpty()) " - " + formatDate(rawEndTime) else "")
                        } else doc.getString("schedule") ?: ""

                        Event(
                            // LUÔN dùng doc.id để khớp với Document trên Firestore
                            id = doc.id, 
                            title = doc.getString("title") ?: "Không có tiêu đề",
                            subtitle = doc.getString("orgName") ?: doc.getString("subtitle") ?: "",
                            schedule = formattedSchedule,
                            venue = doc.getString("venue") ?: "",
                            city = doc.getString("address") ?: doc.getString("city") ?: "",
                            description = doc.getString("description") ?: "",
                            artists = (doc.get("artists") as? List<*>)?.mapNotNull { artistObj ->
                                val map = artistObj as? Map<*, *> ?: return@mapNotNull null
                                Artist(
                                    id = map["id"]?.toString() ?: "",
                                    name = map["name"]?.toString() ?: "",
                                    image = map["image"]?.toString() ?: ""
                                )
                            } ?: emptyList(),
                            timeline = emptyList(),
                            notices = (doc.get("notices") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            imageRes = 0,
                            imageUrl = doc.getString("banner") ?: doc.getString("imageUrl"),
                            tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            category = (doc.get("category") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            orgName = doc.getString("orgName") ?: "",
                            orgLogo = doc.getString("orgLogo"),
                            orgDescription = doc.getString("orgDescription") ?: "",
                            startTime = rawStartTime,
                            endTime = rawEndTime
                        )
                    } catch (e: Exception) {
                        Log.e("FanZoneVM", "Lỗi khi parse Document ${doc.id}: ${e.message}")
                        null
                    }
                }
                
                Log.d("FanZoneVM", "Tải thành công ${fetchedEvents.size} sự kiện.")
                _uiState.update { state ->
                    state.copy(
                        events = fetchedEvents,
                        selectedEventId = fetchedEvents.firstOrNull()?.id ?: state.selectedEventId
                    )
                }
            } catch (e: Exception) {
                Log.e("FanZoneVM", "Lỗi kết nối Firestore: ${e.message}", e)
            }
        }
    }

    private fun formatDate(dateString: String): String {
        try {
            val parts = dateString.split("T")
            if (parts.size >= 2) {
                val date = parts[0]
                val dateParts = date.split("-")
                if (dateParts.size == 3) {
                    return "${dateParts[2]}/${dateParts[1]}/${dateParts[0]}"
                }
            }
        } catch (e: Exception) {}
        return dateString
    }

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

    fun setSelectedSeats(seats: List<EventSeat>) {
        _uiState.update { state ->
            val eventId = state.selectedEvent.id
            val selectedSeatTiers = seats.map { seat ->
                TicketTier(
                    id = seat.id,
                    eventId = eventId,
                    name = buildString {
                        append("Ghế ${seat.seatId}")
                        append(if (seat.isVip) " (VIP)" else " (Thường)")
                    },
                    benefits = seat.zoneId,
                    price = seat.price,
                    status = TierStatus.AVAILABLE
                )
            }

            state.copy(
                tiers = state.tiers.filterNot { it.eventId == eventId } + selectedSeatTiers,
                tierQuantities = selectedSeatTiers.associate { it.id to 1 },
                selectedSeats = seats
            )
        }
    }

    fun clearTicketSelection() {
        _uiState.update { it.copy(tierQuantities = emptyMap(), selectedSeats = emptyList()) }
    }

    fun confirmPurchase(): TicketWalletItem {
        val state = _uiState.value
        val seatLabel = state.selectedSeats
            .takeIf { it.isNotEmpty() }
            ?.joinToString { seat -> seat.seatId }
            ?: state.tiersForSelectedEvent
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
                tierQuantities = emptyMap(),
                selectedSeats = emptyList()
            )
        }
        return ticket
    }
}
