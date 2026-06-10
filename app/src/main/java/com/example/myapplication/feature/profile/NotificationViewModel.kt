package com.example.myapplication.feature.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.model.Notification
import com.example.myapplication.domain.repository.CommunityPostSubscription
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = AppDependencies.notificationRepository(application)
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private var subscription: CommunityPostSubscription? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            subscription?.dispose()
            subscription = null
            _uiState.update {
                it.copy(
                    notifications = emptyList(),
                    currentUserId = null
                )
            }
        } else {
            _uiState.update { it.copy(currentUserId = user.uid, isLoading = true) }
            observeNotifications(user.uid)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.let { user ->
            _uiState.update { it.copy(currentUserId = user.uid, isLoading = true) }
            observeNotifications(user.uid)
        }
    }

    private fun observeNotifications(userId: String) {
        subscription?.dispose()
        subscription = repository.observeNotifications(
            userId = userId,
            onNotifications = { list ->
                _uiState.update {
                    it.copy(
                        notifications = list,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            },
            onError = { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Khong the tai thong bao."
                    )
                }
            }
        )
    }

    fun markAsRead(notificationId: String) {
        repository.markAsRead(notificationId, onSuccess = {}, onError = {})
    }

    fun markAllAsRead() {
        val currentUserId = _uiState.value.currentUserId ?: return
        repository.markAllAsRead(currentUserId, onSuccess = {}, onError = {})
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        subscription?.dispose()
        subscription = null
        super.onCleared()
    }
}
