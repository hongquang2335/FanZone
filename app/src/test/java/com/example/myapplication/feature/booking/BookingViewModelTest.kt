package com.example.myapplication.feature.booking

import com.example.myapplication.domain.model.EventSeat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingViewModelTest {
    private val availableSeat = EventSeat(
        id = "event_A1",
        eventId = "event",
        seatId = "A1",
        zoneId = EventSeat.VIP_ZONE_ID,
        price = 125_000,
        status = "available"
    )
    private val soldSeat = EventSeat(
        id = "event_A2",
        eventId = "event",
        seatId = "A2",
        zoneId = EventSeat.VIP_ZONE_ID,
        price = 125_000,
        status = "sold"
    )

    @Test
    fun toggleSeatSelection_selectsAvailableSeatAndCalculatesTotal() {
        val initialState = BookingUiState(seats = listOf(availableSeat, soldSeat))

        val selectedState = initialState.toggleSeatSelection(availableSeat.id)

        assertTrue(selectedState.hasSelection)
        assertEquals(listOf(availableSeat), selectedState.selectedSeats)
        assertEquals(125_000, selectedState.total)
    }

    @Test
    fun toggleSeatSelection_ignoresUnavailableSeatAndCanDeselect() {
        val initialState = BookingUiState(seats = listOf(availableSeat, soldSeat))

        val afterSoldSeat = initialState.toggleSeatSelection(soldSeat.id)
        val afterSelect = afterSoldSeat.toggleSeatSelection(availableSeat.id)
        val afterDeselect = afterSelect.toggleSeatSelection(availableSeat.id)

        assertFalse(afterSoldSeat.hasSelection)
        assertFalse(afterDeselect.hasSelection)
        assertEquals(0, afterDeselect.total)
    }

    @Test
    fun toggleSeatSelection_limitsSelectionToEightSeats() {
        val seats = (1..9).map { number ->
            availableSeat.copy(
                id = "event_A$number",
                seatId = "A$number"
            )
        }
        var state = BookingUiState(seats = seats)

        seats.forEach { seat ->
            state = state.toggleSeatSelection(seat.id)
        }

        assertEquals(MAX_SELECTED_SEATS, state.selectedSeats.size)
        assertFalse(seats.last().id in state.selectedSeatIds)

        state = state.toggleSeatSelection(seats.first().id)
        state = state.toggleSeatSelection(seats.last().id)

        assertEquals(MAX_SELECTED_SEATS, state.selectedSeats.size)
        assertTrue(seats.last().id in state.selectedSeatIds)
    }
}
