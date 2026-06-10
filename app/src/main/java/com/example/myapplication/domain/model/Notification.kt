package com.example.myapplication.domain.model

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, NEW_POST
}

data class Notification(
    val id: String = "",
    val recipientId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String? = null,
    val type: NotificationType = NotificationType.LIKE,
    val postId: String? = null,
    val postContentExcerpt: String? = null,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
