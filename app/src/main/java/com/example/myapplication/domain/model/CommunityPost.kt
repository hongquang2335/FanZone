package com.example.myapplication.domain.model

import androidx.annotation.DrawableRes

data class CommunityPost(
    val id: String,
    val authorId: String? = null,
    val author: String,
    val role: String,
    val topic: String,
    val content: String,
    val likes: Int,
    val likedBy: List<String> = emptyList(),
    val comments: Int,
    val shareCount: Int = 0,
    @param:DrawableRes val imageRes: Int?,
    val imageUrl: String? = null,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val mediaItems: List<CommunityMediaItem> = emptyList(),
    val eventId: String? = null,
    val eventTitle: String? = null,
    val sharedPost: SharedCommunityPost? = null,
    val createdAtMillis: Long? = null,
    val updatedAtMillis: Long? = null
) {
    fun isLikedByUser(userId: String?): Boolean {
        if (userId == null) return false
        return likedBy.contains(userId)
    }
}

data class CommunityMediaItem(
    val url: String,
    val type: String
)

data class SharedCommunityPost(
    val author: String,
    val caption: String
)
