package com.example.myapplication.data.repository

import com.example.myapplication.data.firebase.NotificationFirestoreDataSource
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.repository.CommunityPostSubscription
import com.example.myapplication.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val firestoreDataSource: NotificationFirestoreDataSource
) : NotificationRepository {

    override fun observeNotifications(
        userId: String,
        onNotifications: (List<Notification>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription {
        val registration = firestoreDataSource.observeNotifications(
            userId = userId,
            onNotifications = onNotifications,
            onError = onError
        )
        return CommunityPostSubscription { registration.remove() }
    }

    override fun createNotification(
        notification: Notification,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.createNotification(notification)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun markAsRead(
        notificationId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.markAsRead(notificationId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun markAllAsRead(
        userId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.markAllAsRead(userId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }
}
