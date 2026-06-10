package com.example.myapplication.feature.community

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.CommunityComment

data class CommunityUiState(
    val remotePosts: List<CommunityPost> = emptyList(),
    val commentsByPostId: Map<String, List<CommunityComment>> = emptyMap(),
    val isLoading: Boolean = true,
    val currentAuthorName: String = "Ban",
    val currentAuthorAvatarUrl: String? = null,
    val currentUserId: String? = null,
    val followedProfileIds: Set<String> = emptySet(),
    val editingPost: CommunityPost? = null,
    val unreadNotificationCount: Int = 0,
    val errorMessage: String? = null
) {
    val posts: List<CommunityPost>
        get() = remotePosts.map { post ->
            post.copy(isAuthorFollowed = post.authorId != null && followedProfileIds.contains(post.authorId))
        }
}
