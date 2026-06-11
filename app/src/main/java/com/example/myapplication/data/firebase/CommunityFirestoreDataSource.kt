package com.example.myapplication.data.firebase

import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityMediaItem
import com.example.myapplication.domain.model.SharedCommunityPost
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
    private val postsCollection = firestore.collection(POSTS_COLLECTION)
    private val usersCollection = firestore.collection(USERS_COLLECTION)

    fun observeCommunityPosts(
        onPosts: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        android.util.Log.d("FirestoreDataSource", "observeCommunityPosts: Bắt đầu đăng ký snapshot listener cho posts...")

        // Fetch once immediately to populate UI in case snapshot listener is blocked/delayed
        postsCollection.orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    android.util.Log.d("FirestoreDataSource", "observeCommunityPosts: GET ban đầu thành công với ${snapshot.size()} posts")
                    val posts = mutableListOf<CommunityPost>()
                    for (doc in snapshot.documents) {
                        val post = toCommunityPost(doc)
                        if (post != null) {
                            posts.add(post)
                        } else {
                            android.util.Log.w("FirestoreDataSource", "observeCommunityPosts (GET): Parse thất bại cho document ID=${doc.id}")
                        }
                    }
                    android.util.Log.d("FirestoreDataSource", "observeCommunityPosts (GET): Gọi hydrateOriginalPosts cho ${posts.size} posts")
                    hydrateOriginalPosts(posts, { hydratedPosts ->
                        hydratePostAuthorAvatars(hydratedPosts, onPosts, onError)
                    }, onError)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreDataSource", "observeCommunityPosts: GET ban đầu thất bại: ${e.message}", e)
            }

        return postsCollection
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirestoreDataSource", "observeCommunityPosts: Lỗi snapshot listener: ${error.message}", error)
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    android.util.Log.w("FirestoreDataSource", "observeCommunityPosts: Snapshot nhận được là null")
                    onPosts(emptyList())
                    return@addSnapshotListener
                }

                android.util.Log.d("FirestoreDataSource", "observeCommunityPosts: Đã nhận snapshot với ${snapshot.size()} documents")
                val posts = mutableListOf<CommunityPost>()
                for (doc in snapshot.documents) {
                    val post = toCommunityPost(doc)
                    if (post != null) {
                        posts.add(post)
                    } else {
                        android.util.Log.w("FirestoreDataSource", "observeCommunityPosts: Parse thất bại cho document ID=${doc.id}. Data: ${doc.data}")
                    }
                }
                android.util.Log.d("FirestoreDataSource", "observeCommunityPosts: Parse thành công ${posts.size} / ${snapshot.size()} posts")
                hydrateOriginalPosts(posts, { hydratedPosts ->
                    hydratePostAuthorAvatars(hydratedPosts, onPosts, onError)
                }, onError)
            }
    }

    fun createCommunityPost(
        authorId: String?,
        author: String,
        authorAvatarUrl: String?,
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
            FIELD_TYPE to TYPE_POST,
            FIELD_AUTHOR to author,
            FIELD_AUTHOR_AVATAR_URL to authorAvatarUrl,
            FIELD_ROLE to role,
            FIELD_TOPIC to topic,
            FIELD_CONTENT to content,
            FIELD_LIKES to 0,
            FIELD_LIKED_BY to emptyList<String>(),
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
        shareAuthorId: String,
        shareAuthor: String,
        shareAuthorAvatarUrl: String?,
        caption: String
    ): Task<Void> {
        val originalPostId = originalPost.originalPostId ?: originalPost.id
        val shareDocument = postsCollection.document()
        val shareRecordDocument = postsCollection
            .document(originalPostId)
            .collection(SHARES_COLLECTION)
            .document(shareDocument.id)
        val originalSnapshot = originalPost.sharedPost ?: originalPost.toOriginalShareSnapshot(originalPostId)
        val data = mutableMapOf<String, Any?>(
            FIELD_TYPE to TYPE_SHARE,
            FIELD_AUTHOR_ID to shareAuthorId,
            FIELD_AUTHOR to shareAuthor,
            FIELD_AUTHOR_AVATAR_URL to shareAuthorAvatarUrl,
            FIELD_ROLE to "Thanh vien cong dong",
            FIELD_TOPIC to "Bai viet chia se",
            FIELD_CONTENT to caption,
            FIELD_LIKES to 0,
            FIELD_LIKED_BY to emptyList<String>(),
            FIELD_COMMENTS to 0,
            FIELD_SHARE_COUNT to 0,
            FIELD_IMAGE_URL to null,
            FIELD_MEDIA_URL to null,
            FIELD_MEDIA_TYPE to null,
            FIELD_MEDIA_ITEMS to emptyList<Map<String, Any?>>(),
            FIELD_EVENT_ID to originalSnapshot.eventId,
            FIELD_EVENT_TITLE to originalSnapshot.eventTitle,
            FIELD_ORIGINAL_POST_ID to originalPostId,
            FIELD_RESHARED_FROM_POST_ID to originalPost.id.takeIf { it != originalPostId },
            FIELD_SHARED_POST to originalSnapshot.toFirestoreMap(),
            FIELD_CREATED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
        val shareRecord = mapOf(
            FIELD_SHARE_ID to shareDocument.id,
            FIELD_POST_ID to originalPostId,
            FIELD_AUTHOR_ID to shareAuthorId,
            FIELD_AUTHOR to shareAuthor,
            FIELD_AUTHOR_AVATAR_URL to shareAuthorAvatarUrl,
            FIELD_SHARED_CAPTION to caption,
            FIELD_RESHARED_FROM_POST_ID to originalPost.id.takeIf { it != originalPostId },
            FIELD_CREATED_AT to FieldValue.serverTimestamp()
        )
        return firestore.runBatch { batch ->
            batch.set(shareDocument, data)
            batch.set(shareRecordDocument, shareRecord)
            batch.update(postsCollection.document(originalPostId), FIELD_SHARE_COUNT, FieldValue.increment(1))
        }
    }

    fun observeComments(
        postId: String,
        onComments: (List<CommunityComment>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        android.util.Log.d("FirestoreDataSource", "observeComments: Bắt đầu đăng ký snapshot listener cho comments, postId=$postId...")

        // Fetch once immediately to populate UI in case snapshot listener is blocked/delayed
        postsCollection.document(postId).collection(COMMENTS_COLLECTION)
            .orderBy(FIELD_CREATED_AT, Query.Direction.ASCENDING).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    android.util.Log.d("FirestoreDataSource", "observeComments: GET ban đầu thành công với ${snapshot.size()} comments")
                    val comments = snapshot.documents.mapNotNull { it.toCommunityComment(postId) }
                    hydrateCommentAuthorAvatars(comments, onComments, onError)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FirestoreDataSource", "observeComments: GET ban đầu thất bại: ${e.message}", e)
            }

        return postsCollection
            .document(postId)
            .collection(COMMENTS_COLLECTION)
            .orderBy(FIELD_CREATED_AT, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirestoreDataSource", "observeComments: Lỗi snapshot listener: ${error.message}", error)
                    onError(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { it.toCommunityComment(postId) }.orEmpty()
                android.util.Log.d("FirestoreDataSource", "observeComments: Nhận snapshot với ${comments.size} comments")
                hydrateCommentAuthorAvatars(comments, onComments, onError)
            }
    }

    fun addComment(
        postId: String,
        authorId: String,
        authorName: String,
        authorAvatarUrl: String?,
        text: String,
        mediaUrl: String? = null,
        mediaType: String? = null,
        mediaItems: List<CommunityMediaItem> = emptyList()
    ): Task<Void> {
        val commentDocument = postsCollection.document(postId).collection(COMMENTS_COLLECTION).document()
        val data = mapOf(
            FIELD_COMMENT_ID to commentDocument.id,
            FIELD_POST_ID to postId,
            FIELD_AUTHOR_ID to authorId,
            FIELD_AUTHOR to authorName,
            FIELD_AUTHOR_AVATAR_URL to authorAvatarUrl,
            FIELD_TEXT to text,
            FIELD_MEDIA_URL to mediaUrl,
            FIELD_MEDIA_TYPE to mediaType,
            FIELD_MEDIA_ITEMS to mediaItems.map { it.toFirestoreMap() },
            FIELD_LIKES to 0,
            FIELD_LIKED_BY to emptyList<String>(),
            FIELD_CREATED_AT to FieldValue.serverTimestamp(),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp()
        )
        return firestore.runBatch { batch ->
            batch.set(commentDocument, data)
            batch.update(postsCollection.document(postId), FIELD_COMMENTS, FieldValue.increment(1))
        }
    }

    fun upsertCommunityPost(post: CommunityPost): Task<Void> {
        return postsCollection
            .document(post.id)
            .set(post.toFirestoreMap(), SetOptions.merge())
    }

    fun likeCommunityPost(postId: String, userId: String): Task<Void> {
        return postsCollection
            .document(postId)
            .update(
                mapOf(
                    FIELD_LIKED_BY to FieldValue.arrayUnion(userId),
                    FIELD_LIKES to FieldValue.increment(1)
                )
            )
    }

    fun unlikeCommunityPost(postId: String, userId: String): Task<Void> {
        return postsCollection
            .document(postId)
            .update(
                mapOf(
                    FIELD_LIKED_BY to FieldValue.arrayRemove(userId),
                    FIELD_LIKES to FieldValue.increment(-1)
                )
            )
    }

    fun updatePost(postId: String, text: String, mediaItems: List<com.example.myapplication.domain.model.CommunityMediaItem>): Task<Void> {
        return postsCollection.document(postId).update(
            mapOf(
                FIELD_CONTENT to text,
                "mediaItems" to mediaItems.map { mapOf("url" to it.url, "type" to it.type) },
                FIELD_IMAGE_URL to null,
                FIELD_MEDIA_URL to null,
                FIELD_MEDIA_TYPE to null,
                FIELD_UPDATED_AT to FieldValue.serverTimestamp()
            )
        )
    }

    fun deletePost(postId: String): Task<Void> {
        return postsCollection.document(postId).delete()
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
                        collection = POSTS_COLLECTION,
                        totalDocuments = snapshot.size(),
                        documents = documentChecks
                    )
                )
            }
            .addOnFailureListener(onError)
    }

    private fun toCommunityPost(document: DocumentSnapshot): CommunityPost? {
        val author = document.getString(FIELD_AUTHOR)
        val role = document.getString(FIELD_ROLE)
        val topic = document.getString(FIELD_TOPIC)
        val content = document.getString(FIELD_CONTENT)

        if (author == null || role == null || topic == null || content == null) {
            android.util.Log.w(
                "FirestoreDataSource",
                "toCommunityPost: Gặp trường null trong document ID=${document.id}. author=$author, role=$role, topic=$topic, content=$content"
            )
            return null
        }

        return CommunityPost(
            id = document.id,
            type = document.getString(FIELD_TYPE) ?: TYPE_POST,
            authorId = document.getString(FIELD_AUTHOR_ID),
            author = author,
            authorAvatarUrl = document.getString(FIELD_AUTHOR_AVATAR_URL),
            role = role,
            topic = topic,
            content = content,
            likes = document.getLong(FIELD_LIKES)?.toInt() ?: 0,
            likedBy = (document.get(FIELD_LIKED_BY) as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
            comments = document.getLong(FIELD_COMMENTS)?.toInt() ?: 0,
            shareCount = document.getLong(FIELD_SHARE_COUNT)?.toInt() ?: 0,
            imageRes = null,
            imageUrl = document.getString(FIELD_IMAGE_URL),
            mediaUrl = document.getString(FIELD_MEDIA_URL),
            mediaType = document.getString(FIELD_MEDIA_TYPE),
            mediaItems = document.get(FIELD_MEDIA_ITEMS)?.let(::toMediaItems).orEmpty(),
            eventId = document.getString(FIELD_EVENT_ID),
            eventTitle = document.getString(FIELD_EVENT_TITLE),
            originalPostId = document.getString(FIELD_ORIGINAL_POST_ID),
            resharedFromPostId = document.getString(FIELD_RESHARED_FROM_POST_ID),
            sharedPost = document.get(FIELD_SHARED_POST)?.let(::toSharedPost),
            createdAtMillis = parseCreatedAt(document.get(FIELD_CREATED_AT)),
            updatedAtMillis = parseCreatedAt(document.get(FIELD_UPDATED_AT))
        )
    }

    private fun hydratePostAuthorAvatars(
        posts: List<CommunityPost>,
        onPosts: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val authorIds = posts
            .flatMap { post -> listOfNotNull(post.authorId, post.sharedPost?.authorId) }
            .distinct()

        if (authorIds.isEmpty()) {
            onPosts(posts.map { post -> post.copy(authorAvatarUrl = null) })
            return
        }

        val tasks = authorIds.map { authorId -> usersCollection.document(authorId).get() }
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener { _ ->
                val avatars = mutableMapOf<String, String?>()
                val displayNames = mutableMapOf<String, String?>()
                tasks.forEach { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            avatars[document.id] = document.getString(FIELD_USER_AVATAR_URL)?.takeIf { it.isNotBlank() }
                            displayNames[document.id] = document.getString("displayName")?.takeIf { it.isNotBlank() }
                        }
                    } else {
                        android.util.Log.e("FirestoreDataSource", "Failed to fetch user profile for post author: ${task.exception?.message}", task.exception)
                    }
                }
                onPosts(
                    posts.map { post ->
                        post.copy(
                            author = post.authorId?.let { authorId -> displayNames[authorId] } ?: post.author,
                            authorAvatarUrl = post.authorId?.let { authorId -> avatars[authorId] } ?: post.authorAvatarUrl,
                            sharedPost = post.sharedPost?.let { share ->
                                share.copy(
                                    author = share.authorId?.let { authorId -> displayNames[authorId] } ?: share.author,
                                    authorAvatarUrl = share.authorId?.let { authorId -> avatars[authorId] } ?: share.authorAvatarUrl
                                )
                            }
                        )
                    }
                )
            }
    }

    private fun hydrateOriginalPosts(
        posts: List<CommunityPost>,
        onComplete: (List<CommunityPost>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val sharePosts = posts.filter { it.originalPostId != null }
        val originalPostIds = sharePosts.mapNotNull { it.originalPostId }.distinct()

        if (originalPostIds.isEmpty()) {
            onComplete(posts)
            return
        }

        val tasks = originalPostIds.map { id -> postsCollection.document(id).get() }
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener { _ ->
                val originalPostsMap = mutableMapOf<String, CommunityPost?>()
                tasks.forEachIndexed { index, task ->
                    val id = originalPostIds[index]
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            originalPostsMap[id] = toCommunityPost(document)
                        } else {
                            originalPostsMap[id] = null
                        }
                    } else {
                        originalPostsMap[id] = null
                    }
                }

                val hydratedPosts = posts.map { post ->
                    if (post.originalPostId != null) {
                        val originalPost = originalPostsMap[post.originalPostId]
                        if (originalPost != null) {
                            val updatedSharedPost = (post.sharedPost ?: SharedCommunityPost(author = originalPost.author)).copy(
                                authorId = originalPost.authorId,
                                author = originalPost.author,
                                content = originalPost.content,
                                mediaItems = originalPost.mediaItems,
                                eventId = originalPost.eventId,
                                eventTitle = originalPost.eventTitle,
                                createdAtMillis = originalPost.createdAtMillis,
                                updatedAtMillis = originalPost.updatedAtMillis,
                                isDeleted = false
                            )
                            post.copy(sharedPost = updatedSharedPost)
                        } else {
                            val updatedSharedPost = (post.sharedPost ?: SharedCommunityPost(author = "")).copy(
                                isDeleted = true
                            )
                            post.copy(sharedPost = updatedSharedPost)
                        }
                    } else {
                        post
                    }
                }
                onComplete(hydratedPosts)
            }
    }

    private fun hydrateCommentAuthorAvatars(
        comments: List<CommunityComment>,
        onComments: (List<CommunityComment>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val authorIds = comments.map { it.authorId }.distinct()
        if (authorIds.isEmpty()) {
            onComments(comments)
            return
        }

        val tasks = authorIds.map { authorId -> usersCollection.document(authorId).get() }
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener { _ ->
                val avatars = mutableMapOf<String, String?>()
                val displayNames = mutableMapOf<String, String?>()
                tasks.forEach { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            avatars[document.id] = document.getString(FIELD_USER_AVATAR_URL)?.takeIf { it.isNotBlank() }
                            displayNames[document.id] = document.getString("displayName")?.takeIf { it.isNotBlank() }
                        }
                    } else {
                        android.util.Log.e("FirestoreDataSource", "Failed to fetch user profile for comment author: ${task.exception?.message}", task.exception)
                    }
                }
                onComments(
                    comments.map { comment ->
                        comment.copy(
                            authorName = displayNames[comment.authorId] ?: comment.authorName,
                            authorAvatarUrl = avatars[comment.authorId] ?: comment.authorAvatarUrl
                        )
                    }
                )
            }
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
        val rawCreatedAt = data[FIELD_CREATED_AT]
        val createdAtMillis = parseCreatedAt(rawCreatedAt)
        val rawUpdatedAt = data[FIELD_UPDATED_AT]
        val updatedAtMillis = parseCreatedAt(rawUpdatedAt)
        return SharedCommunityPost(
            postId = data[FIELD_POST_ID] as? String,
            authorId = data[FIELD_AUTHOR_ID] as? String,
            author = data[FIELD_SHARED_AUTHOR] as? String
                ?: data[FIELD_AUTHOR] as? String
                ?: return null,
            authorAvatarUrl = data[FIELD_AUTHOR_AVATAR_URL] as? String,
            content = data[FIELD_CONTENT] as? String ?: "",
            mediaItems = data[FIELD_MEDIA_ITEMS]?.let(::toMediaItems).orEmpty(),
            eventId = data[FIELD_EVENT_ID] as? String,
            eventTitle = data[FIELD_EVENT_TITLE] as? String,
            caption = data[FIELD_SHARED_CAPTION] as? String ?: "",
            createdAtMillis = createdAtMillis,
            updatedAtMillis = updatedAtMillis,
            isDeleted = data["isDeleted"] as? Boolean ?: false
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun hasValidSharedPost(data: Map<String, Any?>): Boolean {
        val sharedPost = data[FIELD_SHARED_POST] as? Map<String, Any?> ?: return true
        return !sharedPost[FIELD_SHARED_AUTHOR].toString().isBlank() &&
            sharedPost[FIELD_SHARED_CAPTION] is String
    }

    companion object {
        const val POSTS_COLLECTION = "posts"
        const val USERS_COLLECTION = "users"
        const val COMMENTS_COLLECTION = "comments"
        const val SHARES_COLLECTION = "shares"
        const val TYPE_POST = "post"
        const val TYPE_SHARE = "share"

        const val FIELD_TYPE = "type"
        const val FIELD_POST_ID = "postId"
        const val FIELD_COMMENT_ID = "commentId"
        const val FIELD_SHARE_ID = "shareId"
        const val FIELD_AUTHOR_ID = "authorId"
        const val FIELD_AUTHOR = "author"
        const val FIELD_AUTHOR_AVATAR_URL = "authorAvatarUrl"
        const val FIELD_ROLE = "role"
        const val FIELD_TOPIC = "topic"
        const val FIELD_CONTENT = "content"
        const val FIELD_LIKES = "likes"
        const val FIELD_LIKED_BY = "likedBy"
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
        const val FIELD_ORIGINAL_POST_ID = "originalPostId"
        const val FIELD_RESHARED_FROM_POST_ID = "resharedFromPostId"
        const val FIELD_SHARED_POST = "sharedPost"
        const val FIELD_TEXT = "text"
        const val FIELD_CREATED_AT = "createdAt"
        const val FIELD_UPDATED_AT = "updatedAt"
        const val FIELD_SHARED_AUTHOR = "author"
        const val FIELD_SHARED_CAPTION = "caption"
        const val FIELD_USER_AVATAR_URL = "avatarUrl"

        val requiredFields = listOf(
            FIELD_AUTHOR_ID,
            FIELD_TYPE,
            FIELD_AUTHOR,
            FIELD_ROLE,
            FIELD_TOPIC,
            FIELD_CONTENT,
            FIELD_LIKES,
            FIELD_LIKED_BY,
            FIELD_COMMENTS,
            FIELD_SHARE_COUNT,
            FIELD_CREATED_AT,
            FIELD_UPDATED_AT
        )

        val recommendedFields = listOf(
            FIELD_AUTHOR_AVATAR_URL,
            FIELD_IMAGE_URL,
            FIELD_MEDIA_URL,
            FIELD_MEDIA_TYPE,
            FIELD_MEDIA_ITEMS,
            FIELD_EVENT_ID,
            FIELD_EVENT_TITLE,
            FIELD_ORIGINAL_POST_ID,
            FIELD_RESHARED_FROM_POST_ID
        )
    }
}

