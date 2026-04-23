package com.example.myapplication.data

import com.example.myapplication.model.CommunityPost
import com.example.myapplication.model.EventStatus
import com.example.myapplication.model.FanEvent
import com.example.myapplication.model.MediaType
import com.example.myapplication.model.PurchasedTicket
import com.example.myapplication.model.ResaleState
import com.example.myapplication.model.ResaleTicket
import com.example.myapplication.model.TicketState
import com.example.myapplication.model.TicketTier
import com.example.myapplication.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface FanZoneRepository {
    fun featuredEvents(): Flow<List<FanEvent>>
    fun communityPosts(eventId: String? = null): Flow<List<CommunityPost>>
    fun purchasedTickets(): Flow<List<PurchasedTicket>>
    fun resaleTickets(): Flow<List<ResaleTicket>>
    fun currentProfile(): Flow<UserProfile>
}

object FirestoreSchema {
    const val USERS = "users"
    const val EVENTS = "events"
    const val TICKET_TIERS = "ticketTiers"
    const val POSTS = "posts"
    const val COMMENTS = "comments"
    const val ORDERS = "orders"
    const val USER_TICKETS = "userTickets"
    const val RESALE_LISTINGS = "resaleListings"
    const val NOTIFICATIONS = "notifications"
    const val FAVORITES = "favorites"

    val eventDocument = mapOf(
        "title" to "String",
        "coverImage" to "Firebase Storage URL",
        "startAt" to "Timestamp",
        "venue" to "String",
        "city" to "String",
        "priceFrom" to "Number",
        "status" to "available | lowStock | soldOut",
        "tags" to "Array<String>",
        "stats" to "{ favoriteCount, postCount, soldTicketCount }"
    )

    val resaleDocument = mapOf(
        "ticketId" to "String",
        "sellerId" to "String",
        "buyerId" to "String?",
        "price" to "Number",
        "state" to "pendingApproval | onSale | sold",
        "lockedBy" to "String?",
        "lockExpiresAt" to "Timestamp?"
    )
}

class FirebaseFanZoneRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) : FanZoneRepository {
    override fun featuredEvents(): Flow<List<FanEvent>> {
        return MockFanZoneRepository().featuredEvents()
    }

    override fun communityPosts(eventId: String?): Flow<List<CommunityPost>> {
        return MockFanZoneRepository().communityPosts(eventId)
    }

    override fun purchasedTickets(): Flow<List<PurchasedTicket>> {
        return MockFanZoneRepository().purchasedTickets()
    }

    override fun resaleTickets(): Flow<List<ResaleTicket>> {
        return MockFanZoneRepository().resaleTickets()
    }

    override fun currentProfile(): Flow<UserProfile> {
        return MockFanZoneRepository().currentProfile()
    }
}

class MockFanZoneRepository : FanZoneRepository {
    private val tiers = listOf(
        TicketTier(
            id = "standard",
            name = "Zone B (Standard)",
            price = 800_000,
            benefits = listOf("Khu vực đứng chung", "Tầm nhìn bao quát", "Check-in QR nhanh"),
            remaining = 124
        ),
        TicketTier(
            id = "vip",
            name = "Zone A (VIP)",
            price = 2_500_000,
            benefits = listOf("Gần sân khấu nhất", "Lối đi riêng", "Đồ uống miễn phí"),
            remaining = 8
        ),
        TicketTier(
            id = "early",
            name = "Zone C (Early Bird)",
            price = 500_000,
            benefits = listOf("Giá ưu đãi mở bán sớm"),
            remaining = 0
        )
    )

    private val events = listOf(
        FanEvent(
            id = "monsoon-2024",
            title = "Lễ Hội Âm Nhạc Gió Mùa 2024",
            time = "18:00 - 23:00, 28/10/2026",
            venue = "Hoàng Thành Thăng Long",
            city = "Hà Nội",
            priceFrom = 800_000,
            imageUrl = "",
            category = "Music",
            status = EventStatus.LowStock,
            description = "Đêm nhạc ngoài trời với sân khấu ánh sáng 360 độ, khu fanpit mở rộng và hoạt động cộng đồng trước giờ diễn.",
            tiers = tiers,
            tags = listOf("music", "festival", "fanpit")
        ),
        FanEvent(
            id = "art-exhibition",
            title = "Triển Lãm Nghệ Thuật Đương Đại",
            time = "09:00 - 21:00, 05/11/2026",
            venue = "Bảo tàng Mỹ thuật",
            city = "TP. Hồ Chí Minh",
            priceFrom = 500_000,
            imageUrl = "",
            category = "Art",
            status = EventStatus.Available,
            description = "Không gian trưng bày nghệ thuật đương đại, talkshow với nghệ sĩ và khu trải nghiệm tương tác.",
            tiers = tiers,
            tags = listOf("art", "exhibition")
        ),
        FanEvent(
            id = "neon-nights",
            title = "Neon Nights Festival",
            time = "19:00 - 01:00, 12/11/2026",
            venue = "SECC Hall B",
            city = "TP. Hồ Chí Minh",
            priceFrom = 800_000,
            imageUrl = "",
            category = "Festivals",
            status = EventStatus.Available,
            description = "Festival ánh sáng, DJ set và khu check-in cộng đồng theo phong cách VibePass.",
            tiers = tiers,
            tags = listOf("festival", "neon")
        ),
        FanEvent(
            id = "esports-final",
            title = "Esports Grand Final",
            time = "18:30 - 22:30, 02/12/2026",
            venue = "Mỹ Đình Indoor Arena",
            city = "Hà Nội",
            priceFrom = 420_000,
            imageUrl = "",
            category = "Gaming",
            status = EventStatus.Available,
            description = "Chung kết esports, fan meeting đội tuyển và khu trải nghiệm thiết bị gaming.",
            tiers = tiers.take(2),
            tags = listOf("esports", "gaming")
        )
    )

