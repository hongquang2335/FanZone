package com.example.myapplication.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.firebase.VnpayPaymentDataSource
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val paymentDataSource: VnpayPaymentDataSource = VnpayPaymentDataSource()
) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun load(selectedPaymentMethod: String) {
        _uiState.update { it.copy(selectedPaymentMethod = selectedPaymentMethod) }
    }

    fun selectPaymentMethod(methodId: String) {
        _uiState.update { it.copy(selectedPaymentMethod = methodId) }
    }

    fun startVnpayPayment(event: Event, selectedSeats: List<EventSeat>) {
        if (selectedSeats.isEmpty() || _uiState.value.isCreatingPayment) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCreatingPayment = true,
                    paymentError = null,
                    completedTxnRef = null
                )
            }

            runCatching {
                paymentDataSource.createPayment(event.id, selectedSeats)
            }.onSuccess { session ->
                _uiState.update {
                    it.copy(
                        isCreatingPayment = false,
                        paymentUrl = session.paymentUrl,
                        txnRef = session.txnRef
                    )
                }
            }.onFailure { error ->
                showFailure(error.localizedMessage)
            }
        }
    }

    fun handleVnpayReturn(txnRef: String, responseCode: String) {
        val expectedTxnRef = _uiState.value.txnRef
        _uiState.update { it.copy(paymentUrl = null) }

        if (txnRef.isBlank() || txnRef != expectedTxnRef || responseCode != "00") {
            showFailure()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isVerifyingPayment = true, paymentError = null)
            }

            repeat(PAYMENT_STATUS_ATTEMPTS) {
                val status = runCatching {
                    paymentDataSource.getPaymentStatus(txnRef)
                }.getOrNull()

                when (status?.status) {
                    "success" -> {
                        _uiState.update {
                            it.copy(
                                isVerifyingPayment = false,
                                completedTxnRef = txnRef,
                                txnRef = null
                            )
                        }
                        return@launch
                    }
                    "failed" -> {
                        showFailure()
                        return@launch
                    }
                }
                delay(PAYMENT_STATUS_POLL_DELAY_MS)
            }

            showFailure(
                "Chưa thể xác minh kết quả giao dịch. Vui lòng kiểm tra lại hoặc thử lại."
            )
        }
    }

    fun cancelPayment() {
        if (_uiState.value.paymentUrl != null) {
            showFailure("Giao dịch đã bị hủy trước khi hoàn tất.")
        }
    }

    fun reportWebViewError() {
        showFailure("Không thể tải trang thanh toán VNPAY. Vui lòng thử lại.")
    }

    fun dismissPaymentError() {
        _uiState.update { it.copy(paymentError = null) }
    }

    fun consumePaymentSuccess() {
        _uiState.update { it.copy(completedTxnRef = null) }
    }

    private fun showFailure(message: String? = null) {
        _uiState.update {
            it.copy(
                isCreatingPayment = false,
                isVerifyingPayment = false,
                paymentUrl = null,
                txnRef = null,
                paymentError = message ?: DEFAULT_FAILURE_MESSAGE
            )
        }
    }

    private companion object {
        const val PAYMENT_STATUS_ATTEMPTS = 20
        const val PAYMENT_STATUS_POLL_DELAY_MS = 1_000L
        const val DEFAULT_FAILURE_MESSAGE =
            "Đã có lỗi xảy ra trong quá trình xử lý giao dịch. Vui lòng thử lại."
    }
}