private fun DocumentSnapshot.toCommunityComment(postId: String): CommunityComment? {
    val authorId = getString(CommunityFirestoreDataSource.FIELD_AUTHOR_ID) ?: return null
    val author = getString(CommunityFirestoreDataSource.FIELD_AUTHOR) ?: return null
    return CommunityComment(
        id = id,
        postId = postId,
        authorId = authorId,
        authorName = author,
        authorAvatarUrl = getString(CommunityFirestoreDataSource.FIELD_AUTHOR_AVATAR_URL),
        text = getString(CommunityFirestoreDataSource.FIELD_TEXT).orEmpty(),
        mediaUrl = getString(CommunityFirestoreDataSource.FIELD_MEDIA_URL),
        mediaType = getString(CommunityFirestoreDataSource.FIELD_MEDIA_TYPE),
        mediaItems = get(CommunityFirestoreDataSource.FIELD_MEDIA_ITEMS)?.let { value ->
            (value as? List<Map<String, Any?>>)
                ?.mapNotNull { item ->
                    CommunityMediaItem(
                        url = item[CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_URL] as? String ?: return@mapNotNull null,
                        type = item[CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_TYPE] as? String ?: "application/octet-stream"
                    )
                }
                .orEmpty()
        }.orEmpty(),
        likes = getLong(CommunityFirestoreDataSource.FIELD_LIKES)?.toInt() ?: 0,
        likedBy = (get(CommunityFirestoreDataSource.FIELD_LIKED_BY) as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
        createdAtMillis = parseCreatedAt(get(CommunityFirestoreDataSource.FIELD_CREATED_AT)),
        updatedAtMillis = parseCreatedAt(get(CommunityFirestoreDataSource.FIELD_UPDATED_AT))
    )
}

private fun CommunityPost.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        CommunityFirestoreDataSource.FIELD_TYPE to type,
        CommunityFirestoreDataSource.FIELD_AUTHOR_ID to authorId,
        CommunityFirestoreDataSource.FIELD_AUTHOR to author,
        CommunityFirestoreDataSource.FIELD_AUTHOR_AVATAR_URL to authorAvatarUrl,
        CommunityFirestoreDataSource.FIELD_ROLE to role,
        CommunityFirestoreDataSource.FIELD_TOPIC to topic,
        CommunityFirestoreDataSource.FIELD_CONTENT to content,
        CommunityFirestoreDataSource.FIELD_LIKES to likes,
        CommunityFirestoreDataSource.FIELD_LIKED_BY to likedBy,
        CommunityFirestoreDataSource.FIELD_COMMENTS to comments,
        CommunityFirestoreDataSource.FIELD_SHARE_COUNT to shareCount,
        CommunityFirestoreDataSource.FIELD_IMAGE_URL to imageUrl,
        CommunityFirestoreDataSource.FIELD_MEDIA_URL to mediaUrl,
        CommunityFirestoreDataSource.FIELD_MEDIA_TYPE to mediaType,
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEMS to mediaItems.map { it.toFirestoreMap() },
        CommunityFirestoreDataSource.FIELD_EVENT_ID to eventId,
        CommunityFirestoreDataSource.FIELD_EVENT_TITLE to eventTitle,
        CommunityFirestoreDataSource.FIELD_ORIGINAL_POST_ID to originalPostId,
        CommunityFirestoreDataSource.FIELD_RESHARED_FROM_POST_ID to resharedFromPostId,
        CommunityFirestoreDataSource.FIELD_SHARED_POST to sharedPost?.let { share ->
            share.toFirestoreMap()
        },
        CommunityFirestoreDataSource.FIELD_CREATED_AT to FieldValue.serverTimestamp(),
        CommunityFirestoreDataSource.FIELD_UPDATED_AT to FieldValue.serverTimestamp()
    )
}

