package com.example.fanzone.domain.models

enum class MediaType {
    IMAGE, VIDEO, VOICE_NOTE
}

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String,
    val isCeleb: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0
)

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val venue: String,
    val location: String,
    val dateTime: Long,
    val organizerId: String,
    val priceFrom: Double,
    val tags: List<String>,
    val salesVelocity: Float
)

data class TicketZone(
    val id: String,
    val eventId: String,
    val name: String,
    val price: Double,
    val totalSeats: Int,
    val availableSeats: Int,
    val features: List<String>
)

data class Post(
    val id: String,
    val authorId: String,
    val eventId: String?,
    val content: String,
    val mediaUrl: String?,
    val mediaType: MediaType,
    val timestamp: Long,
    val likesCount: Int = 0
)
