package com.example.myapplication.feature.booking

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()
    private var seatsRegistration: ListenerRegistration? = null

    fun load(event: Event) {
        seatsRegistration?.remove()
        _uiState.value = BookingUiState(event = event, isLoading = true)

        seatsRegistration = Firebase.firestore
            .collection("event_seats")
            .whereEqualTo("eventId", event.id)
            .addSnapshotListener { snapshot, exception ->
                if (_uiState.value.event?.id != event.id) return@addSnapshotListener

                if (exception != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.localizedMessage
                                ?: "Không thể tải danh sách ghế. Vui lòng thử lại."
                        )
                    }
                    return@addSnapshotListener
                }

                val seats = snapshot?.documents.orEmpty().mapNotNull { document ->
                    val seatId = document.getString("seatId") ?: return@mapNotNull null
                    EventSeat(
                        id = document.id,
                        eventId = document.getString("eventId") ?: event.id,
                        seatId = seatId,
                        zoneId = document.getString("zoneId").orEmpty(),
                        price = (document.get("price") as? Number)?.toInt() ?: 0,
                        status = document.getString("status").orEmpty()
                    )
                }.sortedWith(
                    compareBy<EventSeat> { seatRowIndex(it.seatId) }
                        .thenBy { seatNumber(it.seatId) }
                )

                val availableSeatIds = seats.filter(EventSeat::isAvailable).mapTo(mutableSetOf()) { it.id }
                _uiState.update {
                    it.copy(
                        seats = seats,
                        selectedSeatIds = it.selectedSeatIds.intersect(availableSeatIds),
                        isLoading = false,
                        error = null
                    )
                }
            }
    }

    fun toggleSeat(seatId: String) {
        _uiState.update { state ->
            state.toggleSeatSelection(seatId)
        }
    }

    fun removeSeat(seatId: String) {
        _uiState.update { state ->
            state.removeSeatSelection(seatId)
        }
    }

    private fun seatRowIndex(seatId: String): Int =
        seatId.takeWhile(Char::isLetter)
            .uppercase()
            .fold(0) { value, letter -> value * 26 + (letter - 'A' + 1) }

    private fun seatNumber(seatId: String): Int =
        seatId.dropWhile(Char::isLetter).toIntOrNull() ?: 0

    override fun onCleared() {
        seatsRegistration?.remove()
        super.onCleared()
    }
}
