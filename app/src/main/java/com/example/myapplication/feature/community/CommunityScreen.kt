package com.example.myapplication.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.core.designsystem.component.SectionHeader

@Composable
fun CommunityScreen(
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>> = emptyMap(),
    currentAuthorName: String,
    currentAuthorAvatarUrl: String?,
    currentUserId: String?,
    onOpenEvent: (String) -> Unit,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { ComposerCard(currentAuthorAvatarUrl = currentAuthorAvatarUrl, onOpenAuth = onOpenAuth) }
            if (isExpanded) {
                item { SectionHeader("Dòng bài viết nổi bật", "Có sự kiện kèm tag") }
            }
            if (isExpanded) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        posts.chunked(2).forEach { group ->
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                group.forEach { post ->
                                    CommunityCard(
                                        post = post,
                                        currentAuthorName = currentAuthorName,
                                        currentUserId = currentUserId,
                                        onOpenEventCommunity = onOpenEvent,
                                        onSharePost = onSharePost,
                                        onToggleLike = { onToggleLike(post.id) },
                                        onToggleFollow = onToggleFollow,
                                        comments = commentsByPostId[post.id].orEmpty(),
                                        onOpenComments = { onOpenComments(post.id) },
                                        onAddComment = { text -> onAddComment(post.id, text) },
                                        onOpenAuth = onOpenAuth,
                                        onOpenProfile = onOpenProfile
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                items(posts) { post ->
                    CommunityCard(
                        post = post,
                        currentAuthorName = currentAuthorName,
                        currentUserId = currentUserId,
                        onOpenEventCommunity = onOpenEvent,
                        onSharePost = onSharePost,
                        onToggleLike = { onToggleLike(post.id) },
                        onToggleFollow = onToggleFollow,
                        comments = commentsByPostId[post.id].orEmpty(),
                        onOpenComments = { onOpenComments(post.id) },
                        onAddComment = { text -> onAddComment(post.id, text) },
                        onOpenAuth = onOpenAuth,
                        onOpenProfile = onOpenProfile
                    )
                }
            }
        }
    }
}
