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
    CANCELLED,
    RESELLING
}

data class Order(
    val bookingId: String, // Matching user's initial snippet
    val userId: String,
    val eventId: String,
    val eventTitle: String,
    val items: List<OrderItem>,
    val totalPrice: Int,
    val paymentStatus: String,
    val paymentMethod: String,
    val sellerId: String,
    val createdAt: String,
    val qrCodeData: String, // Matching user's initial snippet "qrCodeData"
    val venue: String,
    val startTime: String
)

data class OrderItem(
    val ticketType: String,
    val quantity: Int,
    val price: Int
)

data class MyTicket(
    val ticketId: String,
    val bookingId: String, // Match naming
    val eventId: String,
    val eventTitle: String,
    val startTime: String,
    val venue: String,
    val ticketType: String,
    val zoneName: String,
    val purchasePrice: Int,
    val qrCodeData: String, // Keep consistent with Order
    val status: TicketStatus
)
