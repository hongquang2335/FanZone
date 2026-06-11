package com.example.myapplication.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.CommunityCard
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.feature.authentication.AuthUiState
import com.example.myapplication.feature.authentication.AuthUser
import com.example.myapplication.core.util.AppStrings

private data class ProfileColors(
    val background: Color,
    val panel: Color,
    val primaryText: Color,
    val mutedText: Color,
    val divider: Color,
    val sectionIcon: Color,
    val languageBadge: Color
)

@Composable
private fun profileColors(): ProfileColors {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f
    return ProfileColors(
        background = colorScheme.background,
        panel = colorScheme.surface,
        primaryText = colorScheme.onBackground,
        mutedText = colorScheme.onBackground.copy(alpha = if (isDark) 0.64f else 0.58f),
        divider = colorScheme.outline.copy(alpha = if (isDark) 0.48f else 0.72f),
        sectionIcon = colorScheme.primary,
        languageBadge = colorScheme.onBackground.copy(alpha = if (isDark) 0.18f else 0.08f)
    )
}

@Composable
fun ProfileScreen(
    user: UserProfile,
    authState: AuthUiState,
    unreadSupport: Int,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    avatarUrl: String? = null,
    followerCount: Int = 0,
    followingCount: Int = 0,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    onOpenProfileOptions: () -> Unit,
    onSignOut: () -> Unit,
    onOpenProfile: (String) -> Unit = {},
    unreadNotificationCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (authState.isSignedIn) {
        SignedInProfileScreen(
            authUser = authState.user,
            posts = posts,
            commentsByPostId = commentsByPostId,
            avatarUrl = avatarUrl ?: authState.accountProfile.avatarUrl,
            followerCount = followerCount,
            followingCount = followingCount,
            onSharePost = onSharePost,
            onToggleLike = onToggleLike,
            onToggleFollow = onToggleFollow,
            onOpenComments = onOpenComments,
            onAddComment = onAddComment,
            onDeletePost = onDeletePost,
            onEditPost = onEditPost,
            onOpenProfileOptions = onOpenProfileOptions,
            onOpenProfile = onOpenProfile,
            unreadNotificationCount = unreadNotificationCount,
            onOpenNotifications = onOpenNotifications,
            modifier = modifier
        )
        return
    }

    val profileColors = profileColors()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
    ) {
        val compactHeight = maxHeight < 700.dp
        val horizontalPadding = if (maxWidth < 360.dp) 18.dp else 24.dp
        val headerHeight = if (compactHeight) 190.dp else 224.dp
        val patternHeight = if (compactHeight) 132.dp else 156.dp
        val avatarSize = if (compactHeight) 88.dp else 104.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {
                AccountHeaderPattern(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(patternHeight)
                )
                IconButton(
                    onClick = onOpenAuth,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 12.dp, end = horizontalPadding)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Thông báo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                GuestAvatar(
                    size = avatarSize,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-6).dp)
                )
            }

            Text(
                text = AppStrings.Profile.LOGIN_SIGNUP,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenAuth
                    )
                    .padding(top = 4.dp),
                color = Evergreen,
                style = if (compactHeight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = if (compactHeight) 22.dp else 32.dp),
                verticalArrangement = Arrangement.spacedBy(if (compactHeight) 14.dp else 18.dp)
            ) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSupport)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(icon = Icons.AutoMirrored.Filled.Help, title = AppStrings.Profile.HELP_CENTER, colors = profileColors)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = AppStrings.Profile.HELP_CENTER_DESC,
                        tint = profileColors.mutedText,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

        }
    }
}

