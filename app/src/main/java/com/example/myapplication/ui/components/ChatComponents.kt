package com.example.myapplication.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.model.Participant
import com.example.myapplication.ui.theme.*
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
    onSuggestionClick: (String) -> Unit
) {
    val isBot = message.sender == Participant.Bot
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
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
                        text = message.content,
                        isBot = isBot,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (!isBot) {
                Spacer(modifier = Modifier.width(8.dp))
                AvatarBubble(isBot)
            }
        }
        
        if (isBot && message.suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            SuggestionChips(
                suggestions = message.suggestions,
                onSuggestionClick = onSuggestionClick,
                modifier = Modifier.padding(start = 44.dp)
            )
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
    text: String,
    isBot: Boolean,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current
    var displayedTextCount by remember(text) { mutableIntStateOf(if (isPreview || !isBot) text.length else 0) }
    
    if (!isPreview && isBot && displayedTextCount < text.length) {
        LaunchedEffect(text) {
            val words = text.split(" ")
            var currentLength = 0
            for (word in words) {
                currentLength += if (currentLength == 0) word.length else word.length + 1
                displayedTextCount = currentLength
                delay(50)
            }
            displayedTextCount = text.length
        }
    }
    
    val fullText = text.take(displayedTextCount)
    
    val annotatedString = buildAnnotatedString {
        if (fullText.startsWith("Tuyệt vời!")) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = VibeGreenDark)) {
                append("Tuyệt vời!")
            }
            append(fullText.substring(10))
        } else {
            append(fullText)
        }
    }
    
    Text(
        text = annotatedString,
        color = if (isBot) VibeText else Color.White,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // We use a Column of Rows to wrap chips if they exceed width, or LazyRow for simplicity
    // User requested "bố cục như hình" which shows wrapping chips.
    // In Compose, FlowRow is better but for now let's use a simple Column of Rows or a wrapping logic.
    // Actually, I'll use a LazyRow first and if I have time I'll implement a custom FlowLayout or just hardcode some rows for the preview.
    
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Simple wrap logic for 2 rows
        val chunked = suggestions.chunked(2)
        chunked.forEach { rowSuggestions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowSuggestions.forEach { suggestion ->
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
    }
}

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit
) {
    var textState by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* No attachments requested */ }) {
                Icon(Icons.Default.AddCircle, contentDescription = "More", tint = VibeTextMuted)
            }
            
            TextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("Nhập tin nhắn...", color = VibeTextSoft) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = VibeSurfaceMuted,
                    unfocusedContainerColor = VibeSurfaceMuted,
                    disabledContainerColor = VibeSurfaceMuted,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        onSendMessage(textState)
                        textState = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(VibeGreen)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}
