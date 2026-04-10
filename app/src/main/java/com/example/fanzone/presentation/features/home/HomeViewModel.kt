package com.example.fanzone.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fanzone.domain.models.Event
import com.example.fanzone.domain.usecases.GetTrendingEventsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingEventsUseCase: GetTrendingEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrendingEvents()
    }

    private fun loadTrendingEvents() {
        viewModelScope.launch {
            getTrendingEventsUseCase().collect { events ->
                _uiState.value = HomeUiState.Success(events)
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val trendingEvents: List<Event>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
