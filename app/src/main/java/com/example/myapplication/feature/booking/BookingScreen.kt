package com.example.myapplication.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.BookingFooter
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.component.TierCard
import com.example.myapplication.core.designsystem.component.VenueMapCard
import com.example.myapplication.core.designsystem.component.formatPrice

@Composable
fun BookingScreen(
    event: Event,
    tiers: List<TicketTier>,
    quantities: Map<String, Int>,
    onBack: () -> Unit,
    onChangeQuantity: (String, Int) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp
        val total = tiers.sumOf { tier -> tier.price * (quantities[tier.id] ?: 0) }
        val hasSelection = total > 0

        Scaffold(
            topBar = { AppTopBar(title = "Dat ve", onBack = onBack) },
            bottomBar = {
                BookingFooter(
                    label = "Tien hanh thanh toan",
                    priceLabel = if (hasSelection) formatPrice(total) else null,
                    enabled = hasSelection,
                    onClick = onContinue
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionHeader("So do khu vuc", "Su kien: ${event.title}")
                        VenueMapCard(Modifier.fillMaxWidth())
                    }
                    TierColumn(tiers, quantities, onChangeQuantity, Modifier.weight(1.2f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { SectionHeader("So do khu vuc", event.title) }
                    item { VenueMapCard() }
                    item { SectionHeader("Chon ve", null) }
                    item { TierColumn(tiers, quantities, onChangeQuantity) }
                }
            }
        }
    }
}

@Composable
private fun TierColumn(
    tiers: List<TicketTier>,
    quantities: Map<String, Int>,
    onChangeQuantity: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        tiers.forEach { tier ->
            val quantity = quantities[tier.id] ?: 0
            TierCard(
                tier = tier,
                quantity = quantity,
                onDecrease = { onChangeQuantity(tier.id, (quantity - 1).coerceAtLeast(0)) },
                onIncrease = { onChangeQuantity(tier.id, (quantity + 1).coerceAtMost(8)) }
            )
        }
    }
}

