package com.example.myapplication.feature.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.ChatMessage
import com.example.myapplication.domain.model.Participant
import com.example.myapplication.core.designsystem.component.ChatBubble
import com.example.myapplication.core.designsystem.component.ChatInputBar
import com.example.myapplication.core.designsystem.component.ChatTopBar
import com.example.myapplication.core.designsystem.component.SuggestionChips
import com.example.myapplication.core.designsystem.theme.VibeCanvas

import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.core.designsystem.theme.FanZoneTheme
import kotlin.collections.isNotEmpty

@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit,
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberScrollState()

    // Tự động cuộn xuống đáy khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Scaffold(
        topBar = { ChatTopBar(onBackClick = onBackClick) },
        containerColor = VibeCanvas,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()) // Chỉ lấy padding phía trên (TopBar)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Giảm khoảng cách giữa các tin nhắn bằng cách không bọc thêm padding ở đây
                messages.forEach { message ->
                    val isAnimated = viewModel.isMessageAnimated(message.id)
                    android.util.Log.d("ChatbotScreen", "Rendering message: id=${message.id}, isAnimated=$isAnimated")
                    ChatBubble(
                        message = message,
                        isAnimated = isAnimated,
                        onAnimationFinished = { 
                            android.util.Log.d("ChatbotScreen", "onAnimationFinished for id=${message.id}")
                            viewModel.markMessageAsAnimated(message.id) 
                        },
                        onSuggestionClick = { suggestion ->
                            viewModel.sendMessage(suggestion)
                        },
                        onEventClick = onNavigateToEvent
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val messageWithSuggestions = messages.find { it.suggestions.isNotEmpty() }
            if (messageWithSuggestions != null) {
                SuggestionChips(
                    suggestions = messageWithSuggestions.suggestions,
                    onSuggestionClick = { suggestion ->
                        viewModel.sendMessage(suggestion)
                    }
                )
            }

            ChatInputBar(
                onSendMessage = { text ->
                    viewModel.sendMessage(text)
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatbotScreenPreview() {
    FanZoneTheme {
        val mockMessages = listOf<ChatMessage>(
            ChatMessage(
                sender = Participant.Bot,
                content = "Chào bạn! Tôi là trợ lý ảo FanZone. Tôi có thể giúp gì cho bạn hôm nay?",
                suggestions = listOf("Kiểm tra vé của tôi", "Sự kiện sắp diễn ra", "Chính sách hoàn vé", "Liên hệ hỗ trợ")
            ),
            ChatMessage(
                sender = Participant.User,
                content = "Sự kiện sắp diễn ra"
            ),
            ChatMessage(
                sender = Participant.Bot,
                content = "Tuyệt vời! Đây là một số sự kiện nổi bật sắp diễn ra trong tuần này:",
                isThinking = false
            )
        )

        Scaffold(
            topBar = { ChatTopBar(onBackClick = {}) },
            bottomBar = { ChatInputBar(onSendMessage = {}) },
            containerColor = VibeCanvas,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                mockMessages.forEach { message ->
                    ChatBubble(message = message, onSuggestionClick = {})
                }
            }
        }
    }
}
