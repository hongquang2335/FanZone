package com.example.myapplication.feature.booking

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun load(event: Event, tiers: List<TicketTier>, quantities: Map<String, Int>) {
        _uiState.update {
            it.copy(
                event = event,
                tiers = tiers,
                quantities = quantities.filterKeys { tierId -> tiers.any { tier -> tier.id == tierId } }
            )
        }
    }

    fun setQuantity(tierId: String, quantity: Int) {
        _uiState.update { state ->
            state.copy(
                quantities = state.quantities.toMutableMap().apply {
                    put(tierId, quantity.coerceIn(0, 8))
                }
            )
        }
    }
}
