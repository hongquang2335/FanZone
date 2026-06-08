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
        modifier = modifier
    )
}

@Composable
fun EventCommunityRoute(
    event: Event,
    eventId: String?,
    onOpenAuth: () -> Unit,
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
        onBack = onBack,
        modifier = modifier
    )
}
