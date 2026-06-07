package com.example.myapplication.feature.booking

import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat

const val MAX_SELECTED_SEATS = 8

data class BookingUiState(
    val event: Event? = null,
    val seats: List<EventSeat> = emptyList(),
    val selectedSeatIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val selectedSeats: List<EventSeat>
        get() = seats.filter { it.id in selectedSeatIds }

    val total: Int
        get() = selectedSeats.sumOf(EventSeat::price)

    val hasSelection: Boolean
        get() = selectedSeatIds.isNotEmpty()
}

internal fun BookingUiState.toggleSeatSelection(seatId: String): BookingUiState {
    val seat = seats.firstOrNull { it.id == seatId }
    if (seat == null || !seat.isAvailable) return this
    if (seatId !in selectedSeatIds && selectedSeatIds.size >= MAX_SELECTED_SEATS) return this

    return copy(
        selectedSeatIds = selectedSeatIds.toMutableSet().apply {
            if (!add(seatId)) remove(seatId)
        }
    )
}

internal fun BookingUiState.removeSeatSelection(seatId: String): BookingUiState =
    copy(selectedSeatIds = selectedSeatIds - seatId)
