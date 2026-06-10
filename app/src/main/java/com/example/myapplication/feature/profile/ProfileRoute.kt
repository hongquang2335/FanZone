package com.example.myapplication.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.feature.authentication.AuthUiState
import com.google.firebase.firestore.FirebaseFirestore

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
    onOpenProfile: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    unreadNotificationCount: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(user, authState, unreadSupport, posts, unreadNotificationCount) {
        viewModel.load(
            user = user,
            authState = authState,
            unreadSupport = unreadSupport,
            posts = posts,
            unreadNotificationCount = unreadNotificationCount
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
        unreadNotificationCount = uiState.unreadNotificationCount,
        onSharePost = onSharePost,
        onToggleLike = onToggleLike,
        onToggleFollow = onToggleFollow,
        onOpenComments = onOpenComments,
        onAddComment = onAddComment,
        onOpenSupport = onOpenSupport,
        onOpenAuth = onOpenAuth,
        onOpenAccountInfo = onOpenAccountInfo,
        onOpenNotificationSettings = onOpenNotificationSettings,
        onDeletePost = onDeletePost,
        onEditPost = onEditPost,
        onOpenProfileOptions = onOpenProfileOptions,
        onSignOut = onSignOut,
        onOpenProfile = onOpenProfile,
        onOpenNotifications = onOpenNotifications,
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

@Composable
fun ViewedProfileRoute(
    profileId: String,
    currentUserId: String?,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onBack: () -> Unit,
    onOpenAuth: () -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val firestore = remember { FirebaseFirestore.getInstance() }
    var displayName by remember(profileId) { mutableStateOf("") }
    var avatarUrl by remember(profileId) { mutableStateOf<String?>(null) }
    var followerCount by remember(profileId) { mutableStateOf(0) }
    var followingCount by remember(profileId) { mutableStateOf(0) }
    var followerIds by remember(profileId) { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(profileId) {
        firestore.collection("users")
            .document(profileId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    displayName = snapshot.getString("displayName") ?: snapshot.getString("name") ?: "Thành viên"
                    avatarUrl = snapshot.getString("avatarUrl")
                    followerCount = snapshot.getLong("followers")?.toInt()
                        ?: (snapshot.get("followerIds") as? List<*>)?.size
                        ?: 0
                    followingCount = snapshot.getLong("following")?.toInt()
                        ?: (snapshot.get("followingIds") as? List<*>)?.size
                        ?: 0
                    followerIds = (snapshot.get("followerIds") as? List<*>)?.mapNotNull { it as? String }.orEmpty()
                }
            }
    }

    val isFollowing = currentUserId != null && followerIds.contains(currentUserId)
    val userPosts = posts.filter { it.authorId == profileId }

    ViewedProfileScreen(
        viewedUserId = profileId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        followerCount = followerCount,
        followingCount = followingCount,
        isFollowing = isFollowing,
        posts = userPosts,
        commentsByPostId = commentsByPostId,
        currentUserId = currentUserId,
        onToggleFollow = { onToggleFollow(profileId) },
        onSharePost = onSharePost,
        onToggleLike = onToggleLike,
        onOpenComments = onOpenComments,
        onAddComment = onAddComment,
        onBack = onBack,
        onOpenAuth = onOpenAuth,
        onDeletePost = onDeletePost,
        onEditPost = onEditPost,
        modifier = modifier
    )
}
