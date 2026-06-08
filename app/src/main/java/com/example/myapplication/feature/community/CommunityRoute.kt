package com.example.myapplication.feature.community

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.domain.model.Event

@Composable
fun CommunityRoute(
    onOpenEvent: (String) -> Unit,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CommunityScreen(
        posts = uiState.posts,
        commentsByPostId = uiState.commentsByPostId,
        currentAuthorName = uiState.currentAuthorName,
        currentAuthorAvatarUrl = uiState.currentAuthorAvatarUrl,
        currentUserId = uiState.currentUserId,
        onOpenEvent = onOpenEvent,
        onSharePost = viewModel::sharePost,
        onToggleLike = viewModel::toggleLike,
        onToggleFollow = viewModel::toggleFollow,
        onOpenComments = viewModel::observeComments,
        onAddComment = viewModel::addComment,
        onOpenAuth = onOpenAuth,
        onOpenProfile = onOpenProfile,
        onDeletePost = viewModel::deletePost,
        onEditPost = viewModel::openEditPost,
        modifier = modifier
    )

    uiState.editingPost?.let { post ->
        com.example.myapplication.core.designsystem.component.EditPostDialog(
            post = post,
            onDismiss = viewModel::closeEditPost,
            onSave = { newText, newMedia ->
                viewModel.updatePost(post.id, newText, newMedia)
            }
        )
    }
}

@Composable
fun EventCommunityRoute(
    event: Event,
    eventId: String?,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EventCommunityScreen(
        event = event,
        posts = uiState.posts.filter { it.eventId == eventId },
        commentsByPostId = uiState.commentsByPostId,
        currentAuthorName = uiState.currentAuthorName,
        currentAuthorAvatarUrl = uiState.currentAuthorAvatarUrl,
        currentUserId = uiState.currentUserId,
        onSharePost = viewModel::sharePost,
        onToggleLike = viewModel::toggleLike,
        onToggleFollow = viewModel::toggleFollow,
        onOpenComments = viewModel::observeComments,
        onAddComment = viewModel::addComment,
        onOpenAuth = onOpenAuth,
        onOpenProfile = onOpenProfile,
        onDeletePost = viewModel::deletePost,
        onEditPost = viewModel::openEditPost,
        onBack = onBack,
        modifier = modifier
    )

    uiState.editingPost?.let { post ->
        com.example.myapplication.core.designsystem.component.EditPostDialog(
            post = post,
            onDismiss = viewModel::closeEditPost,
            onSave = { newText, newMedia ->
                viewModel.updatePost(post.id, newText, newMedia)
            }
        )
    }
}
