package com.example.myapplication.feature.community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftLine
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.util.AppStrings
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost

@Composable
fun FirestoreCommentsDialog(
    post: CommunityPost,
    comments: List<CommunityComment>,
    likeCount: Int,
    shareCount: Int,
    authorName: String,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }
    var dragDistance by remember { mutableStateOf(0f) }

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
                    .padding(top = 10.dp, bottom = 6.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = { dragDistance = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                dragDistance += dragAmount
                                if (dragDistance > 90f) onDismiss()
                            },
                            onDragEnd = { dragDistance = 0f },
                            onDragCancel = { dragDistance = 0f }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.width(76.dp).height(6.dp),
                    shape = RoundedCornerShape(99.dp),
                    color = SoftLine
                ) {}
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(AppStrings.Community.FAVORITE.format(likeCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(AppStrings.Community.SHARES_COUNT.format(shareCount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 18.dp, vertical = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Text(AppStrings.Community.NO_COMMENTS, color = SoftText, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    items(comments) { comment ->
                        FirestoreCommentBubble(comment = comment)
                    }
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(AppStrings.Community.WRITE_COMMENT_PLACEHOLDER.format(authorName), color = SoftText, style = MaterialTheme.typography.bodyLarge) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F5F7),
                        unfocusedContainerColor = Color(0xFFF3F5F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                TextButton(
                    enabled = draft.isNotBlank(),
                    onClick = {
                        val text = draft
                        draft = ""
                        onAddComment(text)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Evergreen,
                        disabledContentColor = SoftText
                    )
                ) {
                    Text(AppStrings.Community.SEND, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun FirestoreCommentBubble(comment: CommunityComment) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CircleAvatar(size = 46.dp, imageUrl = comment.authorAvatarUrl)
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF0F3F6)) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(comment.authorName, fontWeight = FontWeight.Bold)
                if (comment.text.isNotBlank()) {
                    ExpandableText(text = comment.text, style = MaterialTheme.typography.bodyLarge, textLimit = 50)
                }
                comment.mediaItems.firstOrNull()?.let { media ->
                    Text(media.type, color = SoftText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
