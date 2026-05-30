package com.example.myapplication.feature.community

import com.example.myapplication.domain.model.CommunityPost

data class CommunityFeedUiState(
    val fallbackPosts: List<CommunityPost> = emptyList(),
    val remotePosts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val posts: List<CommunityPost>
        get() = remotePosts + fallbackPosts.filter { fallback ->
            remotePosts.none { remote -> remote.id == fallback.id }
        }
}
