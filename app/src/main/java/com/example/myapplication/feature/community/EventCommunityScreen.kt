package com.example.myapplication.feature.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.SocialProfile

@Composable
fun EventCommunityScreen(
    event: Event,
    posts: List<CommunityPost>,
    profiles: List<SocialProfile>,
    currentAuthorName: String,
    currentUserId: String?,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenAuth: () -> Unit,
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
        val visiblePosts = posts
            .filterForTab(selectedTab, event.id)
            .filterForSearch(searchQuery)

        Column(modifier = Modifier.fillMaxSize()) {
            EventCommunityTopBar(
                title = event.title,
                searchVisible = searchVisible,
                searchQuery = searchQuery,
                onBack = onBack,
                onToggleSearch = {
                    searchVisible = !searchVisible
                    if (!searchVisible) searchQuery = ""
                },
                onSearchQueryChange = { searchQuery = it }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isExpanded) {
                    item {
                        ExpandedEventCommunityContent(
                            event = event,
                            joined = joined,
                            onJoinToggle = { joined = !joined },
                            selectedTab = selectedTab,
                            onSelectTab = { selectedTab = it },
                            visiblePosts = visiblePosts,
                            profiles = profiles,
                            currentAuthorName = currentAuthorName,
                            currentUserId = currentUserId,
                            onSharePost = onSharePost,
                            onToggleLike = onToggleLike,
                            onOpenProfile = onOpenProfile,
                            onOpenAuth = onOpenAuth
                        )
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
                        ComposerCard(eventId = event.id, eventTitle = event.title, onOpenAuth = onOpenAuth)
                    }
                    item {
                        FeedTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                    items(visiblePosts) { post ->
                        CommunityCard(
                            post = post,
                            authorProfile = profiles.firstOrNull { it.id == post.authorId },
                            currentAuthorName = currentAuthorName,
                            currentUserId = currentUserId,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onSharePost = onSharePost,
                            onToggleLike = { onToggleLike(post.id) },
                            onOpenProfile = onOpenProfile,
                            onOpenAuth = onOpenAuth
                        )
                    }
                    if (visiblePosts.isEmpty()) {
                        item {
                            EmptyPostsMessage(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedEventCommunityContent(
    event: Event,
    joined: Boolean,
    onJoinToggle: () -> Unit,
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    visiblePosts: List<CommunityPost>,
    profiles: List<SocialProfile>,
    currentAuthorName: String,
    currentUserId: String?,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenAuth: () -> Unit
) {
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
                onJoinToggle = onJoinToggle
            )
            EventCommunityTabs(
                selectedTab = selectedTab,
                onSelectTab = onSelectTab
            )
            ComposerCard(eventId = event.id, eventTitle = event.title, onOpenAuth = onOpenAuth)
        }
        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeedTitle()
            if (visiblePosts.isEmpty()) {
                EmptyPostsMessage()
            } else {
                visiblePosts.forEach { post ->
                    CommunityCard(
                        post = post,
                        authorProfile = profiles.firstOrNull { it.id == post.authorId },
                        currentAuthorName = currentAuthorName,
                        currentUserId = currentUserId,
                        onSharePost = onSharePost,
                        onToggleLike = { onToggleLike(post.id) },
                        onOpenProfile = onOpenProfile,
                        onOpenAuth = onOpenAuth
                    )
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
                Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
            }
            androidx.compose.foundation.layout.Box {
                IconButton(onClick = { moreExpanded = true }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Thêm")
                }
                DropdownMenu(
                    expanded = moreExpanded,
                    onDismissRequest = { moreExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Xem thông tin nhóm") },
                        onClick = { moreExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Chia sẻ nhóm") },
                        onClick = { moreExpanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Báo cáo nội dung") },
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
private fun FeedTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Phu hop nhat",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun EmptyPostsMessage(modifier: Modifier = Modifier) {
    Text(
        text = "Chua co bai viet phu hop.",
        modifier = modifier,
        color = SoftText
    )
}

private fun List<CommunityPost>.filterForTab(tab: String, eventId: String): List<CommunityPost> {
    return when (tab) {
        "Anh" -> filter { it.imageRes != null || it.mediaType?.startsWith("image/") == true }
        "Su kien" -> filter { it.eventId == eventId }
        "File", "Album" -> emptyList()
        else -> this
    }
}

private fun List<CommunityPost>.filterForSearch(query: String): List<CommunityPost> {
    if (query.isBlank()) return this
    val normalizedQuery = query.trim()
    return filter {
        it.content.contains(normalizedQuery, ignoreCase = true) ||
            it.author.contains(normalizedQuery, ignoreCase = true) ||
            it.topic.contains(normalizedQuery, ignoreCase = true)
    }
}
