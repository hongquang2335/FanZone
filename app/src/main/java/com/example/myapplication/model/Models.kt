package com.example.myapplication.model

enum class EventStatus {
    Available,
    LowStock,
    SoldOut
}

enum class TicketState {
    Upcoming,
    Used,
    Cancelled
}

enum class ResaleState {
    PendingApproval,
    OnSale,
    Sold
}

enum class MediaType {
    Image,
    Video,
    Audio
}

data class TicketTier(
    val id: String,
    val name: String,
    val price: Long,
    val benefits: List<String>,
    val remaining: Int
)

data class FanEvent(
    val id: String,
    val title: String,
    val time: String,
    val venue: String,
    val city: String,
    val priceFrom: Long,
    val imageUrl: String,
    val category: String,
    val status: EventStatus,
    val description: String,
    val tiers: List<TicketTier>,
    val tags: List<String> = emptyList()
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorAvatar: String,
    val postedAt: String,
    val body: String,
    val mediaType: MediaType?,
    val mediaUrl: String?,
    val eventId: String?,
    val eventTitle: String?,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val isSaved: Boolean = false
)

data class PurchasedTicket(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val tierName: String,
    val qrCode: String,
    val time: String,
    val venue: String,
    val state: TicketState
)

data class ResaleTicket(
    val id: String,
    val sourceTicketId: String,
    val eventTitle: String,
    val tierName: String,
    val price: Long,
    val state: ResaleState,
    val warning: String?
)

data class UserProfile(
    val id: String,
    val name: String,
    val bio: String,
    val avatarUrl: String,
    val coverUrl: String,
    val followers: Int,
    val following: Int,
    val friends: Int,
    val joinedEvents: Int,
    val postCount: Int
)

data class BookingDraft(
    val event: FanEvent,
    val tier: TicketTier,
    val quantity: Int
) {
    val totalPrice: Long = tier.price * quantity
}

fun Long.toVnd(): String {
    return "%,d VND".format(this).replace(',', '.')
}
