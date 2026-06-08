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

class CommunityViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var subscription: CommunityPostSubscription? = null
    private val commentSubscriptions = mutableMapOf<String, CommunityPostSubscription>()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            _uiState.update {
                it.copy(
                    currentAuthorName = "Ban",
                    currentAuthorAvatarUrl = null,
                    currentUserId = null,
                    followedProfileIds = emptySet()
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
            repository.likeCommunityPost(postId, currentUserId, onSuccess = {}, onError = {})
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
            onSuccess = {},
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
                currentAuthorAvatarUrl = user.photoUrl?.toString(),
                currentUserId = user.uid
            )
        }

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (auth.currentUser?.uid != user.uid) return@addOnSuccessListener
                val profileName = document.getString("displayName")?.takeIf { it.isNotBlank() }
                val avatarUrl = document.getString("avatarUrl")?.takeIf { it.isNotBlank() } ?: user.photoUrl?.toString()
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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Khong the tai bai viet."
                    )
                }
            }
        )
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        subscription?.dispose()
        subscription = null
        commentSubscriptions.values.forEach { it.dispose() }
        commentSubscriptions.clear()
        super.onCleared()
    }
}
