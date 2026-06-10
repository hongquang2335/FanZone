package com.example.myapplication.data.firebase

import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.NotificationType
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

class NotificationFirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val notificationsCollection = firestore.collection(NOTIFICATIONS_COLLECTION)

    fun observeNotifications(
        userId: String,
        onNotifications: (List<Notification>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        // Fetch once immediately to populate UI in case snapshot listener is blocked/delayed
        notificationsCollection
            .whereEqualTo(FIELD_RECIPIENT_ID, userId)
            .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull(::toNotification)
                    onNotifications(list)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("NotificationDataSource", "observeNotifications: GET ban đầu thất bại: ${e.message}", e)
            }

        return notificationsCollection
            .whereEqualTo(FIELD_RECIPIENT_ID, userId)
            .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull(::toNotification).orEmpty()
                onNotifications(list)
            }
    }

    fun createNotification(notification: Notification): Task<com.google.firebase.firestore.DocumentReference> {
        val data = mapOf(
            FIELD_RECIPIENT_ID to notification.recipientId,
            FIELD_SENDER_ID to notification.senderId,
            FIELD_SENDER_NAME to notification.senderName,
            FIELD_SENDER_AVATAR_URL to notification.senderAvatarUrl,
            FIELD_TYPE to notification.type.name,
            FIELD_POST_ID to notification.postId,
            FIELD_POST_CONTENT_EXCERPT to notification.postContentExcerpt,
            FIELD_TIMESTAMP to FieldValue.serverTimestamp(),
            FIELD_IS_READ to notification.isRead
        )
        return notificationsCollection.add(data)
    }

    fun markAsRead(notificationId: String): Task<Void> {
        return notificationsCollection.document(notificationId).update(FIELD_IS_READ, true)
    }

    fun markAllAsRead(userId: String): Task<Void> {
        return notificationsCollection
            .whereEqualTo(FIELD_RECIPIENT_ID, userId)
            .whereEqualTo(FIELD_IS_READ, false)
            .get()
            .continueWithTask { task ->
                val snapshot = task.result
                val batch = firestore.batch()
                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, FIELD_IS_READ, true)
                }
                batch.commit()
            }
    }

    private fun toNotification(document: DocumentSnapshot): Notification? {
        val recipientId = document.getString(FIELD_RECIPIENT_ID) ?: return null
        val senderId = document.getString(FIELD_SENDER_ID) ?: return null
        val senderName = document.getString(FIELD_SENDER_NAME) ?: return null
        val typeStr = document.getString(FIELD_TYPE) ?: return null
        val type = runCatching { NotificationType.valueOf(typeStr) }.getOrDefault(NotificationType.LIKE)
        val timestamp = document.getTimestamp(FIELD_TIMESTAMP)?.toDate()?.time ?: System.currentTimeMillis()

        return Notification(
            id = document.id,
            recipientId = recipientId,
            senderId = senderId,
            senderName = senderName,
            senderAvatarUrl = document.getString(FIELD_SENDER_AVATAR_URL),
            type = type,
            postId = document.getString(FIELD_POST_ID),
            postContentExcerpt = document.getString(FIELD_POST_CONTENT_EXCERPT),
            timestampMillis = timestamp,
            isRead = document.getBoolean(FIELD_IS_READ) ?: false
        )
    }

    companion object {
        const val NOTIFICATIONS_COLLECTION = "notifications"
        const val FIELD_RECIPIENT_ID = "recipientId"
        const val FIELD_SENDER_ID = "senderId"
        const val FIELD_SENDER_NAME = "senderName"
        const val FIELD_SENDER_AVATAR_URL = "senderAvatarUrl"
        const val FIELD_TYPE = "type"
        const val FIELD_POST_ID = "postId"
        const val FIELD_POST_CONTENT_EXCERPT = "postContentExcerpt"
        const val FIELD_TIMESTAMP = "timestamp"
        const val FIELD_IS_READ = "isRead"
    }
}
