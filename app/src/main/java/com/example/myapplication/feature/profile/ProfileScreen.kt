package com.example.myapplication.feature.profile

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.domain.model.UserProfile

@Composable
fun ProfileScreen(
    user: UserProfile,
    authState: AuthUiState,
    unreadSupport: Int,
    onOpenSupport: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenPinSetup: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (authState.isSignedIn) {
        SignedInProfileScreen(
            authUser = authState.user,
            onOpenSupport = onOpenSupport,
            onOpenAccountInfo = onOpenAccountInfo,
            onOpenPinSetup = onOpenPinSetup,
            onOpenNotificationSettings = onOpenNotificationSettings,
            onSignOut = onSignOut,
            modifier = modifier
        )
        return
    }

    val darkBackground = Color(0xFF232323)
    val panelBackground = Color(0xFF3A3940)
    val mutedText = Color(0xFF9B99A2)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
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
                text = "Dang nhap/Dang ky",
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
                SectionTitle(icon = Icons.Default.Settings, title = "Cai dat ung dung")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = panelBackground
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thay doi ngon ngu",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = Color(0xFF5A5961)
                        ) {
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
                                Text("Vie", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
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
                    SectionTitle(icon = Icons.Default.Help, title = "Trung tam tro giup")
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mo trung tam tro giup",
                        tint = mutedText,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Text(
                text = "Phien ban 3.1.41(30388)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 28.dp),
                color = mutedText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SignedInProfileScreen(
    authUser: AuthUser?,
    onOpenSupport: () -> Unit,
    onOpenAccountInfo: () -> Unit,
    onOpenPinSetup: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkBackground = Color(0xFF232323)
    val panelBackground = Color(0xFF3A3940)
    val mutedText = Color(0xFF9B99A2)
    val displayName = authUser?.displayName.orEmpty()
    val initial = displayName.firstOrNull()
        ?: authUser?.email?.firstOrNull()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
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
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                        .size(avatarSize),
                    shape = CircleShape,
                    color = Color(0xFF078E81)
                ) {
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

            if (displayName.isNotBlank()) {
                Text(
                    text = displayName,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = if (compactHeight) 22.dp else 32.dp),
                verticalArrangement = Arrangement.spacedBy(if (compactHeight) 16.dp else 22.dp)
            ) {
                SectionTitle(icon = Icons.Default.Person, title = "Cai dat tai khoan")
                Surface(shape = RoundedCornerShape(18.dp), color = panelBackground) {
                    Column {
                        AccountRow("Thong tin tai khoan", onClick = onOpenAccountInfo)
                        AccountDivider()
                        AccountRow("Thiet lap ma PIN", onClick = onOpenPinSetup)
                        AccountDivider()
                        AccountRow("Cai dat thong bao", onClick = onOpenNotificationSettings)
                    }
                }

                ProfileNavRow(icon = Icons.Default.Help, title = "Trung tam tro giup", onClick = onOpenSupport, mutedText = mutedText)
                ProfileNavRow(icon = Icons.Default.Logout, title = "Dang xuat", onClick = onSignOut, mutedText = mutedText)
            }

            Text(
                text = "Phien ban 3.1.41(30388)",
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 28.dp),
                color = mutedText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
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
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun AccountRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun AccountDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF5B5961))
    )
}

@Composable
private fun ProfileNavRow(
    icon: ImageVector,
    title: String,
    mutedText: Color,
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
        SectionTitle(icon = icon, title = title)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = mutedText,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun LanguageBadge() {
    Surface(shape = RoundedCornerShape(28.dp), color = Color(0xFF5A5961)) {
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
            Text("Vie", color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
