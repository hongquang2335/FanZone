package com.example.myapplication.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.myapplication.core.designsystem.component.LoginRequiredDialog
import com.example.myapplication.core.util.AppStrings
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.feature.community.component.CommunityCard
import com.example.myapplication.core.designsystem.component.SectionHeader

@Composable
fun CommunityScreen(
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>> = emptyMap(),
    currentAuthorName: String,
    currentAuthorAvatarUrl: String?,
    currentUserId: String?,
    onOpenEvent: (String) -> Unit,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    errorMessage: String? = null,
    targetCommentsPostId: String? = null,
    onCommentsDismissed: () -> Unit = {},
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAuthPrompt by remember { mutableStateOf(false) }
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp
        val listState = rememberLazyListState()

        LaunchedEffect(targetCommentsPostId) {
            if (targetCommentsPostId != null) {
                val index = posts.indexOfFirst { it.id == targetCommentsPostId }
                if (index >= 0) {
                    var targetIndex = 2
                    if (!errorMessage.isNullOrBlank()) targetIndex++
                    targetIndex += index
                    listState.animateScrollToItem(targetIndex)
                }
                onCommentsDismissed()
            }
        }

        @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Cộng đồng",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = {
                                if (currentUserId == null) {
                                    showAuthPrompt = true
                                } else {
                                    onOpenNotifications()
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = "Thông báo",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(28.dp)
                                )
                                if (unreadNotificationCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                            .size(10.dp)
                                            .background(MaterialTheme.colorScheme.error, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
                if (!errorMessage.isNullOrBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
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
                item { ComposerCard(currentAuthorAvatarUrl = currentAuthorAvatarUrl, onOpenAuth = onOpenAuth) }
                if (posts.isEmpty()) {
                    item {
                        com.example.myapplication.core.designsystem.component.EmptyStateCard(
                            title = "Chưa có bài viết nào",
                            body = "Hãy là người đầu tiên chia sẻ bài viết với cộng đồng!"
                        )
                    }
                } else {
                    if (isExpanded) {
                        item { SectionHeader("Dòng bài viết nổi bật", "Có sự kiện kèm tag") }
                    }
                    if (isExpanded) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                posts.chunked(2).forEach { group ->
                                    androidx.compose.foundation.layout.Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        group.forEach { post ->
                                            CommunityCard(
                                                post = post,
                                                currentAuthorName = currentAuthorName,
                                                currentUserId = currentUserId,
                                                onOpenEventCommunity = onOpenEvent,
                                                onSharePost = onSharePost,
                                                onToggleLike = { onToggleLike(post.id) },
                                                onToggleFollow = onToggleFollow,
                                                comments = commentsByPostId[post.id].orEmpty(),
                                                onOpenComments = { onOpenComments(post.id) },
                                                onAddComment = { text -> onAddComment(post.id, text) },
                                                onOpenAuth = onOpenAuth,
                                                onOpenProfile = onOpenProfile,
                                                onDeletePost = { onDeletePost(post.id) },
                                                onEditPost = { onEditPost(post) },
                                                showCommentsInitially = false,
                                                onCommentsDismissed = onCommentsDismissed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(posts) { post ->
                            CommunityCard(
                                post = post,
                                currentAuthorName = currentAuthorName,
                                currentUserId = currentUserId,
                                onOpenEventCommunity = onOpenEvent,
                                onSharePost = onSharePost,
                                onToggleLike = { onToggleLike(post.id) },
                                onToggleFollow = onToggleFollow,
                                comments = commentsByPostId[post.id].orEmpty(),
                                onOpenComments = { onOpenComments(post.id) },
                                onAddComment = { text -> onAddComment(post.id, text) },
                                onOpenAuth = onOpenAuth,
                                onOpenProfile = onOpenProfile,
                                onDeletePost = { onDeletePost(post.id) },
                                onEditPost = { onEditPost(post) },
                                showCommentsInitially = false,
                                onCommentsDismissed = onCommentsDismissed
                            )
                        }
                    }
                }
            }
        }
        if (showAuthPrompt) {
            LoginRequiredDialog(
                onDismiss = { showAuthPrompt = false },
                onLogin = onOpenAuth,
                subtitleText = "Bạn cần đăng nhập để sử dụng chức năng thông báo."
            )
        }
    }
}
