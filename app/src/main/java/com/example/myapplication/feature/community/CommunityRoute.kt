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
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CommunityScreen(
        posts = uiState.posts,
        commentsByPostId = uiState.mappedCommentsByPostId,
        currentAuthorName = uiState.currentAuthorName,
        currentAuthorAvatarUrl = uiState.currentAuthorAvatarUrl,
        currentUserId = uiState.currentUserId,
        isLoading = uiState.isLoading,
        onRefresh = viewModel::refreshPosts,
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
        unreadNotificationCount = uiState.unreadNotificationCount,
        onOpenNotifications = onOpenNotifications,
        errorMessage = uiState.errorMessage,
        targetCommentsPostId = uiState.targetCommentsPostId,
        onCommentsDismissed = { viewModel.setTargetCommentsPostId(null) },
        modifier = modifier
    )

}

@Composable
fun EventCommunityRoute(
    event: Event,
    eventId: String?,
    onOpenAuth: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EventCommunityScreen(
        event = event,
        posts = uiState.posts.filter { it.eventId == eventId },
        commentsByPostId = uiState.mappedCommentsByPostId,
        currentAuthorName = uiState.currentAuthorName,
        currentAuthorAvatarUrl = uiState.currentAuthorAvatarUrl,
        currentUserId = uiState.currentUserId,
        isLoading = uiState.isLoading,
        onRefresh = viewModel::refreshPosts,
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
        unreadNotificationCount = uiState.unreadNotificationCount,
        onOpenNotifications = onOpenNotifications,
        errorMessage = uiState.errorMessage,
        targetCommentsPostId = uiState.targetCommentsPostId,
        onCommentsDismissed = { viewModel.setTargetCommentsPostId(null) },
        modifier = modifier
    )

}
