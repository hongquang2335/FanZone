package com.example.myapplication.core.designsystem.component

import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.imePadding
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myapplication.core.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
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
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.SharedCommunityPost
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus


@Composable
fun HeroBanner(event: Event, onOpenEvent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF24B66B), EvergreenDark, Color(0xFF053B27))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 210.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AssistChip(onClick = {}, label = { Text(AppStrings.Community.FLASH_SALE_TITLE) })
            Text(AppStrings.Community.FLASH_SALE_SUB, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(AppStrings.Community.FLASH_SALE_BODY, color = Color.White.copy(alpha = 0.88f))
            OutlinedButton(
                onClick = onOpenEvent,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.36f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(AppStrings.Community.VIEW_NOW, color = Color.White)
            }
        }
        if (event.imageUrl != null) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        } else {
            Image(
                painter = painterResource(event.imageRes),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        }
    }
}

@Composable
fun EventCard(event: Event, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column {
            Box {
                if (event.imageUrl != null) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(event.imageRes),
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("THÁNG 10", style = MaterialTheme.typography.labelLarge, color = Danger)
                        Text("28", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(event.title, style = MaterialTheme.typography.titleLarge)
                MetaRow(Icons.Default.Schedule, event.schedule)
                MetaRow(Icons.Default.LocationOn, "${event.venue}, ${event.city}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.Community.INTERESTED_COUNT, color = SoftText, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onOpen, shape = RoundedCornerShape(20.dp)) {
                        Text(AppStrings.Community.INTERESTED)
                    }
                }
            }
        }
    }
}

@Composable
fun GradientPanel(title: String, body: String, action: String, onAction: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF052C1D), EvergreenDark, Color(0xFF4AD38E))))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 220.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(body, color = Color.White.copy(alpha = 0.88f))
            Button(onClick = onAction, shape = RoundedCornerShape(20.dp)) {
                Text(action)
            }
        }
    }
}

