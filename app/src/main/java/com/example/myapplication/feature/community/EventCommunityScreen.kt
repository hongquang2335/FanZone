package com.example.myapplication.feature.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
        var searchVisible by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var selectedTab by remember { mutableStateOf("Bai viet") }
        var joined by remember { mutableStateOf(false) }
        val tabPosts = when (selectedTab) {
            "Anh" -> posts.filter { it.imageRes != null }
            "Su kien" -> posts.filter { it.eventId == event.id }
            "File", "Album" -> emptyList()
            else -> posts
        }
        val visiblePosts = if (searchQuery.isBlank()) {
            tabPosts
        } else {
            val query = searchQuery.trim()
            tabPosts.filter {
                it.content.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true) ||
                    it.topic.contains(query, ignoreCase = true)
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            EventCommunityTopBar(
                title = event.title,
                searchVisible = searchVisible,
                searchQuery = searchQuery,
                onBack = onBack
            ,
                onToggleSearch = {
                    searchVisible = !searchVisible
                    if (!searchVisible) searchQuery = ""
                },
                onSearchQueryChange = { searchQuery = it }
            )

            LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (isExpanded) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.widthIn(max = 420.dp).weight(0.9f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EventCommunityHeader(
                                event = event,
                                joined = joined,
                                onJoinToggle = { joined = !joined }
                            )
                            EventCommunityTabs(
                                selectedTab = selectedTab,
                                onSelectTab = { selectedTab = it }
                            )
                            ComposerCard()
                        }
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Phu hop nhat",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (visiblePosts.isEmpty()) {
                                Text("Chua co bai viet phu hop.", color = SoftText)
                            } else {
                                visiblePosts.forEach { post ->
                                    CommunityCard(post = post)
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    EventCommunityHeader(
                        event = event,
                        joined = joined,
                        onJoinToggle = { joined = !joined }
                    )
                }
                item {
                    EventCommunityTabs(
                        selectedTab = selectedTab,
                        onSelectTab = { selectedTab = it }
                    )
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
                items(visiblePosts) { post ->
                    CommunityCard(
                        post = post,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (visiblePosts.isEmpty()) {
                    item {
                        Text(
                            text = "Chua co bai viet phu hop.",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = SoftText
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun EventCommunityTopBar(
    title: String,
    searchVisible: Boolean,
    searchQuery: String,
    onBack: () -> Unit,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var moreExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lai")
        }
        if (searchVisible) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Tim trong nhom") }
            )
        } else {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
        Row {
            IconButton(onClick = onToggleSearch) {
                Icon(Icons.Default.Search, contentDescription = "Tim kiem")
            }
            androidx.compose.foundation.layout.Box {
                IconButton(onClick = { moreExpanded = true }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Them")
                }
                DropdownMenu(
                    expanded = moreExpanded,
                    onDismissRequest = { moreExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Xem thong tin nhom") },
                        onClick = { moreExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Chia se nhom") },
                        onClick = { moreExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Bao cao noi dung") },
                        onClick = { moreExpanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCommunityHeader(
    event: Event,
    joined: Boolean,
    onJoinToggle: () -> Unit
) {
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
                onClick = onJoinToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (joined) "Da tham gia nhom" else "Tham gia nhom")
            }
        }
    }
}

@Composable
private fun EventCommunityTabs(
    selectedTab: String,
    onSelectTab: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listOf("Bai viet", "Anh", "Su kien", "File", "Album")) { label ->
            FilterChip(
                selected = selectedTab == label,
                onClick = { onSelectTab(label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun ComposerCard() {
    var composerOpen by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    var anonymous by remember { mutableStateOf(false) }
    var feeling by remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .weight(1f)
                    .clickable { composerOpen = true },
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
            Icon(
                Icons.Default.Image,
                contentDescription = "Them anh",
                tint = Evergreen,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { composerOpen = true }
            )
        }
    }

    if (composerOpen) {
        NewPostDialog(
            draft = draft,
            onDraftChange = { draft = it },
            anonymous = anonymous,
            feeling = feeling,
            onToggleAnonymous = { anonymous = !anonymous },
            onToggleFeeling = { feeling = !feeling },
            onDismiss = { composerOpen = false },
            onPost = {
                draft = ""
                composerOpen = false
            }
        )
    }
}

@Composable
private fun NewPostDialog(
    draft: String,
    onDraftChange: (String) -> Unit,
    anonymous: Boolean,
    feeling: Boolean,
    onToggleAnonymous: () -> Unit,
    onToggleFeeling: () -> Unit,
    onDismiss: () -> Unit,
    onPost: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Dong")
                }
                Text(
                    text = "Bai viet moi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Them")
                }
            }

            Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar(size = 64.dp)
                    Text("Hong Quang", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item { ComposerActionChip(Icons.Default.MusicNote, "Nhac") }
                    item { ComposerActionChip(Icons.Default.People, "Moi nguoi") }
                    item { ComposerActionChip(Icons.Default.Mood, "Cam xuc") }
                }

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    BasicTextField(
                        value = draft,
                        onValueChange = onDraftChange,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color.Black)
                    )
                    if (draft.isBlank()) {
                        Text(
                            text = "Ban dang nghi gi?",
                            color = SoftText,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { ComposerLargeAction(Icons.Default.Image, "Thu vien") }
                item { ComposerLargeAction(Icons.Default.GifBox, "Anh GIF") }
                item { ComposerLargeAction(Icons.Default.StarBorder, "Cot moc") }
                item { ComposerLargeAction(Icons.Default.Videocam, "Truc tiep") }
            }

            Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = false,
                    onClick = {},
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    label = { Text("Tuy chinh") }
                )
                FilterChip(
                    selected = anonymous,
                    onClick = onToggleAnonymous,
                    leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    label = { Text(if (anonymous) "Bat" else "Tat") }
                )
                FilterChip(
                    selected = feeling,
                    onClick = onToggleFeeling,
                    leadingIcon = { Icon(Icons.Default.Mood, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    label = { Text(if (feeling) "Bat" else "Tat") }
                )
                Button(
                    onClick = onPost,
                    enabled = draft.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dang")
                }
            }
        }
    }
}

@Composable
private fun ComposerActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    AssistChip(
        onClick = {},
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
        label = { Text(label) }
    )
}

@Composable
private fun ComposerLargeAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    OutlinedButton(
        onClick = {},
        modifier = Modifier.size(width = 130.dp, height = 86.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null)
            Text(label)
        }
    }
}
