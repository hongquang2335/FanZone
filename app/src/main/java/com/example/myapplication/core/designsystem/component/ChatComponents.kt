package com.example.myapplication.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.domain.model.AlgoliaEvent
import com.example.myapplication.domain.model.ChatMessage
import com.example.myapplication.domain.model.Participant
import com.example.myapplication.core.designsystem.theme.*
import kotlinx.coroutines.delay


@Composable
fun ChatTopBar(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VibeGreenDark)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VibeMintSoft),
                contentAlignment = Alignment.Center
            ) {
                // Assuming you have a bot icon resource, or use a placeholder
                Text("🤖", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "FanZone Bot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VibeGreenDark
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(VibeGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = VibeTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isAnimated: Boolean = true,
    onAnimationFinished: () -> Unit = {},
    onSuggestionClick: (String) -> Unit,
    onEventClick: (String) -> Unit = {}
) {
    val isBot = message.sender == Participant.Bot

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalAlignment = if (isBot) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isBot) {
                AvatarBubble(isBot)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isBot) 4.dp else 16.dp,
                    bottomEnd = if (isBot) 16.dp else 4.dp
                ),
                color = if (isBot) Color.White else VibeGreen,
                shadowElevation = if (isBot) 2.dp else 0.dp
            ) {
                if (message.isThinking) {
                    ThinkingIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    TypingText(
                        messageId = message.id,
                        text = message.content,
                        isBot = isBot,
                        isAlreadyAnimated = isAnimated,
                        onAnimationFinished = onAnimationFinished,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (!isBot) {
                Spacer(modifier = Modifier.width(8.dp))
                AvatarBubble(isBot)
            }
        }

        // Hiển thị danh sách sự kiện nếu có
        if (isBot && message.events.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            ChatEventList(
                events = message.events,
                onEventClick = onEventClick
            )
        }
    }
}

@Composable
fun ChatEventList(
    events: List<AlgoliaEvent>,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 44.dp), // Thụt vào để thẳng hàng với bubble (avatar 36dp + spacer 8dp)
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventChatCard(
                event = event,
                onClick = { onEventClick(event.objectID) }
            )
        }
    }
}

@Composable
fun EventChatCard(
    event: AlgoliaEvent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(190.dp) // Kích thước nhỏ hơn để hiện được nhiều hơn cùng lúc
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Ảnh sự kiện (sử dụng placeholder nếu không có URL)
            AsyncImage(
                model = "https://picsum.photos/seed/${event.objectID}/400/200", // Placeholder tạm thời
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = VibeGreenDark
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = VibeGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.startTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = VibeTextMuted,
                        maxLines = 1
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = VibeGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.venue,
                        style = MaterialTheme.typography.labelSmall,
                        color = VibeTextMuted,
                        maxLines = 1
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibeGreen),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Xem chi tiết", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AvatarBubble(isBot: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isBot) VibeMintSoft else VibeStrokeStrong),
        contentAlignment = Alignment.Center
    ) {
        if (isBot) {
            Text("🤖", fontSize = 16.sp)
        } else {
            Text("👤", fontSize = 16.sp)
        }
    }
}

@Composable
fun ThinkingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val dotAlphas = listOf(0, 1, 2).map { index ->
            infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }

        dotAlphas.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(VibeGreen.copy(alpha = alpha.value))
            )
        }
    }
}

@Composable
fun TypingText(
    messageId: String,
    text: String,
    isBot: Boolean,
    isAlreadyAnimated: Boolean = false,
    onAnimationFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    var displayedTextCount by remember(text) {
        val initialCount = if (isPreview || !isBot || isAlreadyAnimated) text.length else 0
        android.util.Log.d("ChatComponents", "Initializing displayedTextCount: id=$messageId, count=$initialCount, isAlreadyAnimated=$isAlreadyAnimated")
        mutableIntStateOf(initialCount)
    }

    // Tách biệt logic điều kiện khởi chạy và logic thực thi animation
    val shouldAnimate = !isPreview && isBot && !isAlreadyAnimated
    
    LaunchedEffect(messageId, shouldAnimate) {
        if (shouldAnimate && displayedTextCount < text.length) {
            android.util.Log.d("ChatComponents", "Starting animation for messageId: $messageId")
            val words = text.split(" ")
            var currentLength = 0
            for (word in words) {
                currentLength += if (currentLength == 0) word.length else word.length + 1
                displayedTextCount = currentLength
                delay(30)
            }
            displayedTextCount = text.length
            android.util.Log.d("ChatComponents", "Animation finished for messageId: $messageId")
            onAnimationFinished()
        }
    }

    val fullText = text.take(displayedTextCount)
    val annotatedString = parseMarkdown(fullText)

    Text(
        text = annotatedString,
        color = if (isBot) VibeText else Color.White,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
    )
}

/**
 * Hàm parse Markdown nâng cao
 * Xử lý: Bullet points phức tạp và Bold text lồng nhau
 */
private fun parseMarkdown(text: String): AnnotatedString {
    // Bước 1: Xử lý bullet points lồng nhau hoặc thô
    val lines = text.lines().map { line ->
        var processedLine = line
        // Regex khớp dấu * ở đầu dòng, có thể có khoảng trắng phía trước
        val bulletRegex = Regex("""^\s*\*\s+""")
        if (bulletRegex.containsMatchIn(processedLine)) {
            processedLine = processedLine.replaceFirst(bulletRegex, "  • ")
        }
        processedLine
    }
    val processedText = lines.joinToString("\n")

    // Bước 2: Parse Bold text dùng Regex
    return buildAnnotatedString {
        // Regex mạnh mẽ hơn để tránh lỗi khi có dấu * đơn lẻ hoặc lỗi format
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        var lastIdx = 0

        boldRegex.findAll(processedText).forEach { match ->
            // Thêm đoạn text thường trước match
            append(processedText.substring(lastIdx, match.range.first))

            // Thêm đoạn bold
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = VibeGreenDark)) {
                append(match.groupValues[1])
            }
            lastIdx = match.range.last + 1
        }

        // Thêm đoạn còn lại
        if (lastIdx < processedText.length) {
            append(processedText.substring(lastIdx))
        }
    }
}

@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { suggestion ->
            Surface(
                onClick = { onSuggestionClick(suggestion) },
                shape = RoundedCornerShape(20.dp),
                color = VibeSurfaceMuted,
                border = androidx.compose.foundation.BorderStroke(1.dp, VibeStroke)
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = VibeText
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = VibeStroke, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* No attachments requested */ }) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "More",
                        tint = VibeTextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }

                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    placeholder = {
                        Text(
                            "Nhập tin nhắn...",
                            color = VibeTextSoft,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VibeSurfaceMuted,
                        unfocusedContainerColor = VibeSurfaceMuted,
                        disabledContainerColor = VibeSurfaceMuted,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        autoCorrectEnabled = true
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textState.text.isNotBlank()) {
                                onSendMessage(textState.text)
                                textState = TextFieldValue("")
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textState.text.isNotBlank()) {
                            onSendMessage(textState.text)
                            textState = TextFieldValue("")
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VibeGreen)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}