package com.example.myapplication.feature.booking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier

@Composable
fun BookingRoute(
    event: Event,
    tiers: List<TicketTier>,
    initialQuantities: Map<String, Int>,
    onBack: () -> Unit,
    onCommitQuantities: (Map<String, Int>) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = viewModel()
) {
    LaunchedEffect(event.id, tiers, initialQuantities) {
        viewModel.load(event, tiers, initialQuantities)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val loadedEvent = state.event ?: event

    BookingScreen(
        event = loadedEvent,
        tiers = state.tiers,
        quantities = state.quantities,
        onBack = onBack,
        onChangeQuantity = viewModel::setQuantity,
        onContinue = {
            onCommitQuantities(state.quantities)
            onContinue()
        },
        modifier = modifier
    )
}
