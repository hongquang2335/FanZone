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
fun SectionHeader(title: String, subtitle: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            subtitle?.let {
                Text(it, color = SoftText, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (subtitle != null && subtitle.startsWith("Xem")) {
            Text(subtitle, color = Evergreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CircleAvatar(size: Dp = 44.dp) {
    Image(
        painter = painterResource(R.drawable.fan_zone_avatar),
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
    )
}

@Composable
fun HomeSwitchChip(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (active) Evergreen else Color.Transparent)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (active) Color.White else Ink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CategoryPill(emoji: String, label: String) {
    Column(
        modifier = Modifier
            .width(74.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun FlowCategoryRow(categories: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.chunked(4).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { (emoji, label) ->
                    CategoryPill(emoji, label)
                }
            }
        }
    }
}

