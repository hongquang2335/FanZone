package com.example.myapplication.feature.authentication

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(user = auth.currentUser?.toAuthUser()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private var pendingGoogleCredential: AuthCredential? = null
    private var pendingGoogleEmail: String? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        _uiState.update { state ->
            state.copy(
                user = firebaseUser?.toAuthUser(),
                searchHistory = if (firebaseUser == null) emptyList() else state.searchHistory,
                isLoading = false
            )
        }
        firebaseUser?.uid?.let { uid ->
            loadUserProfile(uid)
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        auth.currentUser?.uid?.let(::loadUserProfile)
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nhập email và mật khẩu.") }
            return
        }
        if (!normalizedEmail.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập email hợp lệ.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        signInWithEmail(normalizedEmail, password, onSuccess)
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Không nhận được Google ID token.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val googleEmail = idToken.emailFromJwt()
        val signedInUser = auth.currentUser
        if (signedInUser != null) {
            signedInUser.linkWithCredential(credential)
                .addOnSuccessListener { result ->
                    handleAuthSuccess(
                        user = result.user,
                        infoMessage = "Đã liên kết Google với tài khoản hiện tại.",
                        onSuccess = onSuccess
                    )
                }
                .addOnFailureListener { throwable ->
                    val code = (throwable as? FirebaseAuthException)?.errorCode
                    if (code == "ERROR_PROVIDER_ALREADY_LINKED" || code == "ERROR_CREDENTIAL_ALREADY_IN_USE") {
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener { result ->
                                handleAuthSuccess(
                                    user = result.user,
                                    infoMessage = "Đã đăng nhập bằng Google.",
                                    onSuccess = onSuccess
                                )
                            }
                            .addOnFailureListener { signInThrowable ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = signInThrowable.toAuthMessage("Không thể đăng nhập bằng Google.")
                                    )
                                }
                            }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = throwable.toAuthMessage("Không thể liên kết Google với tài khoản này.")
                            )
                        }
                    }
                }
            return
        }

        requirePasswordBeforeGoogleIfNeeded(
            email = googleEmail,
            credential = credential,
            onAllowed = { continueGoogleSignIn(credential, googleEmail, onSuccess) }
        )
        return

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                handleAuthSuccess(
                    user = result.user,
                    infoMessage = "Đã đăng nhập bằng Google.",
                    onSuccess = onSuccess
                )
            }
            .addOnFailureListener { throwable ->
                val code = (throwable as? FirebaseAuthException)?.errorCode
                if (code == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                    pendingGoogleCredential = credential
                    pendingGoogleEmail = googleEmail
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Email này đã có tài khoản email/mật khẩu. Hãy đăng nhập bằng mật khẩu một lần, app sẽ tự liên kết Google và đồng bộ dữ liệu.",
                            infoMessage = null
                        )
                    }
                    return@addOnFailureListener
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Không thể đăng nhập bằng Google.")
                    )
                }
            }
    }

    private fun requirePasswordBeforeGoogleIfNeeded(
        email: String?,
        credential: AuthCredential,
        onAllowed: () -> Unit
    ) {
        if (email.isNullOrBlank()) {
            onAllowed()
            return
        }

        fun holdUntilPasswordLogin() {
            pendingGoogleCredential = credential
            pendingGoogleEmail = email
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Email nay da co tai khoan email/mat khau. Hay dang nhap bang mat khau mot lan, app se tu lien ket Google va dong bo du lieu.",
                    infoMessage = null
                )
            }
        }

        fun checkFirestoreUser() {
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    val mustLoginWithPasswordFirst = snapshot.documents
                        .filterNot { it.id == USER_SCHEMA_SEED_ID }
                        .any { document ->
                            val providers = (document.get("authProviders") as? List<*>)
                                ?.mapNotNull { it as? String }
                                .orEmpty()
                            !providers.contains("google.com")
                        }

                    if (mustLoginWithPasswordFirst) {
                        holdUntilPasswordLogin()
                        return@addOnSuccessListener
                    }

                    onAllowed()
                }
                .addOnFailureListener {
                    onAllowed()
                }
        }

        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val methods = result.signInMethods.orEmpty()
                if (methods.contains("password") && !methods.contains("google.com")) {
                    holdUntilPasswordLogin()
                    return@addOnSuccessListener
                }
                checkFirestoreUser()
            }
            .addOnFailureListener {
                checkFirestoreUser()
            }
    }

    private fun continueGoogleSignIn(
        credential: AuthCredential,
        googleEmail: String?,
        onSuccess: () -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                handleAuthSuccess(
                    user = result.user,
                    infoMessage = "Đăng nhập bằng Google thành công.",
                    onSuccess = onSuccess
                )
            }
            .addOnFailureListener { throwable ->
                val code = (throwable as? FirebaseAuthException)?.errorCode
                if (code == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") {
                    pendingGoogleCredential = credential
                    pendingGoogleEmail = googleEmail
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Email nay da co tai khoan email/mat khau. Hay dang nhap bang mat khau mot lan, app se tu lien ket Google va dong bo du lieu.",
                            infoMessage = null
                        )
                    }
                    return@addOnFailureListener
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Không thể đăng nhập bằng Google.")
                    )
                }
            }
    }

    private fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                val pendingCredential = pendingGoogleCredential
                val pendingEmail = pendingGoogleEmail
                val shouldLinkPendingGoogle = pendingCredential != null &&
                    pendingEmail != null &&
                    user?.email?.equals(pendingEmail, ignoreCase = true) == true

                if (shouldLinkPendingGoogle) {
                    user.linkWithCredential(pendingCredential)
                        .addOnSuccessListener { linkResult ->
                            pendingGoogleCredential = null
                            pendingGoogleEmail = null
                            handleAuthSuccess(
                                user = linkResult.user,
                                infoMessage = "Đã đăng nhập và đồng bộ Google với tài khoản email/mật khẩu.",
                                onSuccess = onSuccess
                            )
                        }
                        .addOnFailureListener { linkThrowable ->
                            val code = (linkThrowable as? FirebaseAuthException)?.errorCode
                            if (code == "ERROR_PROVIDER_ALREADY_LINKED" || code == "ERROR_CREDENTIAL_ALREADY_IN_USE") {
                                pendingGoogleCredential = null
                                pendingGoogleEmail = null
                                handleAuthSuccess(
                                    user = user,
                                    infoMessage = "Tài khoản này đã liên kết Google.",
                                    onSuccess = onSuccess
                                )
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = linkThrowable.toAuthMessage("Đăng nhập thành công nhưng chưa thể liên kết Google.")
                                    )
                                }
                            }
                        }
                    return@addOnSuccessListener
                }

                if (pendingCredential != null && pendingEmail != null) {
                    pendingGoogleCredential = null
                    pendingGoogleEmail = null
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Email vừa đăng nhập không trùng với tài khoản Google cần đồng bộ."
                        )
                    }
                    return@addOnSuccessListener
                }

                handleAuthSuccess(
                    user = result.user,
                    infoMessage = if (result.user?.isEmailVerified == false) {
                        "Tài khoản chưa xác nhận email."
                    } else {
                        null
                    },
                    onSuccess = onSuccess
                )
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

    fun register(email: String, password: String, repeatPassword: String, onSuccess: () -> Unit) {
        val normalizedEmail = email.trim()
        if (!isValidPassword(password) || password != repeatPassword || normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            _uiState.update {
                it.copy(
                    showRegisterPasswordRules = true,
                    errorMessage = when {
                        normalizedEmail.isBlank() || !normalizedEmail.contains("@") -> "Vui lòng nhập email hợp lệ."
                        password != repeatPassword -> "Mật khẩu nhập lại không khớp."
                        else -> null
                    }
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
        ensureEmailAvailableForRegister(normalizedEmail) {
            auth.createUserWithEmailAndPassword(normalizedEmail, password)
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
    }

    private fun ensureEmailAvailableForRegister(email: String, onAvailable: () -> Unit) {
        fun blockExistingEmail() {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showRegisterPasswordRules = false,
                    errorMessage = "Email nay da duoc dung. Hay dang nhap bang tai khoan cu hoac dung quen mat khau.",
                    infoMessage = null
                )
            }
        }

        fun checkFirestoreUser() {
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { snapshot ->
                    val emailExists = snapshot.documents.any { it.id != USER_SCHEMA_SEED_ID }
                    if (emailExists) {
                        blockExistingEmail()
                    } else {
                        onAvailable()
                    }
                }
                .addOnFailureListener { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showRegisterPasswordRules = false,
                            errorMessage = throwable.localizedMessage ?: "Không thể kiểm tra email trên Firestore.",
                            infoMessage = null
                        )
                    }
                }
        }

        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                if (result.signInMethods.orEmpty().isNotEmpty()) {
                    blockExistingEmail()
                } else {
                    checkFirestoreUser()
                }
            }
            .addOnFailureListener {
                checkFirestoreUser()
            }
    }

    fun sendPasswordResetLink(email: String, onSuccess: () -> Unit) {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Vui lòng nhập email hợp lệ để đặt lại mật khẩu.",
                    infoMessage = null,
                    showRegisterPasswordRules = false
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                infoMessage = null,
                showRegisterPasswordRules = false,
                pendingPasswordResetEmail = normalizedEmail
            )
        }

        ensureResetEmailExists(normalizedEmail) { exists ->
            if (!exists) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Email này không tồn tại trong hệ thống.",
                        infoMessage = null,
                        pendingPasswordResetEmail = null
                    )
                }
                return@ensureResetEmailExists
            }

            auth.sendPasswordResetEmail(normalizedEmail)
                .addOnSuccessListener {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            infoMessage = "Đã gửi email đặt lại mật khẩu. Hãy lấy mã oobCode trong link email để xác nhận.",
                            pendingPasswordResetEmail = normalizedEmail,
                            pendingPasswordResetCode = null
                        )
                    }
                    onSuccess()
                }
                .addOnFailureListener { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.toAuthMessage("Không thể gửi email đặt lại mật khẩu."),
                            infoMessage = null
                        )
                    }
                }
        }
    }

    private fun ensureResetEmailExists(email: String, onResult: (Boolean) -> Unit) {
        fun checkFirestoreUser() {
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    onResult(snapshot.documents.any { it.id != USER_SCHEMA_SEED_ID })
                }
                .addOnFailureListener { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Không thể kiểm tra email trên Firestore.",
                            infoMessage = null
                        )
                    }
                }
        }

        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                if (result.signInMethods.orEmpty().isNotEmpty()) {
                    onResult(true)
                } else {
                    checkFirestoreUser()
                }
            }
            .addOnFailureListener {
                checkFirestoreUser()
            }
    }

    fun verifyPasswordResetCode(codeOrLink: String, onSuccess: () -> Unit) {
        val resetCode = codeOrLink.extractPasswordResetCode()
        if (resetCode.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Vui lòng nhập mã oobCode hoặc dán link trong email.",
                    infoMessage = null,
                    showRegisterPasswordRules = false
                )
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, infoMessage = null, showRegisterPasswordRules = false)
        }
        auth.verifyPasswordResetCode(resetCode)
            .addOnSuccessListener { email ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        infoMessage = "Mã xác nhận đúng cho email $email. Hãy nhập mật khẩu mới.",
                        pendingPasswordResetEmail = email,
                        pendingPasswordResetCode = resetCode
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Mã xác nhận không hợp lệ hoặc đã hết hạn."),
                        infoMessage = null
                    )
                }
            }
    }

    fun confirmVerifiedPasswordReset(password: String, repeatPassword: String, onSuccess: () -> Unit) {
        val resetCode = _uiState.value.pendingPasswordResetCode.orEmpty()
        if (resetCode.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Vui lòng xác nhận mã trong email trước khi đổi mật khẩu.",
                    infoMessage = null,
                    showRegisterPasswordRules = false
                )
            }
            return
        }
        if (!isValidPassword(password) || password != repeatPassword) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = if (password != repeatPassword) "Mật khẩu nhập lại không khớp." else null,
                    infoMessage = null,
                    showRegisterPasswordRules = true
                )
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, infoMessage = null, showRegisterPasswordRules = false)
        }
        auth.confirmPasswordReset(resetCode, password)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        infoMessage = "Đã đổi mật khẩu. Hãy đăng nhập bằng mật khẩu mới.",
                        showRegisterPasswordRules = false,
                        pendingPasswordResetEmail = null,
                        pendingPasswordResetCode = null
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.toAuthMessage("Không thể đổi mật khẩu."),
                        infoMessage = null
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
        avatarUrl: String?,
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
            avatarUrl = avatarUrl?.trim()?.takeIf { it.isNotBlank() }
        )
        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(userDocument(user, profile), SetOptions.merge())
            .addOnSuccessListener {
                ensureSocialDefaults(user.uid)
                deleteUserSchemaSeed()
                _uiState.update {
                    it.copy(
                        accountProfile = profile,
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

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null, showRegisterPasswordRules = false) }
    }

    fun showError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message, infoMessage = null) }
    }

    fun saveSearchQuery(query: String) {
        val user = auth.currentUser ?: return
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return

        val nextHistory = buildList {
            add(normalizedQuery)
            addAll(
                _uiState.value.searchHistory.filterNot {
                    it.equals(normalizedQuery, ignoreCase = true)
                }
            )
        }.take(5)

        _uiState.update { it.copy(searchHistory = nextHistory) }
        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(
                mapOf(
                    "searchHistory" to nextHistory,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
    }

    private fun handleAuthSuccess(user: FirebaseUser?, infoMessage: String?, onSuccess: () -> Unit) {
        upsertUserDocument(user)
        _uiState.update {
            it.copy(
                user = user?.toAuthUser(),
                isLoading = false,
                errorMessage = null,
                infoMessage = infoMessage
            )
        }
        user?.uid?.let(::loadUserProfile)
        onSuccess()
    }

    private fun upsertUserDocument(user: FirebaseUser?) {
        if (user == null) return
        firestore.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(authUserDocument(user), SetOptions.merge())
            .addOnSuccessListener {
                ensureUserDefaults(user)
                deleteUserSchemaSeed()
            }
    }

    private fun authUserDocument(user: FirebaseUser): Map<String, Any?> {
        return mapOf(
            "uid" to user.uid,
            "email" to user.email.takeIf { !it.isNullOrBlank() },
            "emailVerified" to user.isEmailVerified,
            "authProviders" to user.providerData.mapNotNull { it.providerId }.distinct().ifEmpty { null },
            "schemaVersion" to USER_SCHEMA_VERSION,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    private fun userDocument(user: FirebaseUser, profile: AccountProfile): Map<String, Any?> {
        val now = System.currentTimeMillis()
        return mapOf(
            "uid" to user.uid,
            "email" to user.email.takeIf { !it.isNullOrBlank() },
            "displayName" to profile.fullName.ifBlank { user.displayName.orEmpty() }.takeIf { it.isNotBlank() },
            "phone" to profile.phone.takeIf { it.isNotBlank() },
            "birthday" to profile.birthday.takeIf { it.isNotBlank() },
            "gender" to profile.gender.takeIf { it.isNotBlank() },
            "avatarUrl" to (profile.avatarUrl?.takeIf { it.isNotBlank() } ?: user.photoUrl?.toString()),
            "emailVerified" to user.isEmailVerified,
            "authProviders" to user.providerData.mapNotNull { it.providerId }.distinct().ifEmpty { null },
            "schemaVersion" to USER_SCHEMA_VERSION,
            "createdAt" to now,
            "updatedAt" to now
        )
    }

    private fun deleteUserSchemaSeed() {
        firestore.collection(USERS_COLLECTION)
            .document(USER_SCHEMA_SEED_ID)
            .delete()
    }

    private fun ensureSocialDefaults(uid: String) {
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.uid == uid) {
            ensureUserDefaults(currentUser)
            return
        }
        val userRef = firestore.collection(USERS_COLLECTION).document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val defaults = mutableMapOf<String, Any>()
            if (!snapshot.contains("followers")) defaults["followers"] = 0
            if (!snapshot.contains("following")) defaults["following"] = 0
            if (!snapshot.contains("followerIds")) defaults["followerIds"] = emptyList<String>()
            if (!snapshot.contains("followingIds")) defaults["followingIds"] = emptyList<String>()
            if (defaults.isNotEmpty()) {
                transaction.set(userRef, defaults, SetOptions.merge())
            }
            null
        }
    }

    private fun ensureUserDefaults(user: FirebaseUser) {
        val userRef = firestore.collection(USERS_COLLECTION).document(user.uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val defaults = mutableMapOf<String, Any?>()
            if (!snapshot.contains("displayName")) {
                defaults["displayName"] = user.displayName?.takeIf { it.isNotBlank() }
            }
            if (!snapshot.contains("phone")) defaults["phone"] = null
            if (!snapshot.contains("birthday")) defaults["birthday"] = null
            if (!snapshot.contains("gender")) defaults["gender"] = null
            if (!snapshot.contains("avatarUrl")) defaults["avatarUrl"] = user.photoUrl?.toString()
            if (!snapshot.contains("followers")) defaults["followers"] = 0
            if (!snapshot.contains("following")) defaults["following"] = 0
            if (!snapshot.contains("followerIds")) defaults["followerIds"] = emptyList<String>()
            if (!snapshot.contains("followingIds")) defaults["followingIds"] = emptyList<String>()
            if (!snapshot.contains("searchHistory")) defaults["searchHistory"] = emptyList<String>()
            if (!snapshot.contains("createdAt")) defaults["createdAt"] = System.currentTimeMillis()
            if (defaults.isNotEmpty()) {
                transaction.set(userRef, defaults, SetOptions.merge())
            }
            null
        }
    }

    private fun loadUserProfile(uid: String) {
        firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val profile = AccountProfile(
                    fullName = document.getString("displayName").orEmpty(),
                    phone = document.getString("phone").orEmpty(),
                    birthday = document.getString("birthday").orEmpty(),
                    gender = document.getString("gender").orEmpty(),
                    avatarUrl = document.getString("avatarUrl")
                )
                val searchHistory = (document.get("searchHistory") as? List<*>)
                    ?.mapNotNull { it as? String }
                    ?.take(5)
                    .orEmpty()
                _uiState.update { state ->
                    state.copy(
                        accountProfile = profile,
                        searchHistory = searchHistory,
                        user = state.user?.copy(displayName = profile.fullName.takeIf { it.isNotBlank() } ?: state.user.displayName)
                    )
                }
            }
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val USER_SCHEMA_VERSION = 1
        const val USER_SCHEMA_SEED_ID = "__user_schema_seed__"
    }
}

private fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName?.takeIf { it.isNotBlank() },
        emailVerified = isEmailVerified,
        providerIds = providerData.mapNotNull { it.providerId }.distinct()
    )
}

private fun isValidPassword(password: String): Boolean {
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasUppercase = password.any { it.isUpperCase() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    return password.length in 8..32 && hasLowercase && hasDigit && hasUppercase && hasSpecial
}

private fun String.emailFromJwt(): String? {
    return runCatching {
        val payload = split(".").getOrNull(1) ?: return@runCatching null
        val decoded = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
        JSONObject(decoded).optString("email").takeIf { it.isNotBlank() }
    }.getOrNull()
}

private fun String.extractPasswordResetCode(): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""
    return Regex("[?&]oobCode=([^&]+)")
        .find(trimmed)
        ?.groupValues
        ?.getOrNull(1)
        ?.takeIf { it.isNotBlank() }
        ?: trimmed
}

private fun Throwable.toAuthMessage(fallback: String): String {
    val code = (this as? FirebaseAuthException)?.errorCode
    return when (code) {
        "ERROR_CONFIGURATION_NOT_FOUND" ->
            "Firebase Auth chưa được cấu hình. Hãy bật Authentication > Sign-in method > Email/Password và Google trong Firebase console."
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
            "Email này đã được dùng. Hãy đăng nhập bằng email/mật khẩu trước rồi liên kết Google trong trang thông tin tài khoản."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Email này đã được dùng."
        "ERROR_INVALID_EMAIL" -> "Email không hợp lệ."
        "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu."
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Email hoặc mật khẩu không đúng."
        "ERROR_USER_NOT_FOUND" -> "Tài khoản không tồn tại."
        else -> localizedMessage ?: fallback
    }
}
