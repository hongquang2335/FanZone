package com.example.myapplication.feature.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.repository.CommunityPostSubscription
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunityFeedViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CommunityFeedUiState())
    val uiState: StateFlow<CommunityFeedUiState> = _uiState.asStateFlow()

    private var subscription: CommunityPostSubscription? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            _uiState.update {
                it.copy(
                    currentAuthorName = "Ban",
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

    fun setFallbackPosts(posts: List<CommunityPost>) {
        _uiState.update { it.copy(fallbackPosts = posts) }
    }

    fun sharePost(post: CommunityPost, caption: String) {
        if (_uiState.value.currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de chia se bai viet.") }
            return
        }

        repository.shareCommunityPost(
            post = post,
            author = _uiState.value.currentAuthorName,
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
        if (_uiState.value.currentUserId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de follow.") }
            return
        }

        _uiState.update { state ->
            val nextFollowed = if (state.followedProfileIds.contains(profileId)) {
                state.followedProfileIds - profileId
            } else {
                state.followedProfileIds + profileId
            }
            state.copy(followedProfileIds = nextFollowed)
        }
    }

    private fun loadCurrentAuthor(user: FirebaseUser) {
        val fallbackName = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "Ban"

        _uiState.update { it.copy(currentAuthorName = fallbackName, currentUserId = user.uid) }

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (auth.currentUser?.uid != user.uid) return@addOnSuccessListener
                val profileName = document.getString("displayName")?.takeIf { it.isNotBlank() }
                _uiState.update { it.copy(currentAuthorName = profileName ?: fallbackName, currentUserId = user.uid) }
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
        super.onCleared()
    }
}
