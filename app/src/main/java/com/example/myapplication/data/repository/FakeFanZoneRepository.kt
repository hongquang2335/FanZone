package com.example.myapplication.data.repository

import com.example.myapplication.R
import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventMoment
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.SharedCommunityPost
import com.example.myapplication.domain.model.celebrityProfiles
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
            artists = listOf("Son Tung M-TP", "Den Vau", "Touliver", "Bich Phuong"),
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
            artists = listOf("Linh Dao", "Vu Hoang", "Khanh Nhi"),
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
        ),
        CommunityPost(
            id = "celeb-post-son-tung",
            authorId = celebrityProfiles[0].id,
            author = celebrityProfiles[0].displayName,
            role = celebrityProfiles[0].role,
            topic = "San khau Neon Nights",
            content = "Gap nhau o Neon Nights nhe. Team nao da san sang hat that lon cung minh?",
            likes = 245_000,
            comments = 18_200,
            shareCount = 6_430,
            imageRes = R.drawable.event_concert,
            imageUrl = "android.resource://com.example.myapplication/drawable/event_concert",
            mediaUrl = "android.resource://com.example.myapplication/drawable/event_concert",
            mediaType = "image/png",
            eventId = "neon-night",
            eventTitle = "Neon Nights Festival 2024",
            createdAtMillis = 1_778_652_000_000,
            updatedAtMillis = 1_778_652_000_000
        ),
        CommunityPost(
            id = "celeb-post-den-vau",
            authorId = celebrityProfiles[1].id,
            author = celebrityProfiles[1].displayName,
            role = celebrityProfiles[1].role,
            topic = "Loi hen voi fan",
            content = "Co nhung cau rap chi that su song khi duoc nghe cung khan gia. Hen cac ban o dem nhac toi.",
            likes = 189_500,
            comments = 9_760,
            shareCount = 4_110,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            eventId = "neon-night",
            eventTitle = "Neon Nights Festival 2024",
            createdAtMillis = 1_778_566_400_000,
            updatedAtMillis = 1_778_566_400_000
        ),
        CommunityPost(
            id = "celeb-post-my-tam",
            authorId = celebrityProfiles[2].id,
            author = celebrityProfiles[2].displayName,
            role = celebrityProfiles[2].role,
            topic = "Cam on fan",
            content = "Moi lan gap khan gia la mot lan duoc tiep them nang luong. Cam on vi da luon o do.",
            likes = 312_800,
            comments = 21_430,
            shareCount = 7_850,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            createdAtMillis = 1_778_480_000_000,
            updatedAtMillis = 1_778_480_000_000
        ),
        CommunityPost(
            id = "celeb-post-hoang-thuy-linh",
            authorId = celebrityProfiles[3].id,
            author = celebrityProfiles[3].displayName,
            role = celebrityProfiles[3].role,
            topic = "Y tuong san khau",
            content = "Dang thu nghiem mot ban phoi moi cho san khau sap toi. Muon nghe fan doan bai nao nhat?",
            likes = 98_400,
            comments = 6_210,
            shareCount = 2_340,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            createdAtMillis = 1_778_393_600_000,
            updatedAtMillis = 1_778_393_600_000
        ),
        CommunityPost(
            id = "celeb-post-toc-tien",
            authorId = celebrityProfiles[4].id,
            author = celebrityProfiles[4].displayName,
            role = celebrityProfiles[4].role,
            topic = "Behind the scenes",
            content = "Dang chon outfit cho dem dien tiep theo. Nang luong phai that ruc ro moi hop vibe FanZone.",
            likes = 124_700,
            comments = 5_900,
            shareCount = 3_020,
            imageRes = R.drawable.event_gallery,
            imageUrl = "android.resource://com.example.myapplication/drawable/event_gallery",
            mediaUrl = "android.resource://com.example.myapplication/drawable/event_gallery",
            mediaType = "image/png",
            createdAtMillis = 1_778_307_200_000,
            updatedAtMillis = 1_778_307_200_000
        ),
        CommunityPost(
            id = "celeb-post-suboi",
            authorId = celebrityProfiles[5].id,
            author = celebrityProfiles[5].displayName,
            role = celebrityProfiles[5].role,
            topic = "Hip-hop corner",
            content = "Ai co beat yeu thich thi comment thu. Biet dau minh dem len freestyle trong fan meeting.",
            likes = 76_300,
            comments = 4_840,
            shareCount = 1_760,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            createdAtMillis = 1_778_220_800_000,
            updatedAtMillis = 1_778_220_800_000
        ),
        CommunityPost(
            id = "celeb-post-binz",
            authorId = celebrityProfiles[6].id,
            author = celebrityProfiles[6].displayName,
            role = celebrityProfiles[6].role,
            topic = "Rap show",
            content = "FanZone co ai muon nghe mot track moi o san khau sap toi khong?",
            likes = 88_900,
            comments = 4_320,
            shareCount = 1_980,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            createdAtMillis = 1_778_134_400_000,
            updatedAtMillis = 1_778_134_400_000
        ),
        CommunityPost(
            id = "celeb-post-bich-phuong",
            authorId = celebrityProfiles[7].id,
            author = celebrityProfiles[7].displayName,
            role = celebrityProfiles[7].role,
            topic = "Fan request",
            content = "Dang lap playlist cho dem dien moi. Bai nao cac ban muon hat cung minh nhat?",
            likes = 102_600,
            comments = 6_780,
            shareCount = 2_250,
            imageRes = null,
            imageUrl = null,
            mediaUrl = null,
            mediaType = null,
            createdAtMillis = 1_778_048_000_000,
            updatedAtMillis = 1_778_048_000_000
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

