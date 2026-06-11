package com.example.myapplication.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.NotificationType

object NotificationHelper {
    private const val CHANNEL_ID = "fanzone_notifications"
    private const val CHANNEL_NAME = "FanZone Notifications"
    private const val CHANNEL_DESC = "Notifications for likes, comments, and follows"

    private val displayedNotificationIds = mutableSetOf<String>()
    private var isInitialized = false

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Pre-populates the displayed set with existing notification IDs on initial load
     * so that the user isn't spammed with old notifications.
     */
    fun registerExistingNotifications(notifications: List<Notification>) {
        notifications.forEach {
            displayedNotificationIds.add(it.id)
        }
    }

    /**
     * Resets the initialization state and cleared cached IDs, e.g., on sign out.
     */
    fun reset() {
        displayedNotificationIds.clear()
        isInitialized = false
    }

    /**
     * Handles live updates of the notification list, filtering and pushing alerts for new entries.
     */
    fun handleNotifications(context: Context, notifications: List<Notification>) {
        if (!isInitialized) {
            registerExistingNotifications(notifications)
            isInitialized = true
            return
        }

        notifications.forEach { notification ->
            if (!notification.isRead && !displayedNotificationIds.contains(notification.id)) {
                showNotification(context, notification)
            }
        }
    }

    fun showNotification(context: Context, notification: Notification) {
        displayedNotificationIds.add(notification.id)

        // Build pending intent to open MainActivity when clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (notification.type) {
            NotificationType.LIKE -> "Lượt thích mới"
            NotificationType.COMMENT -> "Bình luận mới"
            NotificationType.FOLLOW -> "Người theo dõi mới"
            NotificationType.NEW_POST -> "Bài viết mới"
        }

        val text = when (notification.type) {
            NotificationType.LIKE -> "${notification.senderName} đã thích bài viết của bạn."
            NotificationType.COMMENT -> "${notification.senderName} đã bình luận: \"${notification.postContentExcerpt}\""
            NotificationType.FOLLOW -> "${notification.senderName} đã bắt đầu theo dõi bạn."
            NotificationType.NEW_POST -> "${notification.senderName} đã đăng một bài viết mới."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.example.myapplication.R.mipmap.ic_launcher) // local launcher icon
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification.id.hashCode(), builder.build())
    }
}