    private val posts = listOf(
        CommunityPost(
            id = "p1",
            authorName = "Minh Anh",
            authorAvatar = "",
            postedAt = "5 phút trước",
            body = "Ai đi Lễ Hội Âm Nhạc Gió Mùa khu VIP không? Mình đang lập nhóm check-in sớm để lấy vị trí đẹp.",
            mediaType = MediaType.Image,
            mediaUrl = null,
            eventId = "monsoon-2024",
            eventTitle = "Lễ Hội Âm Nhạc Gió Mùa 2024",
            likeCount = 248,
            commentCount = 36,
            shareCount = 12
        ),
        CommunityPost(
            id = "p2",
            authorName = "Khoa Trần",
            authorAvatar = "",
            postedAt = "18 phút trước",
            body = "Có ai cần review quyền lợi Zone A không? Lối đi riêng và đồ uống miễn phí khá đáng tiền.",
            mediaType = MediaType.Video,
            mediaUrl = null,
            eventId = "neon-nights",
            eventTitle = "Neon Nights Festival",
            likeCount = 94,
            commentCount = 11,
            shareCount = 4
        ),
        CommunityPost(
            id = "p3",
            authorName = "FanZone Team",
            authorAvatar = "",
            postedAt = "1 giờ trước",
            body = "Resale Grand Final mở duyệt vé từ hôm nay. Vé được chuyển quyền sở hữu theo cơ chế ai thanh toán trước nhận trước.",
            mediaType = null,
            mediaUrl = null,
            eventId = "esports-final",
            eventTitle = "Esports Grand Final",
            likeCount = 512,
            commentCount = 88,
            shareCount = 30
        )
    )

    override fun featuredEvents(): Flow<List<FanEvent>> = flowOf(events)

    override fun communityPosts(eventId: String?): Flow<List<CommunityPost>> {
        return flowOf(if (eventId == null) posts else posts.filter { it.eventId == eventId })
    }

    override fun purchasedTickets(): Flow<List<PurchasedTicket>> {
        return flowOf(
            listOf(
                PurchasedTicket("t1", "monsoon-2024", "Lễ Hội Âm Nhạc Gió Mùa 2024", "Zone A (VIP)", "FZ-MON-7291", "18:00 - 23:00, 28/10/2026", "Hoàng Thành Thăng Long", TicketState.Upcoming),
                PurchasedTicket("t2", "neon-nights", "Neon Nights Festival", "Zone B (Standard)", "FZ-NEO-1138", "19:00 - 01:00, 12/11/2026", "SECC Hall B", TicketState.Upcoming),
                PurchasedTicket("t3", "art-exhibition", "Triển Lãm Nghệ Thuật Đương Đại", "Zone C (Early Bird)", "FZ-ART-5502", "09:00 - 21:00, 05/11/2025", "Bảo tàng Mỹ thuật", TicketState.Used)
            )
        )
    }

    override fun resaleTickets(): Flow<List<ResaleTicket>> {
        return flowOf(
            listOf(
                ResaleTicket("r1", "t4", "Esports Grand Final", "VIP", 2_450_000, ResaleState.OnSale, "Còn 2 vé cùng hạng"),
                ResaleTicket("r2", "t5", "Lễ Hội Âm Nhạc Gió Mùa 2024", "Zone A (VIP)", 2_300_000, ResaleState.PendingApproval, "Chờ duyệt từ FanZone"),
                ResaleTicket("r3", "t6", "Neon Nights Festival", "Zone B (Standard)", 720_000, ResaleState.Sold, "Vừa có người mua")
            )
        )
    }

    override fun currentProfile(): Flow<UserProfile> {
        return flowOf(
            UserProfile(
                id = "u1",
                name = "Quang Nguyen",
                bio = "Music lover, esports fan, luôn sẵn sàng cho sự kiện tiếp theo.",
                avatarUrl = "",
                coverUrl = "",
                followers = 1280,
                following = 312,
                friends = 86,
                joinedEvents = 17,
                postCount = 42
            )
        )
    }
}
