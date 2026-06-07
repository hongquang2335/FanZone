package com.example.myapplication.data.repository

import com.example.myapplication.R
import com.example.myapplication.domain.model.Artist
import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.SharedCommunityPost
import com.example.myapplication.domain.model.SupportShortcut
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.TierStatus
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.domain.repository.FanZoneRepository

object FakeFanZoneRepository : FanZoneRepository {
    override val user = UserProfile(
        name = "Nguyen Minh Tuan",
        membership = "Thanh vien Neon Rewards",
        city = "TP. Ho Chi Minh"
    )

    override val categories = listOf(
        Category("art", "Art", "\uD83C\uDFA8"),
        Category("cinema", "Cinema", "\uD83C\uDFAC"),
        Category("festival", "Festivals", "\uD83C\uDFAA"),
        Category("music", "Music", "\uD83C\uDFB5"),
        Category("gaming", "Gaming", "\uD83C\uDFAE")
    )

    override val events = listOf(
        Event(
            id = "neon-night",
            title = "Neon Nights Festival 2024",
            subtitle = "Le hoi am nhac mua he",
            schedule = "20:00, Thu Bay 15/06/2024",
            venue = "San van dong My Dinh",
            city = "Ha Noi",
            description = "Dem nhac hoi mua he quy tu nhung ten tuoi hang dau cung san khau quy mo lon, khu trai nghiem fandom va khu am thuc ngoai troi.",
            artists = listOf(
                Artist("1", "Son Tung M-TP", "https://i.pravatar.cc/150?u=sontung"),
                Artist("2", "Den Vau", "https://i.pravatar.cc/150?u=denvau"),
                Artist("3", "Touliver", "https://i.pravatar.cc/150?u=touliver"),
                Artist("4", "Bich Phuong", "https://i.pravatar.cc/150?u=bichphuong")
            ),
            timeline = listOf(
                EventMoment("18:00", "Mo cua don khach va check-in"),
                EventMoment("20:00", "Khai mac voi chuong trinh DJ set"),
                EventMoment("21:30", "Main stage")
            ),
            notices = listOf(
                "Su kien chi mo cua tu 16 tuoi tro len.",
                "Khong mang do uong, chat de chay no hoac vat dung ghi hinh chuyen nghiep."
            ),
            imageRes = R.drawable.event_concert
        ),
        Event(
            id = "art-expo",
            title = "Trien Lam Nghe Thuat Duong Dai",
            subtitle = "Khong gian nghe thuat thi giac moi",
            schedule = "09:00, Chu Nhat 28/07/2024",
            venue = "Bao tang My thuat",
            city = "TP. Ho Chi Minh",
            description = "Bo suu tap nghe thuat duong dai gom tranh, installation va workshop giao luu voi curator tre.",
            artists = listOf(
                Artist("5", "Linh Dao", "https://i.pravatar.cc/150?u=linhdao"),
                Artist("6", "Vu Hoang", "https://i.pravatar.cc/150?u=vuhoang"),
                Artist("7", "Khanh Nhi", "https://i.pravatar.cc/150?u=khanhnhi")
            ),
            timeline = listOf(
                EventMoment("09:00", "Mo cua phong trung bay"),
                EventMoment("11:00", "Artist talk"),
                EventMoment("14:00", "Workshop collage")
            ),
            notices = listOf(
                "Ve workshop gioi han theo khung gio.",
                "Khuyen khich dat truoc de giu cho."
            ),
            imageRes = R.drawable.event_gallery
        )
    )

    override val tiers = listOf(
        TicketTier("vip", "neon-night", "Zone A (VIP)", "Gan san khau nhat, loi di rieng, do uong mien phi", 2_500_000, TierStatus.LIMITED),
        TicketTier("standard", "neon-night", "Zone B (Standard)", "Tam nhin bao quat, khu dung chung", 800_000, TierStatus.AVAILABLE),
        TicketTier("earlybird", "neon-night", "Zone C (Early Bird)", "Gia uu dai mo ban som", 500_000, TierStatus.SOLD_OUT),
        TicketTier("expo-pass", "art-expo", "Pass Gallery", "Truy cap tron ngay va talkshow", 350_000, TierStatus.AVAILABLE)
    )

    override val posts = listOf(
        CommunityPost(
            id = "p1",
            authorId = "user-minh-tuan",
            author = "Minh Tuấn",
            role = "Fan cuồng Sơn Tùng",
            topic = "Trao đổi cá nhân",
            content = "Không thể tin được setlist đêm qua. Ai có video đoạn highnote cuối cùng thì chia sẻ với mình với.",
            likes = 128,
            comments = 24,
            shareCount = 4,
            imageRes = R.drawable.event_concert,
            imageUrl = "android.resource://com.example.myapplication/drawable/event_concert",
            mediaUrl = "android.resource://com.example.myapplication/drawable/event_concert",
            mediaType = "image/png",
            sharedPost = SharedCommunityPost(
                author = "Hồng Quang",
                caption = "a"
            ),
            createdAtMillis = 1_746_851_200_000,
            updatedAtMillis = 1_746_851_200_000
        ),
        CommunityPost(
            id = "p2",
            authorId = "fanzone-official",
            author = "FanZone Official",
            role = "Thông báo cộng đồng",
            topic = "Mở đợt pre-sale độc quyền",
            content = "Tuần tới sẽ có đợt pre-sale độc quyền cho khách đã cập nhật hồ sơ thành viên. Nhớ kiểm tra profile trước 20:00 tối thứ Sáu.",
            likes = 84,
            comments = 18,
            shareCount = 2,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            eventId = "neon-night",
            eventTitle = "Neon Nights Festival 2024",
            createdAtMillis = 1_778_393_600_000,
            updatedAtMillis = 1_778_393_600_000
        ),
        CommunityPost(
            id = "p3",
            authorId = "user-hoang-lam",
            author = "Hoàng Lâm",
            role = "Thành viên đã mua vé",
            topic = "Lập nhóm check-in",
            content = "Mình lập nhóm chat để cùng trao đổi và cập nhật thông tin check-in. Bạn nào đi Neon Nights một mình thì vào chung cho vui nhé.",
            likes = 37,
            comments = 12,
            shareCount = 1,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            eventId = "neon-night",
            eventTitle = "Neon Nights Festival 2024",
            createdAtMillis = 1_778_307_200_000,
            updatedAtMillis = 1_778_307_200_000
        )
    )

    override val walletSeed = listOf(
        TicketWalletItem(
            id = "seed-1",
            eventId = "art-expo",
            eventTitle = "Trien Lam Nghe Thuat Duong Dai",
            seatLabel = "PASS ALL DAY",
            schedule = "09:00, Chu Nhat 28/07/2024",
            venue = "Bao tang My thuat, TP. Ho Chi Minh",
            qrCode = "QR-ART-1024",
            status = TicketStatus.UPCOMING
        )
    )

    override val paymentMethods = listOf(
        PaymentMethod("visa", "Visa/Mastercard", "The quoc te va noi dia"),
        PaymentMethod("bank", "Chuyen khoan ngan hang", "Xac nhan tu dong trong 1-3 phut"),
        PaymentMethod("apple", "Apple Pay", "Thanh toan mot cham")
    )

    override val supportShortcuts = listOf(
        SupportShortcut("refund", "Chinh sach hoan ve"),
        SupportShortcut("invoice", "Lay hoa don VAT"),
        SupportShortcut("transfer", "Huong dan chuyen nhuong"),
        SupportShortcut("scan", "Huong dan check-in")
    )
}

