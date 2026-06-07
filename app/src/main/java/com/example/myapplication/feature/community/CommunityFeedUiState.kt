package com.example.myapplication.feature.community

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.SocialProfile
import com.example.myapplication.domain.model.celebrityProfiles

data class CommunityFeedUiState(
    val fallbackPosts: List<CommunityPost> = emptyList(),
    val remotePosts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = true,
    val currentAuthorName: String = "Ban",
    val currentUserId: String? = null,
    val followedProfileIds: Set<String> = emptySet(),
    val errorMessage: String? = null
) {
    val posts: List<CommunityPost>
        get() = remotePosts + fallbackPosts.filter { fallback ->
            remotePosts.none { remote -> remote.id == fallback.id }
        }

    val profiles: List<SocialProfile>
        get() {
            val seededById = celebrityProfiles.associateBy { it.id }
            val postProfiles = posts.mapNotNull { post ->
                val id = post.authorId ?: return@mapNotNull null
                if (seededById.containsKey(id)) return@mapNotNull null
                SocialProfile(
                    id = id,
                    displayName = post.author,
                    handle = "@${post.author.lowercase().filter { it.isLetterOrDigit() }.ifBlank { id }}",
                    bio = "Thanh vien FanZone dang chia se ve su kien va trai nghiem cong dong.",
                    role = post.role,
                    verified = false,
                    followerCount = posts.count { it.authorId == id } * 12,
                    followingCount = 8,
                    avatarColor = 0xFF078E81
                )
            }
            return (celebrityProfiles + postProfiles).distinctBy { it.id }
        }

    fun profileById(profileId: String?): SocialProfile? {
        if (profileId == null) return null
        return profiles.firstOrNull { it.id == profileId }
    }
}