private fun CommunityPost.toOriginalShareSnapshot(originalPostId: String): SharedCommunityPost {
    return SharedCommunityPost(
        postId = originalPostId,
        authorId = authorId,
        author = author,
        authorAvatarUrl = authorAvatarUrl,
        content = content,
        mediaItems = mediaItems,
        eventId = eventId,
        eventTitle = eventTitle,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}

private fun SharedCommunityPost.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        CommunityFirestoreDataSource.FIELD_POST_ID to postId,
        CommunityFirestoreDataSource.FIELD_AUTHOR_ID to authorId,
        CommunityFirestoreDataSource.FIELD_SHARED_AUTHOR to author,
        CommunityFirestoreDataSource.FIELD_AUTHOR_AVATAR_URL to authorAvatarUrl,
        CommunityFirestoreDataSource.FIELD_CONTENT to content,
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEMS to mediaItems.map { it.toFirestoreMap() },
        CommunityFirestoreDataSource.FIELD_EVENT_ID to eventId,
        CommunityFirestoreDataSource.FIELD_EVENT_TITLE to eventTitle,
        CommunityFirestoreDataSource.FIELD_SHARED_CAPTION to caption,
        CommunityFirestoreDataSource.FIELD_CREATED_AT to createdAtMillis,
        CommunityFirestoreDataSource.FIELD_UPDATED_AT to updatedAtMillis,
        "isDeleted" to isDeleted
    )
}

private fun CommunityMediaItem.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_URL to url,
        CommunityFirestoreDataSource.FIELD_MEDIA_ITEM_TYPE to type
    )
}

private fun parseCreatedAt(rawCreatedAt: Any?): Long? {
    if (rawCreatedAt == null) return null
    return when (rawCreatedAt) {
        is com.google.firebase.Timestamp -> rawCreatedAt.toDate().time
        is java.util.Date -> rawCreatedAt.time
        is Number -> {
            val num = rawCreatedAt.toLong()
            if (num < 10_000_000_000L) num * 1000 else num
        }
        is String -> {
            val num = rawCreatedAt.toLongOrNull()
            if (num != null) {
                if (num < 10_000_000_000L) num * 1000 else num
            } else {
                null
            }
        }
        is Map<*, *> -> {
            val seconds = (rawCreatedAt["seconds"] ?: rawCreatedAt["_seconds"]) as? Number
            val nanoseconds = (rawCreatedAt["nanoseconds"] ?: rawCreatedAt["_nanoseconds"]) as? Number
            if (seconds != null) {
                seconds.toLong() * 1000 + (nanoseconds?.toLong() ?: 0L) / 1_000_000L
            } else {
                null
            }
        }
        else -> null
    }
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
