package com.example.myapplication.feature.booking

import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier

data class BookingUiState(
    val event: Event? = null,
    val tiers: List<TicketTier> = emptyList(),
    val quantities: Map<String, Int> = emptyMap()
) {
    val total: Int
        get() = tiers.sumOf { tier -> tier.price * (quantities[tier.id] ?: 0) }

    val hasSelection: Boolean
        get() = total > 0
}
