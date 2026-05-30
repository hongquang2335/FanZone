package com.example.myapplication.feature.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.repository.CommunityPostSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommunityFeedViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.communityRepository(application)
    private val _uiState = MutableStateFlow(CommunityFeedUiState())
    val uiState: StateFlow<CommunityFeedUiState> = _uiState.asStateFlow()

    private var subscription: CommunityPostSubscription? = null

    init {
        observePosts()
    }

    fun setFallbackPosts(posts: List<CommunityPost>) {
        _uiState.update { it.copy(fallbackPosts = posts) }
    }

    fun sharePost(post: CommunityPost, caption: String) {
        repository.shareCommunityPost(
            post = post,
            author = "Hong Quang",
            caption = caption.trim(),
            onSuccess = {},
            onError = { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the chia se bai viet.")
                }
            }
        )
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
        subscription?.dispose()
        subscription = null
        super.onCleared()
    }
}
