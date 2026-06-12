package com.example.myapplication.feature.community

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AssistChip
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.LoginRequiredDialog
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.util.AppStrings
import com.example.myapplication.domain.repository.SelectedCommunityMedia

@Composable
fun ComposerCard(
    eventId: String? = null,
    eventTitle: String? = null,
    currentAuthorAvatarUrl: String? = null,
    onOpenAuth: () -> Unit = {},
    viewModel: CommunityPostViewModel = viewModel()
) {
    var composerOpen by remember { mutableStateOf(false) }
    var showAuthPrompt by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircleAvatar(
                size = 44.dp,
                imageUrl = currentAuthorAvatarUrl ?: state.currentAuthorAvatarUrl
            )
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF3F5F7),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftLine)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (state.currentAuthorId == null) {
                                showAuthPrompt = true
                            } else {
                                composerOpen = true
                            }
                        }
                ) {
                    Text(
                        text = AppStrings.Community.PLACEHOLDER,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        color = SoftText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    if (composerOpen) {
        NewPostDialog(
            state = state,
            currentAuthorAvatarUrl = currentAuthorAvatarUrl,
            onDraftChange = viewModel::updateDraft,
            onMediaSelected = viewModel::addMedia,
            onMediaRemoved = viewModel::removeMedia,
            onToggleAnonymous = viewModel::toggleAnonymous,
            onToggleFeeling = viewModel::toggleFeeling,
            onDismiss = { composerOpen = false },
            onPost = {
                viewModel.createPost(eventId = eventId, eventTitle = eventTitle) {
                    composerOpen = false
                }
            }
        )
    }

    if (showAuthPrompt) {
        LoginRequiredDialog(
            onDismiss = { showAuthPrompt = false },
            onLogin = onOpenAuth,
            subtitleText = AppStrings.Community.AUTH_REQUIRED_DESC
        )
    }
}

@Composable
private fun NewPostDialog(
    state: CommunityPostUiState,
    currentAuthorAvatarUrl: String?,
    onDraftChange: (String) -> Unit,
    onMediaSelected: (List<SelectedCommunityMedia>) -> Unit,
    onMediaRemoved: (SelectedCommunityMedia) -> Unit,
    onToggleAnonymous: () -> Unit,
    onToggleFeeling: () -> Unit,
    onDismiss: () -> Unit,
    onPost: () -> Unit
) {
    val context = LocalContext.current
    var pendingTypes by remember { mutableStateOf(emptyArray<String>()) }
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val selected = uris.map { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            SelectedCommunityMedia(
                uri = uri,
                name = uri.lastPathSegment?.substringAfterLast('/') ?: AppStrings.Community.SELECTED_FILE,
                type = context.contentResolver.getType(uri)
                    ?: pendingTypes.firstOrNull()
                    ?: "application/octet-stream"
            )
        }
        if (selected.isNotEmpty()) {
            onMediaSelected(selected)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = AppStrings.Community.CLOSE)
                }
                Text(
                    text = AppStrings.Community.NEW_POST,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                TextButton(
                    onClick = onPost,
                    enabled = state.canPost && !state.isPosting,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    if (state.isPosting) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Evergreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = AppStrings.Community.POST_ACTION,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.canPost) Evergreen else SoftText
                        )
                    }
                }
            }

            Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar(
                        size = 64.dp,
                        imageUrl = currentAuthorAvatarUrl ?: state.currentAuthorAvatarUrl
                    )
                    Text(state.currentAuthorName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        ComposerActionChip(Icons.Default.Image, AppStrings.Community.ADD_IMAGE) {
                            pendingTypes = arrayOf("image/*")
                            mediaLauncher.launch(pendingTypes)
                        }
                    }
                    item {
                        ComposerActionChip(Icons.Default.Videocam, AppStrings.Community.ADD_VIDEO) {
                            pendingTypes = arrayOf("video/*")
                            mediaLauncher.launch(pendingTypes)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    BasicTextField(
                        value = state.draft,
                        onValueChange = onDraftChange,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color.Black)
                    )
                    if (state.draft.isBlank()) {
                        Text(
                            text = AppStrings.Community.PLACEHOLDER,
                            color = SoftText,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                if (state.selectedMedia.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.selectedMedia) { media ->
                            MediaThumbnailItem(media = media, onRemove = { onMediaRemoved(media) })
                        }
                    }
                }
            }

            Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SelectedMediaRow(
    media: SelectedCommunityMedia,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF3F5F7),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftLine)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    media.type.startsWith("image/") -> Icons.Default.Image
                    media.type.startsWith("video/") -> Icons.Default.Videocam
                    else -> Icons.Default.Image
                },
                contentDescription = null,
                tint = Evergreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = media.name,
                modifier = Modifier.weight(1f),
                color = SoftText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = AppStrings.Community.DELETE_FILE, tint = SoftText)
            }
        }
    }
}

@Composable
private fun MediaThumbnailItem(
    media: SelectedCommunityMedia,
    onRemove: () -> Unit
) {
    val isVideo = media.type.startsWith("video/")
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = media.uri,
            contentDescription = media.name,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = AppStrings.Community.DELETE_FILE,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ComposerActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    AssistChip(
        onClick = onClick,
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
        label = { Text(label) }
    )
}
