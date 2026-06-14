package com.example.myapplication.feature.checkout

import com.example.myapplication.data.firebase.VnpayPaymentDataSource
import com.example.myapplication.data.firebase.VnpayPaymentSession
import com.example.myapplication.data.firebase.VnpayPaymentStatus
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class CheckoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockDataSource = mockk<VnpayPaymentDataSource>(relaxed = true)

    private val sampleEvent = Event(
        id = "event-123",
        title = "Test Concert",
        subtitle = "Sub",
        schedule = "Time",
        venue = "Venue",
        city = "City",
        description = "Desc",
        artists = emptyList(),
        timeline = emptyList(),
        notices = emptyList(),
        imageRes = 0
    )

    private val sampleSeats = listOf(
        EventSeat(
            id = "seat-1",
            eventId = "event-123",
            seatId = "A1",
            zoneId = "VIP",
            price = 100000,
            status = "available"
        )
    )

    @Test
    fun selectPaymentMethod_updatesSelectedPaymentMethod() {
        val viewModel = CheckoutViewModel(mockDataSource)
        viewModel.load("visa")
        viewModel.selectPaymentMethod("bank")
        assertEquals("bank", viewModel.uiState.value.selectedPaymentMethod)
    }

    @Test
    fun startVnpayPayment_success_updatesPaymentUrlAndTxnRef() {
        val viewModel = CheckoutViewModel(mockDataSource)
        coEvery {
            mockDataSource.createPayment("event-123", sampleSeats)
        } returns VnpayPaymentSession("https://vnpay.vn/pay", "txn-999")

        viewModel.startVnpayPayment(sampleEvent, sampleSeats)

        val state = viewModel.uiState.value
        assertEquals("https://vnpay.vn/pay", state.paymentUrl)
        assertEquals("txn-999", state.txnRef)
        assertNull(state.paymentError)
    }

    @Test
    fun startVnpayPayment_failure_updatesError() {
        val viewModel = CheckoutViewModel(mockDataSource)
        coEvery {
            mockDataSource.createPayment("event-123", sampleSeats)
        } throws RuntimeException("Seat already taken")

        viewModel.startVnpayPayment(sampleEvent, sampleSeats)

        val state = viewModel.uiState.value
        assertNull(state.paymentUrl)
        assertNull(state.txnRef)
        assertEquals("Seat already taken", state.paymentError)
    }

    @Test
    fun handleVnpayReturn_success_pollsStatusAndCompletes() {
        val viewModel = CheckoutViewModel(mockDataSource)
        coEvery {
            mockDataSource.createPayment("event-123", sampleSeats)
        } returns VnpayPaymentSession("https://vnpay.vn/pay", "txn-999")

        coEvery {
            mockDataSource.getPaymentStatus("txn-999")
        } returns VnpayPaymentStatus("txn-999", "success", "00")

        viewModel.startVnpayPayment(sampleEvent, sampleSeats)
        assertEquals("txn-999", viewModel.uiState.value.txnRef)

        viewModel.handleVnpayReturn("txn-999", "00")

        val state = viewModel.uiState.value
        assertEquals("txn-999", state.completedTxnRef)
        assertNull(state.txnRef)
        assertNull(state.paymentUrl)
        assertNull(state.paymentError)
    }

    @Test
    fun handleVnpayReturn_failureResponseCode_showsFailure() {
        val viewModel = CheckoutViewModel(mockDataSource)
        coEvery {
            mockDataSource.createPayment("event-123", sampleSeats)
        } returns VnpayPaymentSession("https://vnpay.vn/pay", "txn-999")

        viewModel.startVnpayPayment(sampleEvent, sampleSeats)

        viewModel.handleVnpayReturn("txn-999", "99")

        val state = viewModel.uiState.value
        assertNull(state.completedTxnRef)
        assertNull(state.txnRef)
        assertTrue(state.paymentError!!.contains("Đã có lỗi xảy ra"))
    }
}
