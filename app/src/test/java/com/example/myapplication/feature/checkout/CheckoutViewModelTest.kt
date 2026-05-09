package com.example.myapplication.feature.checkout

import org.junit.Assert.assertEquals
import org.junit.Test

class CheckoutViewModelTest {
    @Test
    fun selectPaymentMethod_updatesSelectedPaymentMethod() {
        val viewModel = CheckoutViewModel()
        viewModel.load("visa")

        viewModel.selectPaymentMethod("bank")

        assertEquals("bank", viewModel.uiState.value.selectedPaymentMethod)
    }
}
