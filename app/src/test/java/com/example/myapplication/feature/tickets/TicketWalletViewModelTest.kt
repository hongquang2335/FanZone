package com.example.myapplication.feature.tickets

import com.example.myapplication.data.repository.FakeFanZoneRepository
import com.example.myapplication.domain.model.TicketStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TicketWalletViewModelTest {
    @Test
    fun selectStatus_updatesFilterState() {
        val viewModel = TicketWalletViewModel()

        viewModel.selectStatus(TicketStatus.COMPLETED)

        assertEquals(TicketStatus.COMPLETED, viewModel.uiState.value.selectedStatus)
    }

    @Test
    fun filteredTickets_returnsTicketsMatchingSelectedStatus() {
        val state = TicketWalletUiState(selectedStatus = TicketStatus.UPCOMING)

        val filtered = state.filteredTickets(FakeFanZoneRepository.walletSeed)

        assertEquals(FakeFanZoneRepository.walletSeed, filtered)
    }
}
