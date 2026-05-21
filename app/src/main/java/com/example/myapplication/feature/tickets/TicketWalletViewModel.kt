package com.example.myapplication.feature.tickets

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.FakeFanZoneRepository
import com.example.myapplication.domain.model.MyTicket
import com.example.myapplication.domain.model.Order
import com.example.myapplication.domain.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.format.DateTimeFormatter

class TicketWalletViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TicketWalletUiState())
    val uiState: StateFlow<TicketWalletUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Mocking data based on updated schema
        val mockOrders = listOf(
            Order(
                bookingId = "booking_101",
                userId = "user_001",
                eventId = "neon-night",
                eventTitle = "Neon Nights Festival 2024",
                items = listOf(),
                totalPrice = 1500000,
                paymentStatus = "success",
                paymentMethod = "ZaloPay",
                sellerId = "system",
                createdAt = "2026-05-10T08:55:36Z",
                qrCodeData = "BILLING_QR_12345",
                venue = "San van dong My Dinh",
                startTime = "20:00, Thu Bay 15/06/2024",
            )
        )

        val mockTickets = listOf(
            MyTicket(
                ticketId = "ticket_999",
                bookingId = "booking_101",
                eventId = "neon-night",
                eventTitle = "Neon Nights Festival 2024",
                startTime = "20:00, Thu Bay 15/06/2024",
                venue = "San van dong My Dinh",
                ticketType = "VIP",
                zoneName = "Khu vuc A (Gan san khau)",
                purchasePrice = 1100000,
                qrCodeData = "unique_hash_for_checkin",
                status = TicketStatus.UPCOMING
            ),
            MyTicket(
                ticketId = "ticket_1000",
                bookingId = "booking_101",
                eventId = "neon-night",
                eventTitle = "Neon Nights Festival 2024",
                startTime = "20:00, Thu Bay 15/06/2024",
                venue = "San van dong My Dinh",
                ticketType = "Standard",
                zoneName = "Khu vuc B",
                purchasePrice = 400000,
                qrCodeData = "unique_hash_checkin_2",
                status = TicketStatus.UPCOMING
            )
        )

        _uiState.update { 
            it.copy(
                orders = mockOrders,
                myTickets = mockTickets,
                recommendations = FakeFanZoneRepository.events.shuffled().take(3)
            )
        }
    }

    fun selectTab(tab: WalletTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectStatus(status: TicketStatus) {
        _uiState.update { it.copy(selectedStatus = status) }
    }
}
