package com.example.myapplication.feature.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.TicketTier

@Composable
fun CheckoutRoute(
    event: Event,
    tiers: List<TicketTier>,
    quantities: Map<String, Int>,
    selectedSeats: List<EventSeat>,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: String,
    onBack: () -> Unit,
    onCommitPaymentMethod: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = viewModel()
) {
    LaunchedEffect(selectedPaymentMethod) {
        viewModel.load(selectedPaymentMethod)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.completedTxnRef) {
        if (state.completedTxnRef != null) {
            onCommitPaymentMethod("vnpay")
            onConfirm()
            viewModel.consumePaymentSuccess()
        }
    }

    CheckoutScreen(
        event = event,
        tiers = tiers,
        quantities = quantities,
        selectedSeats = selectedSeats,
        paymentMethods = paymentMethods,
        selectedPaymentMethod = state.selectedPaymentMethod,
        isCreatingPayment = state.isCreatingPayment,
        isVerifyingPayment = state.isVerifyingPayment,
        paymentUrl = state.paymentUrl,
        paymentError = state.paymentError,
        onBack = onBack,
        onSelectPayment = viewModel::selectPaymentMethod,
        onConfirm = { viewModel.startVnpayPayment(event, selectedSeats) },
        onPaymentReturn = viewModel::handleVnpayReturn,
        onPaymentDismiss = viewModel::cancelPayment,
        onPaymentWebError = viewModel::reportWebViewError,
        onDismissPaymentError = viewModel::dismissPaymentError,
        modifier = modifier
    )
}
