package com.example.myapplication.feature.community

import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.domain.repository.SelectedCommunityMedia

@Composable
fun ComposerCard(
    eventId: String? = null,
    eventTitle: String? = null,
    viewModel: CommunityPostViewModel = viewModel()
) {
    var composerOpen by remember { mutableStateOf(false) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircleAvatar(size = 44.dp)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { composerOpen = true },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF3F5F7),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftLine)
            ) {
                Text(
                    text = "Ban dang nghi gi?",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    color = SoftText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                Icons.Default.Image,
                contentDescription = "Them anh",
                tint = Evergreen,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { composerOpen = true }
            )
        }
    }

    if (composerOpen) {
        NewPostDialog(
            state = state,
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
}

@Composable
private fun NewPostDialog(
    state: CommunityPostUiState,
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
                name = uri.lastPathSegment?.substringAfterLast('/') ?: "Tep da chon",
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
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                androidx.compose.material3.IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Dong")
                }
                Text(
                    text = "Bai viet moi",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
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
                    CircleAvatar(size = 64.dp)
                    Text("Hong Quang", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        ComposerActionChip(Icons.Default.Image, "Anh") {
                            pendingTypes = arrayOf("image/*")
                            mediaLauncher.launch(pendingTypes)
                        }
                    }
                    item {
                        ComposerActionChip(Icons.Default.Videocam, "Video") {
                            pendingTypes = arrayOf("video/*")
                            mediaLauncher.launch(pendingTypes)
                        }
                    }
                    item {
                        ComposerActionChip(Icons.Default.Audiotrack, "Ghi am") {
                            pendingTypes = arrayOf("audio/*")
                            mediaLauncher.launch(pendingTypes)
                        }
                    }
                }

                if (state.selectedMedia.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.selectedMedia.forEach { media ->
                            SelectedMediaRow(media = media, onRemove = { onMediaRemoved(media) })
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
                            text = "Ban dang nghi gi?",
                            color = SoftText,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Surface(color = SoftLine, modifier = Modifier.fillMaxWidth().height(1.dp)) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPost,
                    enabled = state.canPost,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (state.isPosting) "Dang dang..." else "Dang")
                }
            }
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
                    else -> Icons.Default.Audiotrack
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
                Icon(Icons.Default.Delete, contentDescription = "Xoa tep", tint = SoftText)
            }
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
