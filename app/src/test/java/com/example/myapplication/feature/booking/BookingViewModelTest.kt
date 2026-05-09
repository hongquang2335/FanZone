package com.example.myapplication.feature.booking

import com.example.myapplication.data.repository.FakeFanZoneRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingViewModelTest {
    @Test
    fun load_filtersQuantitiesToAvailableTiersForEvent() {
        val event = FakeFanZoneRepository.events.first()
        val tiers = FakeFanZoneRepository.tiers.filter { it.eventId == event.id }
        val viewModel = BookingViewModel()

        viewModel.load(event, tiers, mapOf("vip" to 1, "expo-pass" to 2))

        assertEquals(mapOf("vip" to 1), viewModel.uiState.value.quantities)
    }

    @Test
    fun setQuantity_clampsQuantityToSupportedRange() {
        val event = FakeFanZoneRepository.events.first()
        val tiers = FakeFanZoneRepository.tiers.filter { it.eventId == event.id }
        val viewModel = BookingViewModel()
        viewModel.load(event, tiers, emptyMap())

        viewModel.setQuantity("vip", 99)

        assertEquals(8, viewModel.uiState.value.quantities["vip"])
        assertTrue(viewModel.uiState.value.hasSelection)
    }
}
