package com.example.myapplication.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.feature.authentication.AuthUiState

@Composable
fun ProfileRoute(
    user: UserProfile,
    authState: AuthUiState,
    unreadSupport: Int,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenProfileOptions: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(user, authState, unreadSupport, posts) {
        viewModel.load(
            user = user,
            authState = authState,
            unreadSupport = unreadSupport,
            posts = posts
        )
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadedUser = uiState.user ?: user

    ProfileScreen(
        user = loadedUser,
        authState = uiState.authState,
        unreadSupport = uiState.unreadSupport,
        posts = uiState.posts,
        commentsByPostId = commentsByPostId,
        avatarUrl = uiState.avatarUrl,
        followerCount = uiState.followerCount,
        followingCount = uiState.followingCount,
        onSharePost = onSharePost,
        onToggleLike = onToggleLike,
        onToggleFollow = onToggleFollow,
        onOpenComments = onOpenComments,
        onAddComment = onAddComment,
        onOpenSupport = onOpenSupport,
        onOpenAuth = onOpenAuth,
        onOpenAccountInfo = onOpenAccountInfo,
        onOpenNotificationSettings = onOpenNotificationSettings,
        onOpenProfileOptions = onOpenProfileOptions,
        onSignOut = onSignOut,
        modifier = modifier
    )
}

@Composable
fun ProfileOptionsRoute(
    onBack: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenSupport: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileOptionsScreen(
        onBack = onBack,
        onOpenAccountInfo = onOpenAccountInfo,
        onOpenNotificationSettings = onOpenNotificationSettings,
        onOpenSupport = onOpenSupport,
        onSignOut = onSignOut,
        modifier = modifier
    )
}

@Composable
fun AccountInfoRoute(
    authState: AuthUiState,
    onSave: (String, String, String, String, String?, () -> Unit) -> Unit,
    onLinkGoogle: (String) -> Unit,
    onGoogleError: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccountInfoScreen(
        authUser = authState.user,
        accountProfile = authState.accountProfile,
        authState = authState,
        onSave = onSave,
        onLinkGoogle = onLinkGoogle,
        onGoogleError = onGoogleError,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun NotificationSettingsRoute(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    NotificationSettingsScreen(
        darkTheme = darkTheme,
        onDarkThemeChange = onDarkThemeChange,
        onBack = onBack,
        modifier = modifier
    )
}
