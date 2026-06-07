package com.example.myapplication.feature.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(user = auth.currentUser?.toAuthUser()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        _uiState.update { state ->
            state.copy(user = firebaseUser?.toAuthUser(), isLoading = false)
        }
        firebaseUser?.uid?.let { uid ->
            loadUserProfile(uid)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.uid?.let(::loadUserProfile)
    }

    fun signIn(account: String, password: String, onSuccess: () -> Unit) {
        if (account.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nhập email hoặc số điện thoại và mật khẩu.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }

        val trimmedAccount = account.trim()
        if (trimmedAccount.contains("@")) {
            signInWithEmail(trimmedAccount, password, onSuccess)
            return
        }

        val normalizedPhone = trimmedAccount.filter { it.isDigit() }
        if (normalizedPhone.length != 10) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Số điện thoại phải gồm 10 số."
                )
            }
            return
        }

        firestore.collection("users")
            .whereEqualTo("phone", normalizedPhone)
            .limit(2)
            .get()
            .addOnSuccessListener { snapshot ->
                val documents = snapshot.documents
                when {
                    documents.isEmpty() -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Không tìm thấy tài khoản có số điện thoại này."
                            )
                        }
                    }
                    documents.size > 1 -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Số điện thoại này đang gắn với nhiều tài khoản. Vui lòng đăng nhập bằng email."
                            )
                        }
                    }
                    else -> {
                        val email = documents.first().getString("email").orEmpty()
                        if (email.isBlank()) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Tài khoản này chưa có email đăng nhập."
                                )
                            }
                        } else {
                            signInWithEmail(email, password, onSuccess)
                        }
                    }
                }
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Không thể kiểm tra số điện thoại."
                    )
                }
            }
    }

    private fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                upsertUserDocument(result.user)
                _uiState.update {
                    it.copy(
                        user = result.user?.toAuthUser(),
                        isLoading = false,
                        errorMessage = null,
                        infoMessage = if (result.user?.isEmailVerified == false) {
                            "Tài khoản chưa xác nhận email."
                        } else {
                            null
                        }
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Không thể đăng nhập.")
                    )
                }
            }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Khong the lay thong tin Google account.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                upsertUserDocument(result.user)
                _uiState.update {
                    it.copy(
                        user = result.user?.toAuthUser(),
                        isLoading = false,
                        errorMessage = null,
                        infoMessage = null
                    )
                }
                result.user?.uid?.let(::loadUserProfile)
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Khong the dang nhap bang Google.")
                    )
                }
            }
    }

    fun setAuthError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message, infoMessage = null) }
    }

    fun register(email: String, password: String, repeatPassword: String, onSuccess: () -> Unit) {
        if (!isValidPassword(password) || password != repeatPassword || email.isBlank()) {
            _uiState.update {
                it.copy(
                    showRegisterPasswordRules = true,
                    errorMessage = if (password != repeatPassword) "Mật khẩu nhập lại không khớp." else null
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                infoMessage = null,
                showRegisterPasswordRules = false
            )
        }
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                upsertUserDocument(result.user)
                result.user?.sendEmailVerification()
                    ?.addOnCompleteListener {
                        _uiState.update {
                            it.copy(
                                user = result.user?.toAuthUser(),
                                isLoading = false,
                                infoMessage = "Đã gửi email xác nhận. Hãy kiểm tra hộp thư của bạn.",
                                errorMessage = null
                            )
                        }
                        onSuccess()
                    }
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showRegisterPasswordRules = true,
                        errorMessage = throwable.toAuthMessage("Không thể tạo tài khoản.")
                    )
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _uiState.value = AuthUiState()
    }

    fun saveAccountProfile(
        fullName: String,
        phone: String,
        birthday: String,
        gender: String,
        onSuccess: () -> Unit
    ) {
        val user = auth.currentUser ?: return
        val normalizedPhone = phone.filter { it.isDigit() }
        if (normalizedPhone.isNotEmpty() && normalizedPhone.length != 10) {
            _uiState.update { it.copy(errorMessage = "Số điện thoại phải gồm 10 số.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        val profile = AccountProfile(
            fullName = fullName.trim(),
            phone = normalizedPhone,
            birthday = birthday.trim(),
            gender = gender,
            avatarUri = _uiState.value.accountProfile.avatarUri
        )
        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "uid" to user.uid,
                    "email" to user.email.orEmpty(),
                    "displayName" to profile.fullName,
                    "phone" to profile.phone,
                    "birthday" to profile.birthday,
                    "gender" to profile.gender,
                    "avatarUri" to profile.avatarUri,
                    "emailVerified" to user.isEmailVerified,
                    "updatedAt" to System.currentTimeMillis()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        accountProfile = profile.copy(pinSet = it.accountProfile.pinSet),
                        user = it.user?.copy(displayName = profile.fullName.takeIf { name -> name.isNotBlank() }),
                        isLoading = false,
                        infoMessage = "Đã lưu thông tin tài khoản."
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = throwable.localizedMessage ?: "Không thể lưu thông tin.") }
            }
    }

    fun savePin(pin: String, onSuccess: () -> Unit) {
        val user = auth.currentUser ?: return
        if (pin.length != 6 || pin.any { !it.isDigit() }) {
            _uiState.update { it.copy(errorMessage = "Mã PIN phải gồm 6 số.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "pin" to pin,
                    "pinSet" to true,
                    "updatedAt" to System.currentTimeMillis()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        accountProfile = it.accountProfile.copy(pinSet = true),
                        isLoading = false,
                        infoMessage = "Đã thiết lập mã PIN."
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = throwable.localizedMessage ?: "Không thể lưu mã PIN.") }
            }
    }

    fun saveAvatarUri(uri: String) {
        val user = auth.currentUser ?: return
        _uiState.update {
            it.copy(
                accountProfile = it.accountProfile.copy(avatarUri = uri),
                errorMessage = null,
                infoMessage = null
            )
        }
        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "avatarUri" to uri,
                    "updatedAt" to System.currentTimeMillis()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
            .addOnSuccessListener {
                _uiState.update { it.copy(infoMessage = "Da cap nhat anh dai dien.") }
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.localizedMessage ?: "Khong the cap nhat anh dai dien.")
                }
            }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun upsertUserDocument(user: FirebaseUser?) {
        if (user == null) return
        val data = mapOf(
            "uid" to user.uid,
            "email" to user.email.orEmpty(),
            "displayName" to user.displayName.orEmpty(),
            "emailVerified" to user.isEmailVerified,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(user.uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    private fun loadUserProfile(uid: String) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val profile = AccountProfile(
                    fullName = document.getString("displayName").orEmpty(),
                    phone = document.getString("phone").orEmpty(),
                    birthday = document.getString("birthday").orEmpty(),
                    gender = document.getString("gender").orEmpty(),
                    avatarUri = document.getString("avatarUri"),
                    pinSet = document.getBoolean("pinSet") ?: false
                )
                _uiState.update { state ->
                    state.copy(
                        accountProfile = profile,
                        user = state.user?.copy(displayName = profile.fullName.takeIf { it.isNotBlank() } ?: state.user.displayName)
                    )
                }
            }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}

private fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName?.takeIf { it.isNotBlank() },
        emailVerified = isEmailVerified
    )
}

private fun isValidPassword(password: String): Boolean {
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasUppercase = password.any { it.isUpperCase() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    return password.length in 8..32 && hasLowercase && hasDigit && hasUppercase && hasSpecial
}

private fun Throwable.toAuthMessage(fallback: String): String {
    val code = (this as? FirebaseAuthException)?.errorCode
    return when (code) {
        "ERROR_CONFIGURATION_NOT_FOUND" ->
            "Firebase Auth chưa được cấu hình. Hãy bật Authentication > Sign-in method > Email/Password trong Firebase console."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Email này đã được đăng ký."
        "ERROR_INVALID_EMAIL" -> "Email không hợp lệ."
        "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu."
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Email hoặc mật khẩu không đúng."
        "ERROR_USER_NOT_FOUND" -> "Tài khoản không tồn tại."
        else -> localizedMessage ?: fallback
    }
}