@Composable
private fun SignedInProfileScreen(
    authUser: AuthUser?,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    avatarUrl: String?,
    followerCount: Int,
    followingCount: Int,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    onOpenProfileOptions: () -> Unit,
    onOpenProfile: (String) -> Unit,
    unreadNotificationCount: Int,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileColors = profileColors()
    val displayName = authUser?.displayName.orEmpty()
    val initial = displayName.firstOrNull()
        ?: authUser?.email?.firstOrNull()
    val userPosts = posts.filter { it.authorId == authUser?.uid }
    val totalLikes = userPosts.sumOf { it.likes }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
    ) {
        val compactHeight = maxHeight < 700.dp
        val horizontalPadding = if (maxWidth < 360.dp) 18.dp else 24.dp
        val headerHeight = if (compactHeight) 190.dp else 224.dp
        val patternHeight = if (compactHeight) 132.dp else 156.dp
        val avatarSize = if (compactHeight) 96.dp else 116.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(headerHeight)) {
                AccountHeaderPattern(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(patternHeight)
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 12.dp, end = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onOpenNotifications
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Thông báo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(10.dp)
                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = onOpenProfileOptions
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = AppStrings.Profile.OPTIONS_TITLE,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                        .size(avatarSize),
                    shape = CircleShape,
                    color = Color(0xFF078E81)
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        CircleAvatar(size = avatarSize, imageUrl = avatarUrl)
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initial?.uppercaseChar()?.toString().orEmpty(),
                                color = Color.White,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Text(
                text = displayName.ifBlank { authUser?.email.orEmpty() },
                modifier = Modifier.fillMaxWidth(),
                color = profileColors.primaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            ProfileStatsRow(
                following = followingCount,
                followers = followerCount,
                likes = totalLikes,
                colors = profileColors,
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = if (compactHeight) 14.dp else 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = AppStrings.Profile.YOUR_POSTS,
                    color = profileColors.primaryText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (userPosts.isEmpty()) {
                    EmptyProfilePosts(colors = profileColors)
                } else {
                    userPosts.forEach { post ->
                        CommunityCard(
                            post = post,
                            currentAuthorName = displayName,
                            currentUserId = authUser?.uid,
                            comments = commentsByPostId[post.id].orEmpty(),
                            onSharePost = onSharePost,
                            onToggleLike = { onToggleLike(post.id) },
                            onToggleFollow = onToggleFollow,
                            onOpenComments = { onOpenComments(post.id) },
                            onAddComment = { text -> onAddComment(post.id, text) },
                            onOpenAuth = {},
                            onOpenProfile = onOpenProfile,
                            onDeletePost = { onDeletePost(post.id) },
                            onEditPost = { onEditPost(post) }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun ProfileOptionsScreen(
    onBack: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenSupport: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileColors = profileColors()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
            .navigationBarsPadding()
    ) {
        ProfileOptionsHeader(title = AppStrings.Profile.OPTIONS_TITLE, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            SectionTitle(icon = Icons.Default.Person, title = AppStrings.Profile.ACCOUNT_SETTINGS, colors = profileColors)
            Surface(shape = RoundedCornerShape(18.dp), color = profileColors.panel) {
                Column {
                    AccountRow(AppStrings.Profile.ACCOUNT_INFO, colors = profileColors, onClick = onOpenAccountInfo)
                    AccountDivider(colors = profileColors)
                    AccountRow(AppStrings.Profile.NOTIFICATION_SETTINGS, colors = profileColors, onClick = onOpenNotificationSettings)
                }
            }
            ProfileNavRow(icon = Icons.AutoMirrored.Filled.Help, title = AppStrings.Profile.HELP_CENTER, colors = profileColors, onClick = onOpenSupport)
            ProfileNavRow(icon = Icons.AutoMirrored.Filled.Logout, title = AppStrings.Profile.LOGOUT, colors = profileColors, onClick = onSignOut)
        }
    }
}

@Composable
private fun ProfileOptionsHeader(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .background(Evergreen)
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .size(46.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.Profile.BACK, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileStatsRow(
    following: Int,
    followers: Int,
    likes: Int,
    colors: ProfileColors,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = colors.panel) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileStat(value = following.toString(), label = AppStrings.Profile.FOLLOWING_STAT, colors = colors, modifier = Modifier.weight(1f))
            ProfileStat(value = followers.toString(), label = AppStrings.Profile.FOLLOWERS_STAT, colors = colors, modifier = Modifier.weight(1f))
            ProfileStat(value = likes.toString(), label = AppStrings.Profile.LIKES_STAT, colors = colors, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, colors: ProfileColors, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, color = colors.primaryText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(label, color = colors.mutedText, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun EmptyProfilePosts(colors: ProfileColors) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = colors.panel) {
        Text(
            text = AppStrings.Profile.NO_POSTS_SELF,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 34.dp).fillMaxWidth(),
            color = colors.mutedText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfilePostItem(post: CommunityPost, colors: ProfileColors) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = colors.panel) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(post.topic, color = Evergreen, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(post.content, color = colors.primaryText, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${post.likes}${AppStrings.Profile.LIKES_SUFFIX}", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
                Text("${post.comments}${AppStrings.Profile.COMMENTS_SUFFIX}", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
                Text("${post.shareCount}${AppStrings.Profile.SHARES_SUFFIX}", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AccountHeaderPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF31C77B))
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        val iconTint = Color(0xFF157A4C).copy(alpha = 0.55f)
        PatternIcon(Icons.Default.SportsSoccer, iconTint, Modifier.align(Alignment.TopStart).size(48.dp))
        PatternIcon(Icons.Default.Movie, iconTint, Modifier.align(Alignment.TopCenter).offset(x = (-34).dp).size(40.dp))
        PatternIcon(Icons.Default.ConfirmationNumber, iconTint, Modifier.align(Alignment.Center).offset(x = (-86).dp).size(38.dp))
        PatternIcon(Icons.Default.Palette, iconTint, Modifier.align(Alignment.CenterEnd).size(56.dp))
        PatternIcon(Icons.Default.Mic, iconTint, Modifier.align(Alignment.BottomCenter).offset(x = 118.dp).size(44.dp))
        PatternIcon(Icons.Default.Language, iconTint, Modifier.align(Alignment.BottomStart).offset(x = 24.dp).size(48.dp))
    }
}

@Composable
private fun PatternIcon(icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Icon(icon, contentDescription = null, tint = tint, modifier = modifier)
}

@Composable
private fun GuestAvatar(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 126.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color(0xFFF7F7FA),
        border = BorderStroke(5.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(size * 0.7f),
                shape = CircleShape,
                color = Color(0xFFEDEDF1),
                border = BorderStroke(3.dp, Color(0xFFB7B6BE))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF8D8C94),
                        modifier = Modifier.size(size * 0.46f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String, colors: ProfileColors) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colors.sectionIcon, modifier = Modifier.size(26.dp))
        Text(
            text = title,
            color = colors.primaryText,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun AccountRow(title: String, colors: ProfileColors, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = colors.primaryText, style = MaterialTheme.typography.bodyLarge)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.mutedText,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun AccountDivider(colors: ProfileColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.divider)
    )
}

@Composable
private fun ProfileNavRow(
    icon: ImageVector,
    title: String,
    colors: ProfileColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(icon = icon, title = title, colors = colors)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.mutedText,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun LanguageBadge(colors: ProfileColors) {
    Surface(shape = RoundedCornerShape(28.dp), color = colors.languageBadge) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = Color(0xFFE22D28), modifier = Modifier.size(28.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("*", color = Color(0xFFFFEB3B), fontWeight = FontWeight.ExtraBold)
                }
            }
            Text("Vie", color = colors.primaryText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ViewedProfileScreen(
    viewedUserId: String,
    displayName: String,
    avatarUrl: String?,
    followerCount: Int,
    followingCount: Int,
    isFollowing: Boolean,
    posts: List<CommunityPost>,
    commentsByPostId: Map<String, List<CommunityComment>>,
    currentUserId: String?,
    onToggleFollow: () -> Unit,
    onSharePost: (CommunityPost, String) -> Unit,
    onToggleLike: (String) -> Unit,
    onOpenComments: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onBack: () -> Unit,
    onOpenAuth: () -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (com.example.myapplication.domain.model.CommunityPost) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profileColors = profileColors()
    val initial = displayName.firstOrNull()
    val totalLikes = posts.sumOf { it.likes }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
    ) {
        val compactHeight = maxHeight < 700.dp
        val horizontalPadding = if (maxWidth < 360.dp) 18.dp else 24.dp
        val headerHeight = if (compactHeight) 190.dp else 224.dp
        val patternHeight = if (compactHeight) 132.dp else 156.dp
        val avatarSize = if (compactHeight) 96.dp else 116.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(headerHeight)) {
                AccountHeaderPattern(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(patternHeight)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                        .size(avatarSize),
                    shape = CircleShape,
                    color = Color(0xFF078E81)
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        CircleAvatar(size = avatarSize, imageUrl = avatarUrl)
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initial?.uppercaseChar()?.toString().orEmpty(),
                                color = Color.White,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Text(
                text = displayName,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = profileColors.primaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (currentUserId != viewedUserId) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (currentUserId == null) {
                                onOpenAuth()
                            } else {
                                onToggleFollow()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color.LightGray else Evergreen,
                            contentColor = if (isFollowing) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.widthIn(min = 125.dp)
                    ) {
                        Text(text = if (isFollowing) AppStrings.Profile.FOLLOWED else AppStrings.Profile.FOLLOW)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            ProfileStatsRow(
                following = followingCount,
                followers = followerCount,
                likes = totalLikes,
                colors = profileColors,
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = if (compactHeight) 14.dp else 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = AppStrings.Profile.POSTS_OF_USER.format(displayName),
                    color = profileColors.primaryText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (posts.isEmpty()) {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = profileColors.panel) {
                        Text(
                            text = AppStrings.Profile.NO_POSTS_OTHER.format(displayName),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 34.dp).fillMaxWidth(),
                            color = profileColors.mutedText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    posts.forEach { post ->
                        CommunityCard(
                            post = post,
                            currentAuthorName = displayName,
                            currentUserId = currentUserId,
                            comments = commentsByPostId[post.id].orEmpty(),
                            onSharePost = onSharePost,
                            onToggleLike = { onToggleLike(post.id) },
                            onToggleFollow = { onToggleFollow() },
                            onOpenComments = { onOpenComments(post.id) },
                            onAddComment = { text -> onAddComment(post.id, text) },
                            onOpenAuth = onOpenAuth,
                            onDeletePost = { onDeletePost(post.id) },
                            onEditPost = { onEditPost(post) }
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 12.dp, start = horizontalPadding)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = AppStrings.Profile.BACK,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
