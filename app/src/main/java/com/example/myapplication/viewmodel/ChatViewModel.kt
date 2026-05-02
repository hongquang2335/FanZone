package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.model.Participant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        // Initial bot greeting
        addBotGreeting()
    }

    private fun addBotGreeting() {
        _messages.value = listOf(
            ChatMessage(
                sender = Participant.Bot,
                content = "Chào bạn! Tôi là trợ lý ảo FanZone. Tôi có thể giúp gì cho bạn hôm nay?",
                suggestions = listOf("Kiểm tra vé của tôi", "Sự kiện sắp diễn ra", "Chính sách hoàn vé", "Liên hệ hỗ trợ")
            )
        )
    }

    fun sendMessage(text: String) {
        val userMessage = ChatMessage(sender = Participant.User, content = text)
        _messages.value = _messages.value + userMessage
        
        simulateBotResponse(text)
    }

    private fun simulateBotResponse(userText: String) {
        viewModelScope.launch {
            // Add "thinking" message
            val thinkingMessage = ChatMessage(sender = Participant.Bot, content = "", isThinking = true)
            _messages.value = _messages.value + thinkingMessage
            
            delay(1500) // Thinking time
            
            // Remove thinking and add actual response
            val responseText = when {
                userText.contains("vé", ignoreCase = true) -> "Tuyệt vời! Đây là một số sự kiện nổi bật sắp diễn ra trong tuần này:"
                userText.contains("sự kiện", ignoreCase = true) -> "Hiện tại có rất nhiều sự kiện hấp dẫn! Bạn quan tâm đến thể loại nào?"
                else -> "Tôi đã nhận được thông tin: '$userText'. Tôi đang xử lý yêu cầu của bạn."
            }
            
            _messages.value = _messages.value.filter { !it.isThinking } + ChatMessage(
                sender = Participant.Bot,
                content = responseText
            )
        }
    }
}
