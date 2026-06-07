package com.example.myapplication.domain.model

import androidx.compose.ui.graphics.Color

data class SocialProfile(
    val id: String,
    val displayName: String,
    val handle: String,
    val bio: String,
    val role: String,
    val verified: Boolean = false,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val avatarColor: Long = 0xFF078E81
) {
    val avatarInitial: String
        get() = displayName.firstOrNull()?.uppercaseChar()?.toString().orEmpty()

    val avatarComposeColor: Color
        get() = Color(avatarColor)
}

val celebrityProfiles = listOf(
    SocialProfile(
        id = "celeb-son-tung",
        displayName = "Son Tung M-TP",
        handle = "@sontungmtp",
        bio = "Ca si, songwriter va founder M-TP Entertainment.",
        role = "Nghe si am nhac",
        verified = true,
        followerCount = 5_420_000,
        followingCount = 18,
        avatarColor = 0xFF1D4ED8
    ),
    SocialProfile(
        id = "celeb-den-vau",
        displayName = "Den Vau",
        handle = "@denvau",
        bio = "Rapper ke chuyen bang am nhac va nhung dem live day cam xuc.",
        role = "Rapper",
        verified = true,
        followerCount = 3_210_000,
        followingCount = 42,
        avatarColor = 0xFF111827
    ),
    SocialProfile(
        id = "celeb-my-tam",
        displayName = "My Tam",
        handle = "@mytam",
        bio = "Ca si, nhac si va nha san xuat am nhac.",
        role = "Ca si",
        verified = true,
        followerCount = 4_880_000,
        followingCount = 25,
        avatarColor = 0xFFD946EF
    ),
    SocialProfile(
        id = "celeb-hoang-thuy-linh",
        displayName = "Hoang Thuy Linh",
        handle = "@hoangthuylinh",
        bio = "Nghe si pop voi nhung san khau lay cam hung tu van hoa Viet.",
        role = "Ca si",
        verified = true,
        followerCount = 2_760_000,
        followingCount = 31,
        avatarColor = 0xFFF97316
    ),
    SocialProfile(
        id = "celeb-toc-tien",
        displayName = "Toc Tien",
        handle = "@toctien",
        bio = "Ca si, performer va nguoi yeu thoi trang san khau.",
        role = "Ca si",
        verified = true,
        followerCount = 2_940_000,
        followingCount = 64,
        avatarColor = 0xFFE11D48
    ),
    SocialProfile(
        id = "celeb-suboi",
        displayName = "Suboi",
        handle = "@suboi",
        bio = "Rapper, songwriter va mot trong nhung giong noi hip-hop Viet Nam.",
        role = "Rapper",
        verified = true,
        followerCount = 1_390_000,
        followingCount = 83,
        avatarColor = 0xFF0F766E
    ),
    SocialProfile(
        id = "celeb-binz",
        displayName = "Binz",
        handle = "@binz",
        bio = "Rapper, producer va nghe si cua SpaceSpeakers.",
        role = "Rapper",
        verified = true,
        followerCount = 2_180_000,
        followingCount = 44,
        avatarColor = 0xFF7C3AED
    ),
    SocialProfile(
        id = "celeb-bich-phuong",
        displayName = "Bich Phuong",
        handle = "@bichphuong",
        bio = "Ca si pop voi nhieu ban hit duoc fan yeu thich.",
        role = "Ca si",
        verified = true,
        followerCount = 2_510_000,
        followingCount = 36,
        avatarColor = 0xFF0891B2
    )
)

