package com.example.myapplication.feature.tickets

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.Date

class TicketWalletViewModelTest {

    @Test
    fun selectTab_updatesFilterState() {
        val mockAuth = mockk<FirebaseAuth>(relaxed = true)
        val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
        every { mockAuth.currentUser } returns null

        val viewModel = TicketWalletViewModel(mockAuth, mockFirestore)

        viewModel.selectTab(TicketTimeTab.ENDED)
        assertEquals(TicketTimeTab.ENDED, viewModel.uiState.value.selectedTab)

        viewModel.selectTab(TicketTimeTab.UPCOMING)
        assertEquals(TicketTimeTab.UPCOMING, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun visibleGroups_filtersTicketsCorrectlyBasedOnTab() {
        val upcomingGroup = OwnedEventTickets(
            eventId = "event-1",
            eventTitle = "Upcoming Concert",
            venueName = "Venue A",
            address = "Hanoi",
            imageUrl = null,
            startTime = Date(),
            endTime = Date(System.currentTimeMillis() + 86400000),
            tickets = emptyList(),
            hasEnded = false
        )

        val endedGroup = OwnedEventTickets(
            eventId = "event-2",
            eventTitle = "Past Concert",
            venueName = "Venue B",
            address = "HCMC",
            imageUrl = null,
            startTime = Date(System.currentTimeMillis() - 86400000),
            endTime = Date(System.currentTimeMillis() - 3600000),
            tickets = emptyList(),
            hasEnded = true
        )

        val allGroups = listOf(upcomingGroup, endedGroup)

        val upcomingState = TicketWalletUiState(
            selectedTab = TicketTimeTab.UPCOMING,
            eventGroups = allGroups,
            isLoading = false
        )
        val visibleUpcoming = upcomingState.visibleGroups
        assertEquals(1, visibleUpcoming.size)
        assertEquals("event-1", visibleUpcoming.first().eventId)
        assertFalse(visibleUpcoming.first().hasEnded)

        val endedState = TicketWalletUiState(
            selectedTab = TicketTimeTab.ENDED,
            eventGroups = allGroups,
            isLoading = false
        )
        val visibleEnded = endedState.visibleGroups
        assertEquals(1, visibleEnded.size)
        assertEquals("event-2", visibleEnded.first().eventId)
        assertTrue(visibleEnded.first().hasEnded)
    }
}
