package com.example.myapplication.ui.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FanZoneViewModelTest {
    @Test
    fun selectEvent_ignoresUnknownEventIds() {
        val viewModel = FanZoneViewModel()
        val originalEventId = viewModel.uiState.value.selectedEventId

        viewModel.selectEvent("missing-event")

        assertEquals(originalEventId, viewModel.uiState.value.selectedEventId)
    }

    @Test
    fun confirmPurchase_addsTicketAndClearsSelection() {
        val viewModel = FanZoneViewModel()
        viewModel.setTierQuantity("vip", 1)

        val ticket = viewModel.confirmPurchase()
        val state = viewModel.uiState.value

        assertEquals(ticket.id, state.latestPurchasedTicketId)
        assertEquals(ticket, state.walletItems.first())
        assertEquals("Zone A (VIP) x1", ticket.seatLabel)
        assertTrue(state.tierQuantities.isEmpty())
    }
}
