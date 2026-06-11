package com.example.myapplication.feature.community.component

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.SharedCommunityPost
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimeLabel(createdAtMillis: Long?): String {
    val createdAt = createdAtMillis ?: return "Vừa xong"
    val elapsedMillis = (System.currentTimeMillis() - createdAt).coerceAtLeast(0L)
    val minuteMillis = 60_000L
    val hourMillis = 60 * minuteMillis
    val dayMillis = 24 * hourMillis
    val monthMillis = 31 * dayMillis
    val yearMillis = 365 * dayMillis

    return when {
        elapsedMillis < minuteMillis -> "Vừa xong"
        elapsedMillis < hourMillis -> "cách đây ${elapsedMillis / minuteMillis} phút"
        elapsedMillis < dayMillis -> "cách đây ${elapsedMillis / hourMillis} giờ"
        elapsedMillis < monthMillis -> "cách đây ${elapsedMillis / dayMillis} ngày"
        elapsedMillis < yearMillis -> SimpleDateFormat("d/M", Locale.getDefault()).format(Date(createdAt))
        else -> SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date(createdAt))
    }
}

fun getEffectiveTimeLabel(createdAtMillis: Long?, updatedAtMillis: Long?): String {
    val created = createdAtMillis ?: 0L
    val updated = updatedAtMillis ?: 0L
    val isEdited = updated - created > 1000L
    val timeLabel = formatTimeLabel(if (isEdited) updated else created)
    return if (isEdited) "$timeLabel (Đã chỉnh sửa)" else timeLabel
}

fun CommunityPost.postTimeLabel(): String = getEffectiveTimeLabel(createdAtMillis, updatedAtMillis)

fun SharedCommunityPost.postTimeLabel(): String = getEffectiveTimeLabel(createdAtMillis, updatedAtMillis)

