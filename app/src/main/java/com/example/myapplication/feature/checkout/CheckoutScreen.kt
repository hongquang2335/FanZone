package com.example.myapplication.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.BookingFooter
import com.example.myapplication.core.designsystem.component.CheckoutSummaryCard
import com.example.myapplication.core.designsystem.component.PaymentMethodCard
import com.example.myapplication.core.designsystem.component.formatPrice

@Composable
fun CheckoutScreen(
    event: Event,
    tiers: List<TicketTier>,
    quantities: Map<String, Int>,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: String,
    onBack: () -> Unit,
    onSelectPayment: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp
        val activeLines = tiers.filter { (quantities[it.id] ?: 0) > 0 }
        val total = activeLines.sumOf { it.price * (quantities[it.id] ?: 0) }

        Scaffold(
            topBar = { AppTopBar(title = "Thanh toan", onBack = onBack) },
            bottomBar = {
                BookingFooter(
                    label = "Xac nhan thanh toan",
                    priceLabel = formatPrice(total),
                    enabled = total > 0,
                    onClick = onConfirm
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CheckoutSummaryCard(event, activeLines, quantities)
                    PaymentPanel(paymentMethods, selectedPaymentMethod, onSelectPayment, Modifier.weight(1f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { CheckoutSummaryCard(event, activeLines, quantities) }
                    item { PaymentPanel(paymentMethods, selectedPaymentMethod, onSelectPayment) }
                }
            }
        }
    }
}

@Composable
private fun PaymentPanel(
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: String,
    onSelectPayment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Phuong thuc thanh toan", style = MaterialTheme.typography.titleLarge)
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    label = method.label,
                    subtitle = method.subtitle,
                    selected = method.id == selectedPaymentMethod,
                    onSelect = { onSelectPayment(method.id) }
                )
            }
        }
    }
}

