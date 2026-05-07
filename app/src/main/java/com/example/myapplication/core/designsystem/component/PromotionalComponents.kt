package com.example.myapplication.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
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

@Composable
fun EventCard(event: Event, modifier: Modifier = Modifier, onOpen: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(event.imageRes),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
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
    onOpenEventCommunity: (String) -> Unit = {}
) {
    var liked by remember(post.id) { mutableStateOf(false) }
    var likeCount by remember(post.id) { mutableStateOf(post.likes) }
    var commentCount by remember(post.id) { mutableStateOf(post.comments) }
    var commentOpen by remember(post.id) { mutableStateOf(false) }
    var shareOpen by remember(post.id) { mutableStateOf(false) }
    var shareCount by remember(post.id) { mutableStateOf(4) }

    Card(
        modifier = modifier,
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
                        Text(
                            text = "${post.author} - ${post.role}",
                            color = SoftText,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = post.author,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = post.role,
                            color = SoftText,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (post.eventTitle == null) {
                Text(post.topic, color = SoftText, style = MaterialTheme.typography.bodyMedium)
            }
            Text(post.content, style = MaterialTheme.typography.bodyLarge)
            if (post.imageRes != null) {
                Image(
                    painter = painterResource(post.imageRes),
                    contentDescription = post.topic,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PostAction(
                    icon = Icons.Default.FavoriteBorder,
                    label = "$likeCount",
                    tint = if (liked) Danger else SoftText,
                    onClick = {
                        liked = !liked
                        likeCount += if (liked) 1 else -1
                    }
                )
                PostAction(
                    icon = Icons.AutoMirrored.Outlined.Chat,
                    label = "$commentCount",
                    onClick = { commentOpen = true }
                )
                PostAction(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    label = "Chia se",
                    onClick = { shareOpen = true }
                )
            }
        }
    }

    if (commentOpen) {
        CommentsDialog(
            post = post,
            likeCount = likeCount,
            shareCount = shareCount,
            onDismiss = { commentOpen = false },
            onAddComment = { commentCount++ }
        )
    }

    if (shareOpen) {
        ShareDialog(
            post = post,
            onDismiss = { shareOpen = false },
            onShareNow = {
                shareCount++
                shareOpen = false
            }
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
    onDismiss: () -> Unit,
    onAddComment: () -> Unit
) {
    var draft by remember { mutableStateOf("") }
    var replyTarget by remember { mutableStateOf<String?>(null) }
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
                Text("Yeu thich $likeCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$shareCount luot chia se", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                    CommentThread(
                        author = "Trinh Hai Yen",
                        body = "Xuat sac thuong hang",
                        time = "1 ngay",
                        replies = listOf("Hong Quang" to "Dung la tiet muc dinh nhat dem nay"),
                        onReply = { replyTarget = it }
                    )
                }
                item {
                    CommentThread(
                        author = "Nguyen Chinh",
                        body = "thich qua aaa",
                        time = "1 ngay",
                        replies = emptyList(),
                        onReply = { replyTarget = it }
                    )
                }
                item {
                    CommentThread(
                        author = "Ma Anh Thu",
                        body = "tui me 2 mc may qua aa",
                        time = "2 ngay",
                        replies = listOf("Huy Hung" to "Ma Anh Thu me 2"),
                        onReply = { replyTarget = it }
                    )
                }
                item {
                    CommentThread(
                        author = "Nguyen Van Anh",
                        body = "xinh dep tuyet voi",
                        time = "2 ngay",
                        replies = emptyList(),
                        onReply = { replyTarget = it }
                    )
                }
                item {
                    CommentThread(
                        author = "Nghiem Hoa",
                        body = "Top MC toi tin tuong day rui",
                        time = "2 ngay",
                        replies = emptyList(),
                        onReply = { replyTarget = it }
                    )
                }
            }
            Divider()
            replyTarget?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dang tra loi $it", color = SoftText, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { replyTarget = null }) { Text("Huy") }
                }
            }
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
                    placeholder = { Text(replyTarget?.let { "Tra loi $it" } ?: "Binh luan duoi ten Hong Quang") },
                    singleLine = true
                )
                TextButton(
                    enabled = draft.isNotBlank(),
                    onClick = {
                        draft = ""
                        replyTarget = null
                        onAddComment()
                    }
                ) {
                    Text("Gui")
                }
            }
        }
    }
}

@Composable
private fun CommentThread(
    author: String,
    body: String,
    time: String,
    replies: List<Pair<String, String>>,
    onReply: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CommentBubble(author = author, body = body, time = time, onReply = onReply)
        replies.forEach { (replyAuthor, replyBody) ->
            Row(modifier = Modifier.padding(start = 58.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircleAvatar(size = 32.dp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF0F3F6)) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(replyAuthor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(replyBody, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        Text("Vua xong", color = SoftText, style = MaterialTheme.typography.bodyMedium)
                        Text("Thich", color = SoftText, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Tra loi",
                            color = SoftText,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onReply(replyAuthor) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentBubble(
    author: String,
    body: String,
    time: String,
    onReply: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CircleAvatar(size = 46.dp)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF0F3F6)) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(author, fontWeight = FontWeight.Bold)
                    Text(body, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(time, color = SoftText)
                Text("Thich", color = SoftText, fontWeight = FontWeight.SemiBold)
                Text(
                    "Tra loi",
                    color = SoftText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onReply(author) }
                )
            }
        }
    }
}

@Composable
private fun ShareDialog(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onShareNow: () -> Unit
) {
    var dragDistance by remember { mutableStateOf(0f) }

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
                .clickable(enabled = false) {}
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
                            Text("Hong Quang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            AssistChip(onClick = {}, label = { Text("Bang feed") })
                        }
                    }
                    Text("Ban noi gi di...", color = SoftText, style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = SoftText)
                            Icon(Icons.AutoMirrored.Outlined.Chat, contentDescription = null, tint = SoftText)
                        }
                        Button(onClick = onShareNow, shape = RoundedCornerShape(14.dp)) {
                            Text("Chia se ngay")
                        }
                    }
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Dong")
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

