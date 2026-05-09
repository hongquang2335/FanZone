package com.example.myapplication.feature.tickets

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TicketWalletViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TicketWalletUiState())
    val uiState: StateFlow<TicketWalletUiState> = _uiState.asStateFlow()

    fun selectStatus(status: TicketStatus) {
        _uiState.update { it.copy(selectedStatus = status) }
    }
}