@Composable
fun ExpandableText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    textLimit: Int = 50
) {
    if (text.isBlank()) return

    var isExpanded by remember { mutableStateOf(false) }

    if (text.length > textLimit) {
        if (!isExpanded) {
            Text(
                text = buildAnnotatedString {
                    append(text.take(textLimit))
                    append("... ")
                    withStyle(style = SpanStyle(color = Evergreen, fontWeight = FontWeight.Bold)) {
                        append("Xem thêm")
                    }
                },
                style = style,
                modifier = Modifier.clickable { isExpanded = true }
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    append(text)
                    append(" ")
                    withStyle(style = SpanStyle(color = Evergreen, fontWeight = FontWeight.Bold)) {
                        append("Rút gọn")
                    }
                },
                style = style,
                modifier = Modifier.clickable { isExpanded = false }
            )
        }
    } else {
        Text(text, style = style)
    }
}

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
    onEditPost: () -> Unit = {}
) {
    var liked by remember(post.id, currentUserId, post.likedBy) { mutableStateOf(post.isLikedByUser(currentUserId)) }
    var likeCount by remember(post.id, post.likes) { mutableStateOf(post.likes) }
    var commentCount by remember(post.id) { mutableStateOf(post.comments) }
    var commentOpen by remember(post.id) { mutableStateOf(false) }
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
                onCommentClick = {
                    onOpenComments()
                    commentOpen = true
                },
                onShareClick = { shareOpen = true },
                onOpenProfile = onOpenProfile,
                onEditPost = onEditPost,
                onDeletePost = { showDeleteConfirm = true }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
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


                            if (showDeleteConfirm) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { showDeleteConfirm = false },
                                    title = { Text("Xóa bài viết") },
                                    text = { Text("Bạn có chắc chắn muốn xóa bài viết này không?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            showDeleteConfirm = false
                                            onDeletePost()
                                        }) {
                                            Text("Xóa", color = Danger)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteConfirm = false }) {
                                            Text("Hủy")
                                        }
                                    }
                                )
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
        if (sharedPost != null) {
            if (showDeleteConfirm) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Xóa bài viết") },
                    text = { Text("Bạn có chắc chắn muốn xóa bài viết này không?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteConfirm = false
                            onDeletePost()
                        }) {
                            Text("Xóa", color = Danger)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }

    if (showAuthPrompt) {
        AuthPromptDialog(
            onDismiss = { showAuthPrompt = false },
            onSignIn = onOpenAuth
        )
    }

    if (commentOpen) {
        FirestoreCommentsDialog(
            post = post,
            comments = comments,
            likeCount = likeCount,
            shareCount = shareCount,
            authorName = currentAuthorName,
            onDismiss = { commentOpen = false },
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

@Composable
private fun SharedPostPreview(
    post: CommunityPost,
    share: SharedCommunityPost,
    isMyPost: Boolean,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onEditPost: () -> Unit,
    onDeletePost: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                color = Color.White,
                border = BorderStroke(1.dp, SoftLine)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 14.dp),
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
                                    modifier = Modifier.clickable(enabled = share.authorId != null) {
                                        share.authorId?.let(onOpenProfile)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                PostMetaLine(
                                    author = share.author.takeIf { share.eventTitle != null },
                                    timeLabel = post.postTimeLabel(),
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

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PostAction(
                    icon = Icons.Default.FavoriteBorder,
                    label = "0",
                    onClick = {}
                )
                PostAction(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    label = "0",
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

private fun CommunityPost.postTimeLabel(): String {
    val createdAt = createdAtMillis ?: return "Vừa xong"
    val elapsedMillis = (System.currentTimeMillis() - createdAt).coerceAtLeast(0L)
    val minuteMillis = 60_000L
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    val monthMillis = 31 * dayMillis
    val yearMillis = 365 * dayMillis

    return when {
        elapsedMillis < minuteMillis -> "Vừa xong"
        elapsedMillis < hourMillis -> "cách đây ${elapsedMillis / minuteMillis} phút"
        elapsedMillis < dayMillis -> "cách đây ${elapsedMillis / hourMillis} giờ"
        elapsedMillis < monthMillis -> "cách đây ${elapsedMillis / dayMillis} ngày"
        elapsedMillis < yearMillis -> SimpleDateFormat("d/M", Locale.getDefault()).format(Date(createdAt))
        else -> SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date(createdAt))
    }
}

@Composable
private fun CommunityPostMedia(
    post: CommunityPost,
    height: Dp,
    clipCorners: Boolean = true
) {
    val mediaItems = post.displayMediaItems()
    if (mediaItems.isEmpty()) return

    var viewerStartIndex by remember(post.id) { mutableStateOf<Int?>(null) }
    val shapeModifier = if (clipCorners) Modifier.clip(RoundedCornerShape(20.dp)) else Modifier
    val previewHeight = when {
        mediaItems.all { it.isAudio } -> 104.dp
        height < 340.dp -> 340.dp
        else -> height
    }

    if (mediaItems.size == 1) {
        CommunityMediaPreview(
            item = mediaItems.first(),
            contentDescription = post.content,
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
                .then(shapeModifier),
            onClick = { viewerStartIndex = 0 }
        )
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(mediaItems) { index, item ->
                CommunityMediaPreview(
                    item = item,
                    contentDescription = post.content,
                    modifier = Modifier
                        .width(280.dp)
                        .height(if (item.isAudio) 104.dp else 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    onClick = { viewerStartIndex = index }
                )
            }
        }
    }

    viewerStartIndex?.let { startIndex ->
        CommunityMediaViewer(
            mediaItems = mediaItems,
            startIndex = startIndex,
            contentDescription = post.content,
            onDismiss = { viewerStartIndex = null }
        )
    }
}

@Composable
private fun CommunityMediaPreview(
    item: DisplayCommunityMediaItem,
    contentDescription: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(if (item.isVideo) Color.Black else Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            item.imageRes != null -> {
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            item.isImage -> {
                AsyncImage(
                    model = item.url,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            item.isVideo -> {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Xem video",
                        tint = Color.White,
                        modifier = Modifier.size(58.dp)
                    )
                }
            }
            else -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Evergreen, modifier = Modifier.size(34.dp))
                    Text("Ghi am", color = Ink, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun CommunityMediaViewer(
    mediaItems: List<DisplayCommunityMediaItem>,
    startIndex: Int,
    contentDescription: String,
    onDismiss: () -> Unit
) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = startIndex,
        pageCount = { mediaItems.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            androidx.compose.foundation.pager.HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val item = mediaItems[page]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        item.imageRes != null -> {
                            Image(
                                painter = painterResource(item.imageRes),
                                contentDescription = contentDescription,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        item.isImage -> {
                            AsyncImage(
                                model = item.url,
                                contentDescription = contentDescription,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        item.isVideo -> {
                            CommunityVideoPlayer(url = item.url.orEmpty())
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                                Text("Tep ghi am", color = Color.White, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 14.dp)
                    .background(Color.Black.copy(alpha = 0.42f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Thoat", tint = Color.White)
            }
        }
    }
}

@Composable
private fun CommunityVideoPlayer(url: String) {
    if (url.isBlank()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("Video chưa sẵn sàng", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    LaunchedEffect(url) {
        exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

private data class DisplayCommunityMediaItem(
    val url: String? = null,
    val type: String,
    val imageRes: Int? = null
) {
    val isImage: Boolean get() = imageRes != null || type.startsWith("image/")
    val isVideo: Boolean get() = type.startsWith("video/")
    val isAudio: Boolean get() = type.startsWith("audio/")
}

private fun CommunityPost.displayMediaItems(): List<DisplayCommunityMediaItem> {
    if (imageRes != null) {
        return listOf(DisplayCommunityMediaItem(type = "image/resource", imageRes = imageRes))
    }
    val newItems = mediaItems.map { it.toDisplayItem() }
    if (newItems.isNotEmpty()) return newItems

    val legacyUrl = mediaUrl ?: imageUrl ?: return emptyList()
    val legacyType = mediaType ?: if (imageUrl != null) "image/legacy" else "application/octet-stream"
    return listOf(DisplayCommunityMediaItem(url = legacyUrl, type = legacyType))
}

private fun CommunityMediaItem.toDisplayItem(): DisplayCommunityMediaItem {
    return DisplayCommunityMediaItem(url = url, type = type)
}

@Composable
private fun PostMetaLine(
    timeLabel: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    author: String? = null,
    onAuthorClick: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!author.isNullOrBlank()) {
            Text(
                text = author,
                color = SoftText,
                style = textStyle,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (onAuthorClick != null) Modifier.clickable(onClick = onAuthorClick) else Modifier
            )
            Text("·", color = SoftText, style = textStyle)
        }
        Text(
            text = timeLabel,
            color = SoftText,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PostAction(
    icon: ImageVector,
    label: String,
    tint: Color = SoftText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Text(label, color = tint)
    }
}

@Composable
private fun FirestoreCommentsDialog(
    post: CommunityPost,
    comments: List<CommunityComment>,
    likeCount: Int,
    shareCount: Int,
    authorName: String,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }
    var dragDistance by remember { mutableStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = { dragDistance = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                dragDistance += dragAmount
                                if (dragDistance > 90f) onDismiss()
                            },
                            onDragEnd = { dragDistance = 0f },
                            onDragCancel = { dragDistance = 0f }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.width(76.dp).height(6.dp),
                    shape = RoundedCornerShape(99.dp),
                    color = SoftLine
                ) {}
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(AppStrings.Community.FAVORITE.format(likeCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(AppStrings.Community.SHARES_COUNT.format(shareCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 18.dp, vertical = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Text(AppStrings.Community.NO_COMMENTS, color = SoftText, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    items(comments) { comment ->
                        FirestoreCommentBubble(comment = comment)
                    }
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(AppStrings.Community.WRITE_COMMENT_PLACEHOLDER.format(authorName), color = SoftText, style = MaterialTheme.typography.bodyLarge) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F5F7),
                        unfocusedContainerColor = Color(0xFFF3F5F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                TextButton(
                    enabled = draft.isNotBlank(),
                    onClick = {
                        val text = draft
                        draft = ""
                        onAddComment(text)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Evergreen,
                        disabledContentColor = SoftText
                    )
                ) {
                    Text(AppStrings.Community.SEND, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun FirestoreCommentBubble(comment: CommunityComment) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CircleAvatar(size = 46.dp, imageUrl = comment.authorAvatarUrl)
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF0F3F6)) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(comment.authorName, fontWeight = FontWeight.Bold)
                if (comment.text.isNotBlank()) {
                    ExpandableText(text = comment.text, style = MaterialTheme.typography.bodyLarge, textLimit = 50)
                }
                comment.mediaItems.firstOrNull()?.let { media ->
                    Text(media.type, color = SoftText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ShareDialog(
    post: CommunityPost,
    authorName: String,
    onDismiss: () -> Unit,
    onShareNow: (String) -> Unit
) {
    var dragDistance by remember { mutableStateOf(0f) }
    var shareDraft by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.36f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFFF2F5F8))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .navigationBarsPadding()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = { dragDistance = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                dragDistance += dragAmount
                                if (dragDistance > 70f) {
                                    onDismiss()
                                }
                            },
                            onDragEnd = { dragDistance = 0f },
                            onDragCancel = { dragDistance = 0f }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(76.dp)
                        .height(6.dp),
                    shape = RoundedCornerShape(99.dp),
                    color = SoftLine
                ) {}
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircleAvatar(size = 52.dp)
                        Column {
                            Text(authorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    TextField(
                        value = shareDraft,
                        onValueChange = { shareDraft = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(AppStrings.Community.PLACEHOLDER, color = SoftText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = SoftLine,
                            unfocusedIndicatorColor = SoftLine
                        ),
                        minLines = 3,
                        maxLines = 5
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            onClick = {
                                onShareNow(shareDraft)
                                shareDraft = ""
                            },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(AppStrings.Community.SHARE_NOW)
                        }
                    }
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(AppStrings.Community.CLOSE)
            }
        }
        }
    }
}

@Composable
private fun ShareSection(
    title: String,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 18.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(items) { label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(68.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Evergreen)
                        }
                    }
                    Text(label, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(86.dp))
                }
            }
        }
    }
}

@Composable
fun AuthPromptDialog(
    onDismiss: () -> Unit,
    onSignIn: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Evergreen,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = AppStrings.Community.AUTH_REQUIRED_TITLE,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )
                Text(
                    text = AppStrings.Community.AUTH_REQUIRED_DESC,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftText
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftLine)
                    ) {
                        Text(AppStrings.Community.LATER, color = SoftText)
                    }
                    Button(
                        onClick = {
                            onDismiss()
                            onSignIn()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(AppStrings.Community.LOGIN)
                    }
                }
            }
        }
    }
}

@Composable
fun EditPostDialog(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onSave: (String, List<com.example.myapplication.domain.model.CommunityMediaItem>) -> Unit
) {
    var draft by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(post.content) }
    var currentMediaItems by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(post.mediaItems) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                androidx.compose.material3.IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng")
                }
                Text(
                    text = "Chỉnh sửa bài viết",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                )
                TextButton(
                    onClick = { onSave(draft, currentMediaItems) },
                    enabled = (draft.isNotBlank() && draft != post.content) || currentMediaItems != post.mediaItems,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Lưu",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if ((draft.isNotBlank() && draft != post.content) || currentMediaItems != post.mediaItems) Evergreen else SoftText
                    )
                }
            }

            androidx.compose.material3.Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar(
                        size = 64.dp,
                        imageUrl = post.authorAvatarUrl
                    )
                    Text(post.author, style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
                }

                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    decorationBox = { innerTextField ->
                        Box {
                            if (draft.isEmpty()) {
                                Text(AppStrings.Community.PLACEHOLDER, color = SoftText, style = MaterialTheme.typography.bodyLarge)
                            }
                            innerTextField()
                        }
                    }
                )

                if (currentMediaItems.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentMediaItems) { media ->
                            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))) {
                                if (media.type == "image") {
                                    AsyncImage(
                                        model = media.url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                    }
                                }
                                androidx.compose.material3.IconButton(
                                    onClick = { currentMediaItems = currentMediaItems - media },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
