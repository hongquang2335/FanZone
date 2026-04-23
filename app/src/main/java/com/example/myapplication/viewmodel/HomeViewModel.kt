package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FanZoneRepository
import com.example.myapplication.data.MockFanZoneRepository
import com.example.myapplication.model.FanEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val events: List<FanEvent>,
    val searchQuery: String = "",
    val selectedSection: String = "Xu hướng"
) {
    val filteredEvents: List<FanEvent> =
        events.filter {
            searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.city.contains(searchQuery, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
        }
}

class HomeViewModel(
    private val repository: FanZoneRepository = MockFanZoneRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<ScreenState<HomeUiState>>(ScreenState.Loading)
    val state: StateFlow<ScreenState<HomeUiState>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.featuredEvents().collect { events ->
                _state.value = if (events.isEmpty()) {
                    ScreenState.Empty("Chưa có sự kiện phù hợp.")
                } else {
                    ScreenState.Ready(HomeUiState(events))
                }
            }
        }
    }

    fun updateSearch(query: String) {
        val current = (_state.value as? ScreenState.Ready)?.data ?: return
        _state.value = ScreenState.Ready(current.copy(searchQuery = query))
    }

    fun selectSection(section: String) {
        val current = (_state.value as? ScreenState.Ready)?.data ?: return
        _state.value = ScreenState.Ready(current.copy(selectedSection = section))
    }
}
