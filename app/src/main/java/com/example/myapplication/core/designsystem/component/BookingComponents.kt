package com.example.myapplication.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.core.designsystem.theme.Danger
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.EvergreenDark
import com.example.myapplication.core.designsystem.theme.Ink
import com.example.myapplication.core.designsystem.theme.LavenderWash
import com.example.myapplication.core.designsystem.theme.MintWash
import com.example.myapplication.core.designsystem.theme.PeachWash
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.designsystem.theme.SurfaceCard
import com.example.myapplication.core.designsystem.theme.Warning
import com.example.myapplication.core.util.formatVnd
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus


@Composable
fun TierCard(
    tier: TicketTier,
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (tier.status) {
                                        TierStatus.AVAILABLE -> Evergreen
                                        TierStatus.LIMITED -> Warning
                                        TierStatus.SOLD_OUT -> SoftLine
                                    }
                                )
                        )
                        Text(tier.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(tier.benefits, color = SoftText)
                    if (tier.status == TierStatus.SOLD_OUT) {
                        Text(
                            formatPrice(tier.price),
                            color = SoftText,
                            textDecoration = TextDecoration.LineThrough,
                            style = MaterialTheme.typography.titleMedium
                        )
                    } else {
                        Text(formatPrice(tier.price), color = Evergreen, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }
                when (tier.status) {
                    TierStatus.LIMITED -> StateTag("Sap het", PeachWash, Warning)
                    TierStatus.SOLD_OUT -> StateTag("Het ve", Color(0xFFF3F0EF), SoftText)
                    TierStatus.AVAILABLE -> StateTag("Mo ban", MintWash, Evergreen)
                }
            }
            QuantityStepper(
                quantity = quantity,
                enabled = tier.status != TierStatus.SOLD_OUT,
                onDecrease = onDecrease,
                onIncrease = onIncrease
            )
        }
    }
}

@Composable
fun QuantityStepper(quantity: Int, enabled: Boolean, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Surface(shape = RoundedCornerShape(22.dp), color = SurfaceCard, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDecrease, enabled = enabled && quantity > 0) {
                Icon(Icons.Outlined.Remove, contentDescription = "Giam")
            }
            Text("$quantity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = onIncrease, enabled = enabled) {
                Icon(Icons.Outlined.Add, contentDescription = "Tang")
            }
        }
    }
}

@Composable
fun CheckoutSummaryCard(event: Event, activeLines: List<TicketTier>, quantities: Map<String, Int>) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Tom tat don hang", style = MaterialTheme.typography.titleLarge)
            Text(event.title, fontWeight = FontWeight.Bold)
            activeLines.forEach { tier ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${tier.name} x${quantities[tier.id] ?: 0}")
                    Text(formatPrice(tier.price * (quantities[tier.id] ?: 0)), fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tong thanh toan", style = MaterialTheme.typography.titleMedium)
                Text(
                    formatPrice(activeLines.sumOf { it.price * (quantities[it.id] ?: 0) }),
                    style = MaterialTheme.typography.titleLarge,
                    color = Evergreen,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    label: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) MintWash else SurfaceCard,
        border = BorderStroke(1.dp, if (selected) Evergreen else SoftLine)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = SoftText, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = if (selected) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (selected) Evergreen else SoftText
            )
        }
    }
}

