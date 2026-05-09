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
fun InfoPanel(rows: List<Pair<ImageVector, String>>) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            rows.forEachIndexed { index, row ->
                MetaRow(row.first, row.second)
                if (index != rows.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
fun ArtistRow(artists: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(artists) { artist ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircleAvatar(size = 56.dp)
                Text(artist, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TimelineColumn(timeline: List<EventMoment>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        timeline.forEach { moment ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Evergreen))
                    Box(modifier = Modifier.width(2.dp).height(34.dp).background(SoftLine))
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(moment.time, color = Evergreen, fontWeight = FontWeight.Bold)
                    Text(moment.title)
                }
            }
        }
    }
}

@Composable
fun VenueMapCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF5F0EC))
                    .border(1.dp, SoftLine, RoundedCornerShape(26.dp))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .width(220.dp)
                        .height(58.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Evergreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Zone A (VIP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MapZone("Zone B", Modifier.weight(1f))
                    MapZone("Zone B", Modifier.weight(1f))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(0.82f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE8E0DA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Zone C (Early Bird)", color = SoftText)
                }
            }
            Text("Venue map responsive: tren tablet co the doi sang layout 2 cot voi panel chon ve ben canh.", color = SoftText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun MapZone(label: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, SoftLine, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun NoticeCard(note: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, SoftLine)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Evergreen)
            Text(note, color = SoftText)
        }
    }
}

