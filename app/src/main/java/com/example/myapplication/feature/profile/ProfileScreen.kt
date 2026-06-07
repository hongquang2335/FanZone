package com.example.myapplication.feature.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.SocialProfile
import com.example.myapplication.domain.model.UserProfile

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
    onOpenSupport: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenPinSetup: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenProfileOptions: () -> Unit,
    onChangeAvatar: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (authState.isSignedIn) {
        SignedInProfileScreen(
            authUser = authState.user,
            accountProfile = authState.accountProfile,
            posts = posts,
            onChangeAvatar = onChangeAvatar,
            onOpenProfileOptions = onOpenProfileOptions,
            modifier = modifier
        )
        return
    }

    val profileColors = profileColors()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
            .navigationBarsPadding()
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
                        .statusBarsPadding()
                )
                GuestAvatar(
                    size = avatarSize,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-6).dp)
                )
            }

            Text(
                text = "Đăng nhập/Đăng ký",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAuth)
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
                SectionTitle(icon = Icons.Default.Settings, title = "Cài đặt ứng dụng", colors = profileColors)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = profileColors.panel
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thay đổi ngôn ngữ",
                            color = profileColors.primaryText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        LanguageBadge(colors = profileColors)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenSupport)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(icon = Icons.Default.Help, title = "Trung tâm trợ giúp", colors = profileColors)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mở trung tâm trợ giúp",
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
    accountProfile: AccountProfile,
    posts: List<CommunityPost>,
    onChangeAvatar: (String) -> Unit,
    onOpenProfileOptions: () -> Unit,
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
            .navigationBarsPadding()
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
                        .statusBarsPadding()
                )
                IconButton(
                    onClick = onOpenProfileOptions,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 12.dp, end = horizontalPadding)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Mở tùy chọn profile",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                EditableProfileAvatar(
                    initial = initial?.uppercaseChar()?.toString().orEmpty(),
                    avatarUri = accountProfile.avatarUri,
                    size = avatarSize,
                    onChangeAvatar = onChangeAvatar,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                )
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
                following = 0,
                followers = 0,
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
                    text = "Bài viết của bạn",
                    color = profileColors.primaryText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (userPosts.isEmpty()) {
                    EmptyProfilePosts(colors = profileColors)
                } else {
                    userPosts.forEach { post ->
                        ProfilePostItem(post = post, colors = profileColors)
                    }
                }
            }

        }
    }
}

@Composable
fun PublicProfileScreen(
    profile: SocialProfile,
    posts: List<CommunityPost>,
    isFollowing: Boolean,
    isCurrentUser: Boolean,
    onBack: () -> Unit,
    onToggleFollow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileColors = profileColors()
    val profilePosts = posts.filter { it.authorId == profile.id }
    val totalLikes = profilePosts.sumOf { it.likes }
    val followers = profile.followerCount + if (isFollowing) 1 else 0

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(profileColors.background)
            .navigationBarsPadding()
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
                        .statusBarsPadding()
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(top = 12.dp, start = horizontalPadding)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lai",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                VerifiedProfileAvatar(
                    profile = profile,
                    size = avatarSize,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profile.displayName,
                    color = profileColors.primaryText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (profile.verified) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Tai khoan da xac minh",
                        tint = Color(0xFF1D9BF0),
                        modifier = Modifier.padding(start = 6.dp).size(22.dp)
                    )
                }
            }

            Text(
                text = profile.handle,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                color = profileColors.mutedText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = profile.bio,
                modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = 12.dp),
                color = profileColors.primaryText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            if (!isCurrentUser) {
                Button(
                    onClick = onToggleFollow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) profileColors.panel else Evergreen,
                        contentColor = if (isFollowing) profileColors.primaryText else Color.White
                    )
                ) {
                    Text(if (isFollowing) "Dang follow" else "Follow", fontWeight = FontWeight.Bold)
                }
            }

            ProfileStatsRow(
                following = profile.followingCount,
                followers = followers,
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
                    text = "Bai viet",
                    color = profileColors.primaryText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                if (profilePosts.isEmpty()) {
                    EmptyProfilePosts(colors = profileColors)
                } else {
                    profilePosts.forEach { post ->
                        ProfilePostItem(post = post, colors = profileColors)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableProfileAvatar(
    initial: String,
    avatarUri: String?,
    size: androidx.compose.ui.unit.Dp,
    onChangeAvatar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            onChangeAvatar(uri.toString())
        }
    }

    Box(modifier = modifier.size(size + 10.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = Color(0xFF078E81),
            border = BorderStroke(5.dp, Color.White)
        ) {
            if (avatarUri != null) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "Anh dai dien",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initial,
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(38.dp)
                .clickable { launcher.launch(arrayOf("image/*")) },
            shape = CircleShape,
            color = Evergreen,
            border = BorderStroke(3.dp, Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Doi anh dai dien",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun VerifiedProfileAvatar(
    profile: SocialProfile,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size + 12.dp), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = profile.avatarComposeColor,
            border = BorderStroke(5.dp, Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = profile.avatarInitial,
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (profile.verified) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Tai khoan da xac minh",
                        tint = Color(0xFF1D9BF0),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileOptionsScreen(
    onBack: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenPinSetup: () -> Unit,
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
        ProfileOptionsHeader(title = "Tùy chọn", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            SectionTitle(icon = Icons.Default.Person, title = "Cài đặt tài khoản", colors = profileColors)
            Surface(shape = RoundedCornerShape(18.dp), color = profileColors.panel) {
                Column {
                    AccountRow("Thông tin tài khoản", colors = profileColors, onClick = onOpenAccountInfo)
                    AccountDivider(colors = profileColors)
                    AccountRow("Thiết lập mã PIN", colors = profileColors, onClick = onOpenPinSetup)
                    AccountDivider(colors = profileColors)
                    AccountRow("Cài đặt", colors = profileColors, onClick = onOpenNotificationSettings)
                }
            }
            ProfileNavRow(icon = Icons.Default.Help, title = "Trung tâm trợ giúp", colors = profileColors, onClick = onOpenSupport)
            ProfileNavRow(icon = Icons.Default.Logout, title = "Đăng xuất", colors = profileColors, onClick = onSignOut)
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
                .clickable(onClick = onBack),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White, modifier = Modifier.size(28.dp))
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
            ProfileStat(value = following.toString(), label = "đã follow", colors = colors, modifier = Modifier.weight(1f))
            ProfileStat(value = followers.toString(), label = "Follower", colors = colors, modifier = Modifier.weight(1f))
            ProfileStat(value = likes.toString(), label = "thích", colors = colors, modifier = Modifier.weight(1f))
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
            text = "Bạn chưa tạo bài viết nào.",
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
                Text("${post.likes} thích", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
                Text("${post.comments} bình luận", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
                Text("${post.shareCount} chia sẻ", color = colors.mutedText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AccountHeaderPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF31C77B))
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
