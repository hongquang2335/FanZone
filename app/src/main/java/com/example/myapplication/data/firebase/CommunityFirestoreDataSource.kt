package com.example.myapplication.data.firebase

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.SharedCommunityPost
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class CommunityFirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val postsCollection = firestore.collection(COMMUNITY_POSTS_COLLECTION)

    fun observeCommunityPosts(
        onPosts: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return postsCollection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val posts = snapshot
                    ?.documents
                    ?.mapNotNull(::toCommunityPost)
                    .orEmpty()
                onPosts(posts)
            }
    }

    fun createCommunityPost(
        authorId: String?,
        author: String,
        role: String,
        topic: String,
        content: String,
        eventId: String? = null,
        eventTitle: String? = null,
        imageUrl: String? = null,
        mediaUrl: String? = null,
        mediaType: String? = null,
        mediaItems: List<CommunityMediaItem> = emptyList()
    ): Task<DocumentReference> {
        val data = mutableMapOf<String, Any?>(
            FIELD_AUTHOR_ID to authorId,
            FIELD_AUTHOR to author,
            FIELD_ROLE to role,
            FIELD_TOPIC to topic,
            FIELD_CONTENT to content,
            FIELD_LIKES to 0,
            FIELD_COMMENTS to 0,
            FIELD_SHARE_COUNT to 0,
            FIELD_IMAGE_URL to imageUrl,
            FIELD_MEDIA_URL to mediaUrl,
            FIELD_MEDIA_TYPE to mediaType,
            FIELD_MEDIA_ITEMS to mediaItems.map { it.toFirestoreMap() },
            FIELD_EVENT_ID to eventId,
            FIELD_EVENT_TITLE to eventTitle,
            FIELD_CREATED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
        return postsCollection.add(data)
    }

    fun createSharedPost(
        originalPost: CommunityPost,
        shareAuthor: String,
        caption: String
    ): Task<DocumentReference> {
        val data = mutableMapOf<String, Any?>(
            FIELD_AUTHOR_ID to originalPost.authorId,
            FIELD_AUTHOR to originalPost.author,
            FIELD_ROLE to originalPost.role,
            FIELD_TOPIC to originalPost.topic,
            FIELD_CONTENT to originalPost.content,
            FIELD_LIKES to 0,
            FIELD_COMMENTS to 0,
            FIELD_SHARE_COUNT to 0,
            FIELD_IMAGE_URL to originalPost.imageUrl,
            FIELD_MEDIA_URL to originalPost.mediaUrl,
            FIELD_MEDIA_TYPE to originalPost.mediaType,
            FIELD_MEDIA_ITEMS to originalPost.mediaItems.map { it.toFirestoreMap() },
            FIELD_EVENT_ID to originalPost.eventId,
            FIELD_EVENT_TITLE to originalPost.eventTitle,
            FIELD_SHARED_POST to mapOf(
                FIELD_SHARED_AUTHOR to shareAuthor,
                FIELD_SHARED_CAPTION to caption
            ),
            FIELD_CREATED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
        return postsCollection.add(data)
    }

    fun upsertCommunityPost(post: CommunityPost): Task<Void> {
        return postsCollection
            .document(post.id)
            .set(post.toFirestoreMap(), SetOptions.merge())
    }

    fun checkCommunityStorage(
        onResult: (CommunityStorageCheck) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        postsCollection.get()
            .addOnSuccessListener { snapshot ->
                val documentChecks = snapshot.documents.map { document ->
                    val data = document.data.orEmpty()
                    CommunityDocumentCheck(
                        id = document.id,
                        missingRequiredFields = requiredFields.filter { field -> data[field] == null },
                        missingRecommendedFields = recommendedFields.filter { field -> data[field] == null },
                        hasValidSharedPost = hasValidSharedPost(data)
                    )
                }
                onResult(
                    CommunityStorageCheck(
                        collection = COMMUNITY_POSTS_COLLECTION,
                        totalDocuments = snapshot.size(),
                        documents = documentChecks
                    )
                )
            }
            .addOnFailureListener(onError)
    }

    private fun toCommunityPost(document: DocumentSnapshot): CommunityPost? {
        val author = document.getString(FIELD_AUTHOR) ?: return null
        val role = document.getString(FIELD_ROLE) ?: return null
        val topic = document.getString(FIELD_TOPIC) ?: return null
        val content = document.getString(FIELD_CONTENT) ?: return null

        return CommunityPost(
            id = document.id,
            authorId = document.getString(FIELD_AUTHOR_ID),
            author = author,
            role = role,
            topic = topic,
            content = content,
            likes = document.getLong(FIELD_LIKES)?.toInt() ?: 0,
            comments = document.getLong(FIELD_COMMENTS)?.toInt() ?: 0,
            shareCount = document.getLong(FIELD_SHARE_COUNT)?.toInt() ?: 0,
            imageRes = null,
            imageUrl = document.getString(FIELD_IMAGE_URL),
            mediaUrl = document.getString(FIELD_MEDIA_URL),
            mediaType = document.getString(FIELD_MEDIA_TYPE),
            mediaItems = document.get(FIELD_MEDIA_ITEMS)?.let(::toMediaItems).orEmpty(),
            eventId = document.getString(FIELD_EVENT_ID),
            eventTitle = document.getString(FIELD_EVENT_TITLE),
            sharedPost = document.get(FIELD_SHARED_POST)?.let(::toSharedPost),
            createdAtMillis = document.getTimestamp(FIELD_CREATED_AT)?.toDate()?.time,
            updatedAtMillis = document.getTimestamp(FIELD_UPDATED_AT)?.toDate()?.time
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun toMediaItems(value: Any): List<CommunityMediaItem> {
        return (value as? List<Map<String, Any?>>)
            ?.mapNotNull { item ->
                CommunityMediaItem(
                    url = item[FIELD_MEDIA_ITEM_URL] as? String ?: return@mapNotNull null,
                    type = item[FIELD_MEDIA_ITEM_TYPE] as? String ?: "application/octet-stream"
                )
            }
            .orEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toSharedPost(value: Any): SharedCommunityPost? {
        val data = value as? Map<String, Any?> ?: return null
        return SharedCommunityPost(
            author = data[FIELD_SHARED_AUTHOR] as? String ?: return null,
            caption = data[FIELD_SHARED_CAPTION] as? String ?: ""
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun hasValidSharedPost(data: Map<String, Any?>): Boolean {
        val sharedPost = data[FIELD_SHARED_POST] as? Map<String, Any?> ?: return true
        return !sharedPost[FIELD_SHARED_AUTHOR].toString().isBlank() &&
            sharedPost[FIELD_SHARED_CAPTION] is String
    }

    companion object {
        const val COMMUNITY_POSTS_COLLECTION = "communityPosts"

        const val FIELD_AUTHOR_ID = "authorId"
        const val FIELD_AUTHOR = "author"
        const val FIELD_ROLE = "role"
        const val FIELD_TOPIC = "topic"
        const val FIELD_CONTENT = "content"
        const val FIELD_LIKES = "likes"
        const val FIELD_COMMENTS = "comments"
        const val FIELD_SHARE_COUNT = "shareCount"
        const val FIELD_IMAGE_URL = "imageUrl"
        const val FIELD_MEDIA_URL = "mediaUrl"
        const val FIELD_MEDIA_TYPE = "mediaType"
        const val FIELD_MEDIA_ITEMS = "mediaItems"
        const val FIELD_MEDIA_ITEM_URL = "url"
        const val FIELD_MEDIA_ITEM_TYPE = "type"
        const val FIELD_EVENT_ID = "eventId"
        const val FIELD_EVENT_TITLE = "eventTitle"
        const val FIELD_SHARED_POST = "sharedPost"
        const val FIELD_CREATED_AT = "createdAt"
        const val FIELD_UPDATED_AT = "updatedAt"
        const val FIELD_SHARED_AUTHOR = "author"
        const val FIELD_SHARED_CAPTION = "caption"

        val requiredFields = listOf(
            FIELD_AUTHOR_ID,
            FIELD_AUTHOR,
            FIELD_ROLE,
            FIELD_TOPIC,
            FIELD_CONTENT,
            FIELD_LIKES,
            FIELD_COMMENTS,
            FIELD_SHARE_COUNT,
            FIELD_CREATED_AT,
            FIELD_UPDATED_AT
        )

        val recommendedFields = listOf(
            FIELD_IMAGE_URL,
            FIELD_MEDIA_URL,
            FIELD_MEDIA_TYPE,
            FIELD_MEDIA_ITEMS,
            FIELD_EVENT_ID,
            FIELD_EVENT_TITLE
        )
    }
}

private fun CommunityPost.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        CommunityFirestoreDataSource.FIELD_AUTHOR_ID to authorId,
        CommunityFirestoreDataSource.FIELD_AUTHOR to author,
        CommunityFirestoreDataSource.FIELD_ROLE to role,
        CommunityFirestoreDataSource.FIELD_TOPIC to topic,
        CommunityFirestoreDataSource.FIELD_CONTENT to content,
        CommunityFirestoreDataSource.FIELD_LIKES to likes,
        CommunityFirestoreDataSource.FIELD_COMMENTS to comments,
        CommunityFirestoreDataSource.FIELD_SHARE_COUNT to shareCount,
        CommunityFirestoreDataSource.FIELD_IMAGE_URL to imageUrl,
        CommunityFirestoreDataSource.FIELD_MEDIA_URL to mediaUrl,
        CommunityFirestoreDataSource.FIELD_MEDIA_TYPE to mediaType,
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEMS to mediaItems.map { it.toFirestoreMap() },
        CommunityFirestoreDataSource.FIELD_EVENT_ID to eventId,
        CommunityFirestoreDataSource.FIELD_EVENT_TITLE to eventTitle,
        CommunityFirestoreDataSource.FIELD_SHARED_POST to sharedPost?.let { share ->
            mapOf(
                CommunityFirestoreDataSource.FIELD_SHARED_AUTHOR to share.author,
                CommunityFirestoreDataSource.FIELD_SHARED_CAPTION to share.caption
            )
        },
        CommunityFirestoreDataSource.FIELD_CREATED_AT to FieldValue.serverTimestamp(),
        CommunityFirestoreDataSource.FIELD_UPDATED_AT to FieldValue.serverTimestamp()
    )
}

private fun CommunityMediaItem.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_URL to url,
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_TYPE to type
    )
}

data class CommunityStorageCheck(
    val collection: String,
    val totalDocuments: Int,
    val documents: List<CommunityDocumentCheck>
) {
    val hasRequiredFields: Boolean
        get() = documents.all { it.missingRequiredFields.isEmpty() && it.hasValidSharedPost }
}

data class CommunityDocumentCheck(
    val id: String,
    val missingRequiredFields: List<String>,
    val missingRecommendedFields: List<String>,
    val hasValidSharedPost: Boolean
)
