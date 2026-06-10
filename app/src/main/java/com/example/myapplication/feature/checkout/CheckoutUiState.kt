package com.example.myapplication.feature.checkout

data class CheckoutUiState(
    val selectedPaymentMethod: String = "",
    val isCreatingPayment: Boolean = false,
    val isVerifyingPayment: Boolean = false,
    val paymentUrl: String? = null,
    val txnRef: String? = null,
    val completedTxnRef: String? = null,
    val paymentError: String? = null
)
