package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Notification

interface NotificationRepository {
    fun observeNotifications(
        userId: String,
        onNotifications: (List<Notification>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription

    fun createNotification(
        notification: Notification,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    )

    fun createNotifications(
        notifications: List<Notification>,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    )

    fun markAsRead(
        notificationId: String,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    )

    fun markAllAsRead(
        userId: String,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    )
}
