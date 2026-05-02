package com.example.myapplication.ui.screens.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.model.Participant
import com.example.myapplication.ui.components.ChatBubble
import com.example.myapplication.ui.components.ChatInputBar
import com.example.myapplication.ui.components.ChatTopBar
import com.example.myapplication.viewmodel.ChatViewModel
import com.example.myapplication.ui.theme.VibeCanvas
import kotlinx.coroutines.launch

import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = { ChatTopBar(onBackClick = onBackClick) },
        bottomBar = {
            ChatInputBar(onSendMessage = { text ->
                viewModel.sendMessage(text)
            })
        },
        containerColor = VibeCanvas
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        onSuggestionClick = { suggestion ->
                            viewModel.sendMessage(suggestion)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatbotScreenPreview() {
    MyApplicationTheme {
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
            containerColor = VibeCanvas
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(mockMessages) { message ->
                    ChatBubble(message = message, onSuggestionClick = {})
                }
            }
        }
    }
}
