package com.example.myapplication.feature.profile

import com.example.myapplication.domain.model.Notification

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String? = null
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}
