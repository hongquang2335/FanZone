package com.example.myapplication.feature.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.CommunityComment
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.repository.CommunityPostSubscription
import com.example.myapplication.domain.repository.CreateCommunityCommentRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.NotificationType
import com.example.myapplication.domain.repository.NotificationRepository
import com.example.myapplication.core.notification.NotificationHelper

class CommunityViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val notificationRepository = AppDependencies.notificationRepository(application)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var subscription: CommunityPostSubscription? = null
    private var notificationSubscription: CommunityPostSubscription? = null
    private val commentSubscriptions = mutableMapOf<String, CommunityPostSubscription>()
    private var currentUserListener: com.google.firebase.firestore.ListenerRegistration? = null
 
    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            subscription?.dispose()
            subscription = null
            notificationSubscription?.dispose()
            notificationSubscription = null
            currentUserListener?.remove()
            currentUserListener = null
            NotificationHelper.reset()
            _uiState.update {
                it.copy(
                    currentAuthorName = "Ban",
                    currentAuthorAvatarUrl = null,
                    currentUserId = null,
                    followedProfileIds = emptySet(),
                    unreadNotificationCount = 0
                )
            }
        } else {
            loadCurrentAuthor(user)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.let(::loadCurrentAuthor)
        observePosts()
    }

    fun sharePost(post: CommunityPost, caption: String) {
        val currentUserId = _uiState.value.currentUserId
        if (currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de chia se bai viet.") }
            return
        }

        repository.shareCommunityPost(
            post = post,
            shareAuthorId = currentUserId,
            author = _uiState.value.currentAuthorName,
            authorAvatarUrl = _uiState.value.currentAuthorAvatarUrl,
            caption = caption.trim(),
            onSuccess = {},
            onError = { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the chia se bai viet.")
                }
            }
        )
    }

    fun toggleLike(postId: String) {
        val currentUserId = _uiState.value.currentUserId ?: return
        val post = _uiState.value.posts.firstOrNull { it.id == postId } ?: return
        val isLiked = post.likedBy.contains(currentUserId)

        if (isLiked) {
            repository.unlikeCommunityPost(postId, currentUserId, onSuccess = {}, onError = {})
        } else {
            repository.likeCommunityPost(postId, currentUserId, onSuccess = {
                if (post.authorId != null && post.authorId != currentUserId) {
                    val notification = Notification(
                        recipientId = post.authorId,
                        senderId = currentUserId,
                        senderName = _uiState.value.currentAuthorName,
                        senderAvatarUrl = _uiState.value.currentAuthorAvatarUrl,
                        type = NotificationType.LIKE,
                        postId = postId,
                        postContentExcerpt = post.content.take(60)
                    )
                    notificationRepository.createNotification(notification)
                }
            }, onError = {})
        }
    }

    fun toggleFollow(profileId: String) {
        val currentUserId = _uiState.value.currentUserId
        if (currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de follow.") }
            return
        }
        if (currentUserId == profileId) return

        val isFollowing = _uiState.value.followedProfileIds.contains(profileId)
        _uiState.update { state ->
            val nextFollowed = if (isFollowing) {
                state.followedProfileIds - profileId
            } else {
                state.followedProfileIds + profileId
            }
            state.copy(followedProfileIds = nextFollowed)
        }

        val currentUserRef = firestore.collection("users").document(currentUserId)
        val targetUserRef = firestore.collection("users").document(profileId)
        firestore.runBatch { batch ->
            if (isFollowing) {
                batch.update(currentUserRef, "followingIds", com.google.firebase.firestore.FieldValue.arrayRemove(profileId))
                batch.update(currentUserRef, "following", com.google.firebase.firestore.FieldValue.increment(-1))
                batch.update(targetUserRef, "followerIds", com.google.firebase.firestore.FieldValue.arrayRemove(currentUserId))
                batch.update(targetUserRef, "followers", com.google.firebase.firestore.FieldValue.increment(-1))
            } else {
                batch.update(currentUserRef, "followingIds", com.google.firebase.firestore.FieldValue.arrayUnion(profileId))
                batch.update(currentUserRef, "following", com.google.firebase.firestore.FieldValue.increment(1))
                batch.update(targetUserRef, "followerIds", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                batch.update(targetUserRef, "followers", com.google.firebase.firestore.FieldValue.increment(1))
            }
        }.addOnSuccessListener {
            if (!isFollowing && profileId != currentUserId) {
                val notification = Notification(
                    recipientId = profileId,
                    senderId = currentUserId,
                    senderName = _uiState.value.currentAuthorName,
                    senderAvatarUrl = _uiState.value.currentAuthorAvatarUrl,
                    type = NotificationType.FOLLOW
                )
                notificationRepository.createNotification(notification)
            }
        }.addOnFailureListener { throwable ->
            _uiState.update { state ->
                state.copy(
                    followedProfileIds = if (isFollowing) state.followedProfileIds + profileId else state.followedProfileIds - profileId,
                    errorMessage = throwable.localizedMessage ?: "Khong the cap nhat follow."
                )
            }
        }
    }

    fun observeComments(postId: String) {
        if (commentSubscriptions.containsKey(postId)) return
        commentSubscriptions[postId] = repository.observeComments(
            postId = postId,
            onComments = { comments ->
                _uiState.update { state ->
                    state.copy(commentsByPostId = state.commentsByPostId + (postId to comments))
                }
            },
            onError = { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the tai binh luan.")
                }
            }
        )
    }

    fun addComment(postId: String, text: String) {
        val currentUserId = _uiState.value.currentUserId
        if (currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de binh luan.") }
            return
        }
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return
        repository.addComment(
            request = CreateCommunityCommentRequest(
                postId = postId,
                authorId = currentUserId,
                authorName = _uiState.value.currentAuthorName,
                authorAvatarUrl = _uiState.value.currentAuthorAvatarUrl,
                text = trimmedText
            ),
            onSuccess = {
                val post = _uiState.value.posts.firstOrNull { it.id == postId }
                if (post != null && post.authorId != null && post.authorId != currentUserId) {
                    val notification = Notification(
                        recipientId = post.authorId,
                        senderId = currentUserId,
                        senderName = _uiState.value.currentAuthorName,
                        senderAvatarUrl = _uiState.value.currentAuthorAvatarUrl,
                        type = NotificationType.COMMENT,
                        postId = postId,
                        postContentExcerpt = trimmedText.take(60)
                    )
                    notificationRepository.createNotification(notification)
                }
            },
            onError = { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the gui binh luan.")
                }
            }
        )
    }

    private fun loadCurrentAuthor(user: FirebaseUser) {
        val fallbackName = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "Ban"

        _uiState.update {
            it.copy(
                currentAuthorName = fallbackName,
                currentAuthorAvatarUrl = null,
                currentUserId = user.uid
            )
        }
        observeNotifications(user.uid)

        currentUserListener?.remove()
        currentUserListener = firestore.collection("users")
            .document(user.uid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    android.util.Log.e("CommunityViewModel", "Error listening to current user: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (document != null && document.exists() && auth.currentUser?.uid == user.uid) {
                    val profileName = document.getString("displayName")?.takeIf { it.isNotBlank() }
                    val avatarUrl = document.getString("avatarUrl")?.takeIf { it.isNotBlank() }
                    val followingIds = (document.get("followingIds") as? List<*>)?.mapNotNull { it as? String }.orEmpty().toSet()
                    _uiState.update {
                        it.copy(
                            currentAuthorName = profileName ?: fallbackName,
                            currentAuthorAvatarUrl = avatarUrl,
                            currentUserId = user.uid,
                            followedProfileIds = followingIds
                        )
                    }
                }
            }
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId, onSuccess = {}, onError = {})
    }

    private fun observePosts() {
        subscription?.dispose()
        subscription = repository.observeCommunityPosts(
            onPosts = { posts ->
                _uiState.update {
                    it.copy(
                        remotePosts = posts,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            },
            onError = { throwable ->
                android.util.Log.e("CommunityViewModel", "Error loading community posts: ${throwable.message}", throwable)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Khong the tai bai viet."
                    )
                }
            }
        )
    }

    fun refreshPosts() {
        _uiState.update { it.copy(isLoading = true) }
        observePosts()
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        subscription?.dispose()
        subscription = null
        notificationSubscription?.dispose()
        notificationSubscription = null
        currentUserListener?.remove()
        currentUserListener = null
        commentSubscriptions.values.forEach { it.dispose() }
        commentSubscriptions.clear()
        super.onCleared()
    }
 
    private fun observeNotifications(userId: String) {
        notificationSubscription?.dispose()
        notificationSubscription = notificationRepository.observeNotifications(
            userId = userId,
            onNotifications = { list ->
                val unread = list.count { !it.isRead }
                _uiState.update { it.copy(unreadNotificationCount = unread) }
                NotificationHelper.handleNotifications(getApplication(), list)
            },
            onError = {}
        )
    }

    fun setTargetCommentsPostId(postId: String?) {
        _uiState.update { it.copy(targetCommentsPostId = postId) }
        if (postId != null) {
            observeComments(postId)
        }
    }

    fun openEditPost(post: CommunityPost) {
        _uiState.update { it.copy(editingPost = post) }
    }

    fun closeEditPost() {
        _uiState.update { it.copy(editingPost = null) }
    }

    fun updatePost(postId: String, text: String, mediaItems: List<com.example.myapplication.domain.model.CommunityMediaItem>) {
        repository.updatePost(
            postId = postId,
            text = text,
            mediaItems = mediaItems,
            onSuccess = {
                closeEditPost()
                observePosts()
            },
            onError = { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the cap nhat bai viet.")
                }
            }
        )
    }
}
