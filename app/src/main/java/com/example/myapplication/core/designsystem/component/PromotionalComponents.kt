package com.example.myapplication.core.designsystem.component

import android.net.Uri
import android.widget.VideoView
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.SharedCommunityPost
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus
import coil.compose.AsyncImage


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
            AssistChip(onClick = {}, label = { Text("Giam chop nhoang") })
            Text("Flash Sale\nCuoi Tuan!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text("Giam gia len den 50% cho cac su kien hot nhat tuan nay.", color = Color.White.copy(alpha = 0.88f))
            OutlinedButton(
                onClick = onOpenEvent,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.36f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Xem ngay", color = Color.White)
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
                        Text("THANG 10", style = MaterialTheme.typography.labelLarge, color = Danger)
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
                    Text("+99 quan tam", color = SoftText, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onOpen, shape = RoundedCornerShape(20.dp)) {
                        Text("Quan tam")
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
fun CommunityCard(
    post: CommunityPost,
    modifier: Modifier = Modifier,
    currentAuthorName: String = "Bạn",
    currentUserId: String? = null,
    onOpenEventCommunity: (String) -> Unit = {},
    onSharePost: (CommunityPost, String) -> Unit = { _, _ -> },
    onToggleLike: () -> Unit = {},
    onOpenAuth: () -> Unit = {}
) {
    var liked by remember(post.id) { mutableStateOf(post.isLikedByUser(currentUserId)) }
    var likeCount by remember(post.id) { mutableStateOf(post.likes) }
    var commentCount by remember(post.id) { mutableStateOf(post.comments) }
    var commentOpen by remember(post.id) { mutableStateOf(false) }
    var shareOpen by remember(post.id) { mutableStateOf(false) }
    var shareCount by remember(post.id) { mutableStateOf(post.shareCount) }
    var showAuthPrompt by remember { mutableStateOf(false) }
    val sharedPost = post.sharedPost

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (sharedPost != null) {
            SharedPostPreview(
                post = post,
                share = sharedPost,
                onCommentClick = { commentOpen = true },
                onShareClick = { shareOpen = true }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircleAvatar()
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
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = post.author,
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
                    }
                    Text(post.content, style = MaterialTheme.typography.bodyLarge)
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

    if (showAuthPrompt) {
        AuthPromptDialog(
            onDismiss = { showAuthPrompt = false },
            onSignIn = onOpenAuth
        )
    }

    if (commentOpen) {
        CommentsDialog(
            post = post,
            likeCount = likeCount,
            shareCount = shareCount,
            authorName = currentAuthorName,
            onDismiss = { commentOpen = false },
            onAddComment = { commentCount++ }
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
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircleAvatar()
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = share.author,
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
            }

            if (share.caption.isNotBlank()) {
                Text(share.caption, style = MaterialTheme.typography.bodyLarge)
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
                            CircleAvatar(size = 36.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = post.eventTitle ?: post.author,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                PostMetaLine(
                                    author = post.author.takeIf { post.eventTitle != null },
                                    timeLabel = post.postTimeLabel(),
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Text(post.content, style = MaterialTheme.typography.bodyMedium)
                    }
                    CommunityPostMedia(post = post, height = 220.dp, clipCorners = false)
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
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(mediaItems) { _, item ->
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillMaxHeight()
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
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                setVideoURI(Uri.parse(url))
                setOnPreparedListener { player ->
                    player.isLooping = true
                    start()
                }
            }
        },
        update = { view ->
            if (!view.isPlaying) {
                view.setVideoURI(Uri.parse(url))
                view.start()
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
    author: String? = null
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
                overflow = TextOverflow.Ellipsis
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
private fun CommentsDialog(
    post: CommunityPost,
    likeCount: Int,
    shareCount: Int,
    authorName: String,
    onDismiss: () -> Unit,
    onAddComment: () -> Unit
) {
    var draft by remember { mutableStateOf("") }
    var dragDistance by remember { mutableStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .navigationBarsPadding()
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
                                if (dragDistance > 90f) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Yêu thích $likeCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$shareCount lượt chia sẻ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Divider()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    CommentBubble(
                        author = "Trịnh Hải Yến",
                        body = "Xuất sắc thượng hạng"
                    )
                }
                item {
                    CommentBubble(
                        author = "Hồng Quang",
                        body = "Đúng là tiết mục đỉnh nhất đêm nay"
                    )
                }
                item {
                    CommentBubble(
                        author = "Nguyễn Chinh",
                        body = "thích quá aaa"
                    )
                }
                item {
                    CommentBubble(
                        author = "Mã Anh Thu",
                        body = "tui mê 2 MC này quá aa"
                    )
                }
                item {
                    CommentBubble(
                        author = "Huy Hùng",
                        body = "Mã Anh Thu mê 2"
                    )
                }
                item {
                    CommentBubble(
                        author = "Nguyễn Vân Anh",
                        body = "xinh đẹp tuyệt vời"
                    )
                }
                item {
                    CommentBubble(
                        author = "Nghiêm Hòa",
                        body = "Top MC tôi tin tưởng đây rồi"
                    )
                }
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Bình luận dưới tên $authorName") },
                    singleLine = true
                )
                TextButton(
                    enabled = draft.isNotBlank(),
                    onClick = {
                        draft = ""
                        onAddComment()
                    }
                ) {
                    Text("Gửi")
                }
            }
        }
    }
}

@Composable
private fun CommentBubble(
    author: String,
    body: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CircleAvatar(size = 46.dp)
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF0F3F6)) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(author, fontWeight = FontWeight.Bold)
                Text(body, style = MaterialTheme.typography.bodyLarge)
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
                        placeholder = { Text("Bạn đang nghĩ gì?", color = SoftText) },
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
                            Text("Chia sẻ ngay")
                        }
                    }
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Đóng")
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
                    text = "Yêu cầu đăng nhập",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )
                Text(
                    text = "Vui lòng đăng nhập để thích, bình luận, chia sẻ hoặc viết bài của riêng bạn.",
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
                        Text("Để sau", color = SoftText)
                    }
                    Button(
                        onClick = {
                            onDismiss()
                            onSignIn()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Đăng nhập")
                    }
                }
            }
        }
    }
}
