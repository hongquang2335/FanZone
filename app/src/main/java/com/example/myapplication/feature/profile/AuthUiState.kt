package com.example.myapplication.feature.profile

data class AuthUiState(
    val user: AuthUser? = null,
    val accountProfile: AccountProfile = AccountProfile(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val showRegisterPasswordRules: Boolean = false
) {
    val isSignedIn: Boolean
        get() = user != null
}

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val emailVerified: Boolean
)

data class AccountProfile(
    val fullName: String = "",
    val phone: String = "",
    val birthday: String = "",
    val gender: String = "",
    val avatarUri: String? = null,
    val pinSet: Boolean = false
)
