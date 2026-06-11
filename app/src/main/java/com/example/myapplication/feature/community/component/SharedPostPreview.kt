package com.example.myapplication.feature.community.component

import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.theme.Danger
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.SharedCommunityPost

@Composable
fun SharedPostPreview(
    post: CommunityPost,
    share: SharedCommunityPost,
    isMyPost: Boolean,
    liked: Boolean,
    likeCount: Int,
    commentCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenEventCommunity: (String) -> Unit,
    onEditPost: () -> Unit,
    onDeletePost: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

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
                    Text(
                        text = post.postTimeLabel(),
                        color = SoftText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                                    onDeletePost()
                                }
                            )
                        }
                    }
                }
            }

            if (post.content.isNotBlank()) {
                ExpandableText(text = post.content, style = MaterialTheme.typography.bodyLarge)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (share.isDeleted) Color(0xFFF8F9FA) else Color.White,
                border = BorderStroke(1.dp, SoftLine)
            ) {
                if (share.isDeleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = SoftText,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Nội dung này hiện không khả dụng",
                                color = SoftText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val hasMedia = share.mediaItems.isNotEmpty()
                        Column(
                            modifier = Modifier.padding(
                                start = 14.dp,
                                top = 14.dp,
                                end = 14.dp,
                                bottom = if (hasMedia) 0.dp else 14.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircleAvatar(
                                    size = 36.dp,
                                    imageUrl = share.authorAvatarUrl,
                                    modifier = Modifier.clickable(enabled = share.authorId != null) {
                                        share.authorId?.let(onOpenProfile)
                                    }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = share.eventTitle ?: share.author,
                                        modifier = Modifier.clickable(
                                            enabled = if (share.eventTitle != null) share.eventId != null else share.authorId != null
                                        ) {
                                            if (share.eventTitle != null) {
                                                share.eventId?.let(onOpenEventCommunity)
                                            } else {
                                                share.authorId?.let(onOpenProfile)
                                            }
                                        },
                                        fontWeight = if (share.eventTitle != null) FontWeight.ExtraBold else FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    PostMetaLine(
                                        author = share.author.takeIf { share.eventTitle != null },
                                        timeLabel = share.postTimeLabel(),
                                        textStyle = MaterialTheme.typography.bodySmall,
                                        onAuthorClick = share.authorId?.let { id -> { onOpenProfile(id) } }
                                    )
                                }
                            }
                            ExpandableText(text = share.content, style = MaterialTheme.typography.bodyMedium)
                        }
                        CommunityPostMedia(post = post.copy(mediaItems = share.mediaItems, imageUrl = null, mediaUrl = null, mediaType = null), height = 220.dp, clipCorners = false)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PostAction(
                    icon = Icons.Default.FavoriteBorder,
                    label = "$likeCount",
                    tint = if (liked) Danger else SoftText,
                    onClick = onLikeClick
                )
                PostAction(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    label = "$commentCount",
                    onClick = onCommentClick
                )
                PostAction(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    label = "Chia sẻ",
                    onClick = onShareClick
                )
            }
        }
    }
}
