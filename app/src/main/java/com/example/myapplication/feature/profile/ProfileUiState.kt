package com.example.myapplication.feature.profile

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.feature.authentication.AuthUiState

data class ProfileUiState(
    val user: UserProfile? = null,
    val authState: AuthUiState = AuthUiState(),
    val unreadSupport: Int = 0,
    val posts: List<CommunityPost> = emptyList(),
    val avatarUrl: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val unreadNotificationCount: Int = 0,
    val darkTheme: Boolean = false
)
