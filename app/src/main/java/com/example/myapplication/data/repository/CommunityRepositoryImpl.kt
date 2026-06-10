package com.example.myapplication.data.repository

import com.example.myapplication.data.firebase.CommunityFirestoreDataSource
import com.example.myapplication.data.firebase.CommunityStorageDataSource
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.repository.CommunityPostSubscription
import com.example.myapplication.domain.repository.CommunityRepository
import com.example.myapplication.domain.repository.CreateCommunityCommentRequest
import com.example.myapplication.domain.repository.CreateCommunityPostRequest

class CommunityRepositoryImpl(
    private val firestoreDataSource: CommunityFirestoreDataSource,
    private val storageDataSource: CommunityStorageDataSource
) : CommunityRepository {
    override fun observeCommunityPosts(
        onPosts: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription {
        val registration = firestoreDataSource.observeCommunityPosts(
            onPosts = onPosts,
            onError = onError
        )
        return CommunityPostSubscription { registration.remove() }
    }

    override fun createCommunityPost(
        request: CreateCommunityPostRequest,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (request.media.isEmpty()) {
            writePost(request = request, mediaItems = emptyList(), onSuccess = onSuccess, onError = onError)
            return
        }

        val uploadedItems = mutableListOf<CommunityMediaItem>()
        var finishedCount = 0
        var failed = false

        request.media.forEach { media ->
            storageDataSource.uploadCommunityMedia(
                mediaUri = media.uri,
                mediaType = media.type,
                onSuccess = { mediaUrl, mediaType ->
                    if (failed) return@uploadCommunityMedia
                    uploadedItems += CommunityMediaItem(url = mediaUrl, type = mediaType)
                    finishedCount++
                    if (finishedCount == request.media.size) {
                        writePost(
                            request = request,
                            mediaItems = uploadedItems.toList(),
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    }
                },
                onError = { throwable ->
                    if (failed) return@uploadCommunityMedia
                    failed = true
                    onError(throwable)
                }
            )
        }
    }

    private fun writePost(
        request: CreateCommunityPostRequest,
        mediaItems: List<CommunityMediaItem>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val firstMedia = mediaItems.firstOrNull()
        firestoreDataSource.createCommunityPost(
            authorId = request.authorId,
            author = request.author,
            authorAvatarUrl = request.authorAvatarUrl,
            role = request.role,
            topic = request.topic,
            content = request.content,
            eventId = request.eventId,
            eventTitle = request.eventTitle,
            imageUrl = firstMedia?.url?.takeIf { firstMedia.type.startsWith("image/") },
            mediaUrl = firstMedia?.url,
            mediaType = firstMedia?.type,
            mediaItems = mediaItems
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun shareCommunityPost(
        post: CommunityPost,
        shareAuthorId: String,
        author: String,
        authorAvatarUrl: String?,
        caption: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.createSharedPost(
            originalPost = post,
            shareAuthorId = shareAuthorId,
            shareAuthor = author,
            shareAuthorAvatarUrl = authorAvatarUrl,
            caption = caption
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun observeComments(
        postId: String,
        onComments: (List<com.example.myapplication.domain.model.CommunityComment>) -> Unit,
        onError: (Throwable) -> Unit
    ): CommunityPostSubscription {
        val registration = firestoreDataSource.observeComments(
            postId = postId,
            onComments = onComments,
            onError = onError
        )
        return CommunityPostSubscription { registration.remove() }
    }

    override fun addComment(
        request: CreateCommunityCommentRequest,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (request.media.isEmpty()) {
            writeComment(request, emptyList(), onSuccess, onError)
            return
        }

        val uploadedItems = mutableListOf<CommunityMediaItem>()
        var finishedCount = 0
        var failed = false
        request.media.forEach { media ->
            storageDataSource.uploadCommunityMedia(
                mediaUri = media.uri,
                mediaType = media.type,
                onSuccess = { mediaUrl, mediaType ->
                    if (failed) return@uploadCommunityMedia
                    uploadedItems += CommunityMediaItem(url = mediaUrl, type = mediaType)
                    finishedCount++
                    if (finishedCount == request.media.size) {
                        writeComment(request, uploadedItems.toList(), onSuccess, onError)
                    }
                },
                onError = { throwable ->
                    if (failed) return@uploadCommunityMedia
                    failed = true
                    onError(throwable)
                }
            )
        }
    }

    private fun writeComment(
        request: CreateCommunityCommentRequest,
        mediaItems: List<CommunityMediaItem>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val firstMedia = mediaItems.firstOrNull()
        firestoreDataSource.addComment(
            postId = request.postId,
            authorId = request.authorId,
            authorName = request.authorName,
            authorAvatarUrl = request.authorAvatarUrl,
            text = request.text,
            mediaUrl = firstMedia?.url,
            mediaType = firstMedia?.type,
            mediaItems = mediaItems
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun likeCommunityPost(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.likeCommunityPost(postId, userId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun unlikeCommunityPost(
        postId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.unlikeCommunityPost(postId, userId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun updatePost(
        postId: String,
        text: String,
        mediaItems: List<CommunityMediaItem>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.updatePost(postId, text, mediaItems)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }

    override fun deletePost(
        postId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firestoreDataSource.deletePost(postId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onError)
    }
}
