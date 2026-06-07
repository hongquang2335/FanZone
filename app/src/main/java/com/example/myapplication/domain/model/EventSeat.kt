package com.example.myapplication.domain.model

data class EventSeat(
    val id: String,
    val eventId: String,
    val seatId: String,
    val zoneId: String,
    val price: Int,
    val status: String
) {
    val isAvailable: Boolean
        get() = status.equals("available", ignoreCase = true)

    val isVip: Boolean
        get() = zoneId == VIP_ZONE_ID

    companion object {
        const val VIP_ZONE_ID = "khan_dai_vip"
        const val STANDARD_ZONE_ID = "khan_dai_thuong"
    }
}
