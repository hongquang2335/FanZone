package com.example.myapplication.domain.repository

import android.net.Uri
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.CommunityPost

interface CommunityRepository {
    fun observeCommunityPosts(
        onPosts: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription

    fun createCommunityPost(
        request: CreateCommunityPostRequest,
        onSuccess: (String) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun shareCommunityPost(
        post: CommunityPost,
        shareAuthorId: String,
        author: String,
        authorAvatarUrl: String?,
        caption: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun updatePost(
        postId: String,
        text: String,
        mediaItems: List<CommunityMediaItem>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun deletePost(
        postId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun observeComments(
        postId: String,
        onComments: (List<CommunityComment>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription

    fun addComment(
        request: CreateCommunityCommentRequest,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun likeCommunityPost(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )

    fun unlikeCommunityPost(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    )
}

fun interface CommunityPostSubscription {
    fun dispose()
}

data class CreateCommunityPostRequest(
    val authorId: String?,
    val author: String,
    val authorAvatarUrl: String? = null,
    val anonymous: Boolean,
    val content: String,
    val eventId: String? = null,
    val eventTitle: String? = null,
    val media: List<SelectedCommunityMedia> = emptyList()
) {
    val role: String
        get() = if (anonymous) "An danh" else "Thanh vien cong dong"

    val topic: String
        get() = if (eventTitle != null) "Bai viet su kien" else "Bai viet cong dong"
}

data class SelectedCommunityMedia(
    val uri: Uri,
    val name: String,
    val type: String
)

data class CreateCommunityCommentRequest(
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val text: String,
    val media: List<SelectedCommunityMedia> = emptyList()
)
