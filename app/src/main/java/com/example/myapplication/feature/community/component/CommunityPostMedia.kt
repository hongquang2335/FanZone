package com.example.myapplication.feature.community.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.Ink
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.CommunityPost

data class DisplayCommunityMediaItem(
    val url: String? = null,
    val type: String,
    val imageRes: Int? = null
) {
    val isImage: Boolean get() = imageRes != null || type.startsWith("image/")
    val isVideo: Boolean get() = type.startsWith("video/")
    val isAudio: Boolean get() = type.startsWith("audio/")
}

fun CommunityPost.displayMediaItems(): List<DisplayCommunityMediaItem> {
    if (imageRes != null) {
        return listOf(DisplayCommunityMediaItem(type = "image/resource", imageRes = imageRes))
    }
    val newItems = mediaItems.map { it.toDisplayItem() }
    if (newItems.isNotEmpty()) return newItems

    val legacyUrl = mediaUrl ?: imageUrl ?: return emptyList()
    val legacyType = mediaType ?: if (imageUrl != null) "image/legacy" else "application/octet-stream"
    return listOf(DisplayCommunityMediaItem(url = legacyUrl, type = legacyType))
}

fun CommunityMediaItem.toDisplayItem(): DisplayCommunityMediaItem {
    return DisplayCommunityMediaItem(url = url, type = type)
}

@Composable
fun CommunityPostMedia(
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
fun CommunityMediaPreview(
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
fun CommunityMediaViewer(
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
fun CommunityVideoPlayer(url: String) {
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
