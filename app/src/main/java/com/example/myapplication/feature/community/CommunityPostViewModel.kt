package com.example.myapplication.feature.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.repository.CreateCommunityPostRequest
import com.example.myapplication.domain.repository.SelectedCommunityMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunityPostViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val _uiState = MutableStateFlow(CommunityPostUiState())
    val uiState: StateFlow<CommunityPostUiState> = _uiState.asStateFlow()

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

        _uiState.update { it.copy(isPosting = true, errorMessage = null) }

        repository.createCommunityPost(
            request = CreateCommunityPostRequest(
                authorId = "user-hong-quang",
                author = "Hong Quang",
                anonymous = state.anonymous,
                content = content,
                eventId = eventId,
                eventTitle = eventTitle,
                media = state.selectedMedia
            ),
            onSuccess = {
                _uiState.value = CommunityPostUiState()
                onSuccess()
            },
            onError = ::handlePostError
        )
    }

    private fun handlePostError(throwable: Throwable) {
        _uiState.update {
            it.copy(
                isPosting = false,
                errorMessage = throwable.localizedMessage ?: "Khong the dang bai viet."
            )
        }
    }
}
