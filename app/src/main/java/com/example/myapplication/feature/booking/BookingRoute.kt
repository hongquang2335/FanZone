package com.example.myapplication.feature.booking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat

@Composable
fun BookingRoute(
    event: Event,
    onBack: () -> Unit,
    onContinue: (List<EventSeat>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(event.id) {
        viewModel.load(event)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val loadedEvent = state.event ?: event

    BookingScreen(
        event = loadedEvent,
        seats = state.seats,
        selectedSeatIds = state.selectedSeatIds,
        isLoading = state.isLoading,
        error = state.error,
        onBack = onBack,
        onToggleSeat = viewModel::toggleSeat,
        onRemoveSeat = viewModel::removeSeat,
        onContinue = { onContinue(state.selectedSeats) },
        modifier = modifier
    )
}
