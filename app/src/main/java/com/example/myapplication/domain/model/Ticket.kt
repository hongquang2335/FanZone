package com.example.myapplication.domain.model

data class TicketTier(
    val id: String,
    val eventId: String,
    val name: String,
    val benefits: String,
    val price: Int,
    val status: TierStatus
)

enum class TierStatus {
    AVAILABLE,
    LIMITED,
    SOLD_OUT
}

data class TicketWalletItem(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val seatLabel: String,
    val schedule: String,
    val venue: String,
    val qrCode: String,
    val status: TicketStatus
)

enum class TicketStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}
