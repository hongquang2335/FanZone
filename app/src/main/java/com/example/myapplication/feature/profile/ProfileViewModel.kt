package com.example.myapplication.feature.profile

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.feature.authentication.AuthUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var userRegistration: ListenerRegistration? = null
    private var observedUserId: String? = null

    fun load(
        user: UserProfile,
        authState: AuthUiState,
        unreadSupport: Int,
        posts: List<CommunityPost>,
        unreadNotificationCount: Int = 0,
        darkTheme: Boolean = _uiState.value.darkTheme
    ) {
        _uiState.update {
            it.copy(
                user = user,
                authState = authState,
                unreadSupport = unreadSupport,
                posts = posts,
                avatarUrl = authState.accountProfile.avatarUrl,
                unreadNotificationCount = unreadNotificationCount,
                darkTheme = darkTheme
            )
        }
        observeUserStats(authState.user?.uid)
    }

    private fun observeUserStats(userId: String?) {
        if (observedUserId == userId) return
        userRegistration?.remove()
        observedUserId = userId
        if (userId == null) {
            userRegistration = null
            _uiState.update {
                it.copy(avatarUrl = null, followerCount = 0, followingCount = 0)
            }
            return
        }
        userRegistration = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val followerCount = snapshot.getLong("followers")?.toInt()
                    ?: (snapshot.get("followerIds") as? List<*>)?.size
                    ?: 0
                val followingCount = snapshot.getLong("following")?.toInt()
                    ?: (snapshot.get("followingIds") as? List<*>)?.size
                    ?: 0
                _uiState.update {
                    it.copy(
                        avatarUrl = snapshot.getString("avatarUrl") ?: it.avatarUrl,
                        followerCount = followerCount,
                        followingCount = followingCount
                    )
                }
            }
    }

    override fun onCleared() {
        userRegistration?.remove()
        super.onCleared()
    }
}
