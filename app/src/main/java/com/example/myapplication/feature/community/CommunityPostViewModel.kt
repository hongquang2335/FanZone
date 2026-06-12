package com.example.myapplication.feature.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.repository.CreateCommunityPostRequest
import com.example.myapplication.domain.repository.SelectedCommunityMedia
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.model.NotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunityPostViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val notificationRepository = AppDependencies.notificationRepository(application)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CommunityPostUiState())
    val uiState: StateFlow<CommunityPostUiState> = _uiState.asStateFlow()
    private var currentUserListener: com.google.firebase.firestore.ListenerRegistration? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            clearCurrentAuthor()
        } else {
            loadCurrentAuthor(user)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.let(::loadCurrentAuthor)
    }

    fun updateDraft(value: String) {
        _uiState.update { it.copy(draft = value, errorMessage = null) }
    }

    fun addMedia(media: List<SelectedCommunityMedia>) {
        _uiState.update {
            it.copy(
                selectedMedia = it.selectedMedia + media,
                errorMessage = null
            )
        }
    }

    fun removeMedia(media: SelectedCommunityMedia) {
        _uiState.update {
            it.copy(
                selectedMedia = it.selectedMedia.filterNot { selected -> selected.uri == media.uri },
                errorMessage = null
            )
        }
    }

    fun toggleAnonymous() {
        _uiState.update { it.copy(anonymous = !it.anonymous) }
    }

    fun toggleFeeling() {
        _uiState.update { it.copy(feeling = !it.feeling) }
    }

    fun createPost(
        eventId: String?,
        eventTitle: String?,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        val content = state.draft.trim()
        if (state.isPosting || (content.isBlank() && state.selectedMedia.isEmpty())) return

        val authorId = state.currentAuthorId
        if (authorId == null) {
            _uiState.update { it.copy(errorMessage = "Ban can dang nhap de tao bai viet.") }
            return
        }

        _uiState.update { it.copy(isPosting = true, errorMessage = null) }

        repository.createCommunityPost(
            request = CreateCommunityPostRequest(
                authorId = authorId,
                author = state.currentAuthorName,
                authorAvatarUrl = state.currentAuthorAvatarUrl,
                anonymous = state.anonymous,
                content = content,
                eventId = eventId,
                eventTitle = eventTitle,
                media = state.selectedMedia
            ),
            onSuccess = { postId ->
                _uiState.value = CommunityPostUiState(
                    currentAuthorId = authorId,
                    currentAuthorName = state.currentAuthorName,
                    currentAuthorAvatarUrl = state.currentAuthorAvatarUrl
                )
                firestore.collection("users")
                    .document(authorId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val followerIds = (document.get("followerIds") as? List<*>)
                                ?.mapNotNull { it as? String }
                                .orEmpty()
                            followerIds.forEach { followerId ->
                                val notification = Notification(
                                    recipientId = followerId,
                                    senderId = authorId,
                                    senderName = state.currentAuthorName,
                                    senderAvatarUrl = state.currentAuthorAvatarUrl,
                                    type = NotificationType.NEW_POST,
                                    postId = postId,
                                    postContentExcerpt = content.take(60)
                                )
                                notificationRepository.createNotification(notification)
                            }
                        }
                    }
                onSuccess()
            },
            onError = ::handlePostError
        )
    }

    private fun clearCurrentAuthor() {
        currentUserListener?.remove()
        currentUserListener = null
        _uiState.update {
            CommunityPostUiState(
                draft = it.draft,
                selectedMedia = it.selectedMedia,
                anonymous = it.anonymous,
                feeling = it.feeling
            )
        }
    }

    private fun loadCurrentAuthor(user: FirebaseUser) {
        val fallbackName = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "Ban"

        _uiState.update {
            it.copy(
                currentAuthorId = user.uid,
                currentAuthorName = fallbackName,
                currentAuthorAvatarUrl = null
            )
        }

        currentUserListener?.remove()
        currentUserListener = firestore.collection("users")
            .document(user.uid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    android.util.Log.e("CommunityPostViewModel", "Error listening to current user: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (document != null && document.exists() && auth.currentUser?.uid == user.uid) {
                    val profileName = document.getString("displayName")?.takeIf { it.isNotBlank() }
                    val avatarUrl = document.getString("avatarUrl")?.takeIf { it.isNotBlank() }
                    _uiState.update {
                        it.copy(
                            currentAuthorId = user.uid,
                            currentAuthorName = profileName ?: fallbackName,
                            currentAuthorAvatarUrl = avatarUrl
                        )
                    }
                }
            }
    }

    private fun handlePostError(throwable: Throwable) {
        _uiState.update {
            it.copy(
                isPosting = false,
                errorMessage = throwable.localizedMessage ?: "Khong the dang bai viet."
            )
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        currentUserListener?.remove()
        currentUserListener = null
        super.onCleared()
    }
}
