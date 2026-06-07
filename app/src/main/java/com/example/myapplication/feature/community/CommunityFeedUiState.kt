package com.example.myapplication.feature.community

import com.example.myapplication.domain.model.CommunityPost

data class CommunityFeedUiState(
    val remotePosts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = true,
    val currentAuthorName: String = "Ban",
    val currentUserId: String? = null,
    val followedProfileIds: Set<String> = emptySet(),
    val errorMessage: String? = null
) {
    val posts: List<CommunityPost>
        get() = remotePosts
}
