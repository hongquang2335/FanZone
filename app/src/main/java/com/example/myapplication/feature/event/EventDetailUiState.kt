package com.example.myapplication.feature.event

import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val tiers: List<TicketTier> = emptyList(),
    val error: String? = null
)
