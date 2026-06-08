package com.example.myapplication.feature.authentication

data class AuthUiState(
    val user: AuthUser? = null,
    val accountProfile: AccountProfile = AccountProfile(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val showRegisterPasswordRules: Boolean = false,
    val pendingPasswordResetEmail: String? = null,
    val pendingPasswordResetCode: String? = null
) {
    val isSignedIn: Boolean
        get() = user != null
}

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val emailVerified: Boolean,
    val providerIds: List<String> = emptyList()
) {
    val isGoogleLinked: Boolean
        get() = providerIds.contains("google.com")
}

data class AccountProfile(
    val fullName: String = "",
    val phone: String = "",
    val birthday: String = "",
    val gender: String = "",
    val avatarUrl: String? = null
)
