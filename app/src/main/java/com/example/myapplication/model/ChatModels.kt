package com.example.myapplication.model

import androidx.compose.runtime.Immutable

enum class Participant {
    User, Bot
}

@Immutable
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: Participant,
    val content: String,
    val suggestions: List<String> = emptyList(),
    val isThinking: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
