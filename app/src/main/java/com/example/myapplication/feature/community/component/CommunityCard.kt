package com.example.myapplication.feature.community.component

import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.DeleteConfirmDialog
import com.example.myapplication.core.designsystem.component.LoginRequiredDialog
import com.example.myapplication.core.designsystem.theme.Danger
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.util.AppStrings
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost

@Composable
fun CommunityCard(
    post: CommunityPost,
    modifier: Modifier = Modifier,
    currentAuthorName: String = "Bạn",
    currentUserId: String? = null,
    comments: List<CommunityComment> = emptyList(),
    onOpenEventCommunity: (String) -> Unit = {},
    onSharePost: (CommunityPost, String) -> Unit = { _, _ -> },
    onToggleLike: () -> Unit = {},
    onToggleFollow: (String) -> Unit = {},
    onOpenComments: () -> Unit = {},
    onAddComment: (String) -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onOpenProfile: (String) -> Unit = {},
    onDeletePost: () -> Unit = {},
    onEditPost: () -> Unit = {},
    showCommentsInitially: Boolean = false,
    onCommentsDismissed: () -> Unit = {}
) {
    var liked by remember(post.id, currentUserId, post.likedBy) { mutableStateOf(post.isLikedByUser(currentUserId)) }
    var likeCount by remember(post.id, post.likes) { mutableStateOf(post.likes) }
    var commentCount by remember(post.id) { mutableStateOf(post.comments) }
    var commentOpen by remember(post.id, showCommentsInitially) { mutableStateOf(showCommentsInitially) }
    var shareOpen by remember(post.id) { mutableStateOf(false) }
    var shareCount by remember(post.id) { mutableStateOf(post.shareCount) }
    var showAuthPrompt by remember { mutableStateOf(false) }
    val sharedPost = post.sharedPost

    val isMyPost = post.authorId != null && post.authorId == currentUserId
    var menuExpanded by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showDeleteConfirm by remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (sharedPost != null) {
            SharedPostPreview(
                post = post,
                share = sharedPost,
                isMyPost = isMyPost,
                liked = liked,
                likeCount = likeCount,
                commentCount = commentCount,
                onLikeClick = {
                    if (currentUserId == null) {
                        showAuthPrompt = true
                    } else {
                        liked = !liked
                        likeCount += if (liked) 1 else -1
                        onToggleLike()
                    }
                },
                onCommentClick = {
                    if (currentUserId == null) {
                        showAuthPrompt = true
                    } else {
                        onOpenComments()
                        commentOpen = true
                    }
                },
                onShareClick = {
                    if (currentUserId == null) {
                        showAuthPrompt = true
                    } else {
                        shareOpen = true
                    }
                },
                onOpenProfile = onOpenProfile,
                onOpenEventCommunity = onOpenEventCommunity,
                onEditPost = onEditPost,
                onDeletePost = { showDeleteConfirm = true }
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircleAvatar(
                            imageUrl = post.authorAvatarUrl,
                            modifier = Modifier.clickable(enabled = post.authorId != null) {
                                post.authorId?.let(onOpenProfile)
                            }
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (post.eventTitle != null) {
                                Text(
                                    text = post.eventTitle,
                                    modifier = Modifier.clickable(enabled = post.eventId != null) {
                                        post.eventId?.let(onOpenEventCommunity)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                PostMetaLine(
                                    author = post.author,
                                    timeLabel = post.postTimeLabel(),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    onAuthorClick = post.authorId?.let { id -> { onOpenProfile(id) } }
                                )
                            } else {
                                Text(
                                    text = post.author,
                                    modifier = Modifier.clickable(enabled = post.authorId != null) {
                                        post.authorId?.let(onOpenProfile)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                PostMetaLine(
                                    timeLabel = post.postTimeLabel(),
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        if (isMyPost) {
                            Box {
                                androidx.compose.material3.IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }
                                ) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Chỉnh sửa") },
                                        onClick = {
                                            menuExpanded = false
                                            onEditPost()
                                        }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Xóa", color = Danger) },
                                        onClick = {
                                            menuExpanded = false
                                            showDeleteConfirm = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                    ExpandableText(text = post.content, style = MaterialTheme.typography.bodyLarge)
                    CommunityPostMedia(post = post, height = 190.dp)
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        PostAction(
                            icon = Icons.Default.FavoriteBorder,
                            label = "$likeCount",
                            tint = if (liked) Danger else SoftText,
                            onClick = {
                                if (currentUserId == null) {
                                    showAuthPrompt = true
                                } else {
                                    liked = !liked
                                    likeCount += if (liked) 1 else -1
                                    onToggleLike()
                                }
                            }
                        )
                        PostAction(
                            icon = Icons.AutoMirrored.Outlined.Chat,
                            label = "$commentCount",
                            onClick = {
                                if (currentUserId == null) {
                                    showAuthPrompt = true
                                } else {
                                    onOpenComments()
                                    commentOpen = true
                                }
                            }
                        )
                        PostAction(
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            label = "Chia sẻ",
                            onClick = {
                                if (currentUserId == null) {
                                    showAuthPrompt = true
                                } else {
                                    shareOpen = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        DeleteConfirmDialog(
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                showDeleteConfirm = false
                onDeletePost()
            }
        )
    }

    if (showAuthPrompt) {
        LoginRequiredDialog(
            onDismiss = { showAuthPrompt = false },
            onLogin = onOpenAuth,
            subtitleText = AppStrings.Community.AUTH_REQUIRED_DESC
        )
    }

    if (commentOpen) {
        FirestoreCommentsDialog(
            post = post,
            comments = comments,
            likeCount = likeCount,
            shareCount = shareCount,
            authorName = currentAuthorName,
            onDismiss = {
                commentOpen = false
                onCommentsDismissed()
            },
            onAddComment = { text ->
                commentCount++
                onAddComment(text)
            }
        )
    }

    if (shareOpen) {
        ShareDialog(
            post = post,
            authorName = currentAuthorName,
            onDismiss = { shareOpen = false },
            onShareNow = { caption ->
                shareCount++
                onSharePost(post, caption)
                shareOpen = false
            }
        )
    }
}
