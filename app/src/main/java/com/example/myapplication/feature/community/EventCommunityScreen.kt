package com.example.myapplication.feature.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Public
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event

@Composable
fun EventCommunityScreen(
    event: Event,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>> = emptyMap(),
    currentAuthorName: String,
    currentAuthorAvatarUrl: String?,
    currentUserId: String?,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (CommunityPost) -> Unit = {},
    onBack: () -> Unit,
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isExpanded = maxWidth >= 720.dp

        Column(modifier = Modifier.fillMaxSize()) {
            EventCommunityTopBar(
                title = event.title,
                onBack = onBack,
                unreadNotificationCount = unreadNotificationCount,
                onOpenNotifications = onOpenNotifications
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!errorMessage.isNullOrBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFEF4444), shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFB91C1C),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                if (isExpanded) {
                    item {
                        ExpandedEventCommunityContent(
                            event = event,
                            posts = posts,
                            commentsByPostId = commentsByPostId,
                            currentAuthorName = currentAuthorName,
                            currentAuthorAvatarUrl = currentAuthorAvatarUrl,
                            currentUserId = currentUserId,
                            onSharePost = onSharePost,
                            onToggleLike = onToggleLike,
                            onToggleFollow = onToggleFollow,
                            onOpenComments = onOpenComments,
                            onAddComment = onAddComment,
                            onOpenAuth = onOpenAuth,
                            onOpenProfile = onOpenProfile,
                            onDeletePost = onDeletePost,
                            onEditPost = onEditPost
                        )
                    }
                } else {
                    item { EventCommunityHeader(event = event) }
                    item {
                        ComposerCard(
                            eventId = event.id,
                            eventTitle = event.title,
                            currentAuthorAvatarUrl = currentAuthorAvatarUrl,
                            onOpenAuth = onOpenAuth
                        )
                    }
                    items(posts) { post ->
                        CommunityCard(
                            post = post,
                            currentAuthorName = currentAuthorName,
                            currentUserId = currentUserId,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onSharePost = onSharePost,
                            onToggleLike = { onToggleLike(post.id) },
                            onToggleFollow = onToggleFollow,
                            comments = commentsByPostId[post.id].orEmpty(),
                            onOpenComments = { onOpenComments(post.id) },
                            onAddComment = { text -> onAddComment(post.id, text) },
                            onOpenAuth = onOpenAuth,
                            onOpenProfile = onOpenProfile,
                            onDeletePost = { onDeletePost(post.id) },
                            onEditPost = { onEditPost(post) }
                        )
                    }
                    if (posts.isEmpty()) {
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
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    currentAuthorName: String,
    currentAuthorAvatarUrl: String?,
    currentUserId: String?,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onEditPost: (CommunityPost) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EventCommunityHeader(event = event)
            ComposerCard(
                eventId = event.id,
                eventTitle = event.title,
                currentAuthorAvatarUrl = currentAuthorAvatarUrl,
                onOpenAuth = onOpenAuth
            )
        }
        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (posts.isEmpty()) {
                EmptyPostsMessage()
            } else {
                posts.forEach { post ->
                    CommunityCard(
                        post = post,
                        currentAuthorName = currentAuthorName,
                        currentUserId = currentUserId,
                        onSharePost = onSharePost,
                        onToggleLike = { onToggleLike(post.id) },
                        onToggleFollow = onToggleFollow,
                        comments = commentsByPostId[post.id].orEmpty(),
                        onOpenComments = { onOpenComments(post.id) },
                        onAddComment = { text -> onAddComment(post.id, text) },
                        onOpenAuth = onOpenAuth,
                        onOpenProfile = onOpenProfile,
                        onDeletePost = { onDeletePost(post.id) },
                        onEditPost = { onEditPost(post) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCommunityTopBar(
    title: String,
    onBack: () -> Unit,
    unreadNotificationCount: Int,
    onOpenNotifications: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        Box(modifier = Modifier.size(48.dp)) {
            IconButton(onClick = onOpenNotifications) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Thông báo",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }
            if (unreadNotificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(18.dp)
                        .background(MaterialTheme.colorScheme.error, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadNotificationCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCommunityHeader(event: Event) {
    Column(modifier = Modifier.background(Color.White)) {
        EventCommunityBanner(event = event)
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

        }
    }
}

@Composable
private fun EventCommunityBanner(event: Event) {
    when {
        !event.imageUrl.isNullOrBlank() -> {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
        }

        event.imageRes != 0 -> {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Color(0xFFF1F5F9))
            )
        }
    }
}

@Composable
private fun FeedTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Phù hợp nhất",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun EmptyPostsMessage(modifier: Modifier = Modifier) {
    Text(
        text = "Chưa có bài viết phù hợp.",
        modifier = modifier,
        color = SoftText
    )
}
