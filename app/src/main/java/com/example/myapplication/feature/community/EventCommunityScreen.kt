package com.example.myapplication.feature.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event

@Composable
fun EventCommunityScreen(
    event: Event,
    posts: List<CommunityPost>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isExpanded = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                EventCommunityTopBar(onBack = onBack)
            }
            item {
                EventCommunityHeader(event = event)
            }
            item {
                EventCommunityTabs()
            }
            item {
                ComposerCard()
            }
            item {
                Text(
                    text = "Phu hop nhat",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            if (isExpanded) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        posts.chunked(2).forEach { group ->
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                group.forEach { post -> CommunityCard(post) }
                            }
                        }
                    }
                }
            } else {
                items(posts) { post ->
                    CommunityCard(
                        post = post,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCommunityTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lai")
        }
        Row {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, contentDescription = "Tim kiem")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreHoriz, contentDescription = "Them")
            }
        }
    }
}

@Composable
private fun EventCommunityHeader(event: Event) {
    Column(modifier = Modifier.background(Color.White)) {
        Image(
            painter = painterResource(event.imageRes),
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, tint = SoftText, modifier = Modifier.size(18.dp))
                Text("Nhom cong khai", color = SoftText)
                Text("- 1.070 thanh vien", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tham gia nhom")
            }
        }
    }
}

@Composable
private fun EventCommunityTabs() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listOf("Bai viet", "Anh", "Su kien", "File", "Album")) { label ->
            AssistChip(onClick = {}, label = { Text(label) })
        }
    }
}

@Composable
private fun ComposerCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircleAvatar(size = 44.dp)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF3F5F7),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftLine)
            ) {
                Text(
                    text = "Ban viet gi di...",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    color = SoftText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(Icons.Default.Image, contentDescription = null, tint = Evergreen, modifier = Modifier.size(28.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AssistChip(onClick = {}, label = { Text("Bai viet an danh") })
            AssistChip(onClick = {}, label = { Text("Cam xuc") })
        }
    }
}
