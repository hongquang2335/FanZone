package com.example.myapplication.feature.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.myapplication.R
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authState: AuthUiState,
    onClose: () -> Unit,
    onOpenRegister: () -> Unit,
    onLogin: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleLogin: (String) -> Unit,
    onGoogleLoginError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val canSubmit = email.isNotBlank() && password.isNotBlank()

    AuthScaffold(
        modifier = modifier,
        title = "Đăng nhập",
        navigation = {
            CircleHeaderButton(icon = Icons.Default.Close, contentDescription = "Đóng", onClick = onClose)
        },
        mascot = false
    ) {
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Nhập email",
            trailingIcon = {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7E7E86))
            },
            keyboardType = KeyboardType.Email
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Nhập mật khẩu",
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF7E7E86)
                    )
                }
            },
            keyboardType = KeyboardType.Password
        )
        Button(
            onClick = { onLogin(email, password) },
            enabled = canSubmit && !authState.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(if (authState.isLoading) "Đang xử lý..." else "Đăng nhập", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        GoogleSignInButton(
            enabled = !authState.isLoading,
            onGoogleLogin = onGoogleLogin,
            onGoogleLoginError = onGoogleLoginError
        )
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
        Text(
            text = "Quên mật khẩu?",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !authState.isLoading
                ) { onForgotPassword() }
                .padding(top = 10.dp),
            color = Color(0xFF7D7B82),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Tạo tài khoản ngay",
            modifier = Modifier.fillMaxWidth().clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpenRegister
            ).padding(top = 8.dp),
            color = Evergreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GoogleSignInButton(
    enabled: Boolean,
    onGoogleLogin: (String) -> Unit,
    onGoogleLoginError: (String) -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Tiếp tục với Google"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val googleWebClientId = stringResource(R.string.google_web_client_id)

    OutlinedButton(
        onClick = {
            if (googleWebClientId.startsWith("YOUR_WEB_CLIENT_ID")) {
                onGoogleLoginError("Thiếu Google Web Client ID. Hãy cập nhật google_web_client_id trong strings.xml.")
                return@OutlinedButton
            }
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val playServicesStatus = googleApiAvailability.isGooglePlayServicesAvailable(context)
            if (playServicesStatus != ConnectionResult.SUCCESS) {
                onGoogleLoginError("Google Play Services chua s?n s�ng tr�n thi?t b? n�y. H�y c?p nh?t Google Play Services ho?c d�ng emulator c� Play Store.")
                return@OutlinedButton
            }
            scope.launch {
                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(googleWebClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = credentialManager.getCredential(context, request)
                    val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                    onGoogleLogin(googleCredential.idToken)
                } catch (_: GetCredentialCancellationException) {
                    onGoogleLoginError("Bạn đã hủy đăng nhập Google.")
                } catch (throwable: GetCredentialException) {
                    onGoogleLoginError(throwable.toGoogleSignInMessage())
                } catch (throwable: Throwable) {
                    onGoogleLoginError(throwable.toGoogleSignInMessage())
                }
            }
        },
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFD9D7E0))
    ) {
        Text(text, color = Color(0xFF232323), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

private fun Throwable.toGoogleSignInMessage(): String {
    val detail = localizedMessage.orEmpty()
    val normalized = detail.lowercase()
    if (
        normalized.contains("developer console") ||
        normalized.contains("not set up correctly") ||
        normalized.contains("10:") ||
        normalized.contains("configuration")
    ) {
        return "Google Sign-In chưa sẵn sàng. Cần thêm Android OAuth client cho com.example.myapplication với SHA-1 debug trong Google Cloud/Firebase rồi tải lại google-services.json."
    }
    return detail.ifBlank { "Không thể đăng nhập Google." }
}

@Composable
fun RegisterScreen(
    authState: AuthUiState,
    onBack: () -> Unit,
    onOpenLogin: () -> Unit,
    onRegister: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatVisible by remember { mutableStateOf(false) }
    val canSubmit = email.isNotBlank() && password.isNotBlank() && repeatPassword.isNotBlank()

    AuthScaffold(
        modifier = modifier,
        title = "Đăng ký tài khoản",
        navigation = {
            CircleHeaderButton(icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", onClick = onBack)
        },
        compactHeader = false
    ) {
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Nhập email của bạn",
            keyboardType = KeyboardType.Email
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Nhập mật khẩu",
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF7E7E86)
                    )
                }
            },
            keyboardType = KeyboardType.Password
        )
        AuthTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            placeholder = "Nhập lại mật khẩu",
            visualTransformation = if (repeatVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { repeatVisible = !repeatVisible }) {
                    Icon(
                        if (repeatVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF7E7E86)
                    )
                }
            },
            keyboardType = KeyboardType.Password
        )
        if (authState.showRegisterPasswordRules) {
            PasswordRules()
        }
        Button(
            onClick = { onRegister(email, password, repeatPassword) },
            enabled = canSubmit && !authState.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(if (authState.isLoading) "Đang xử lý..." else "Tiếp tục", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
        Text(
            text = "Đăng nhập ngay",
            modifier = Modifier.fillMaxWidth().clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpenLogin
            ).padding(top = 10.dp),
            color = Evergreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ForgotPasswordScreen(
    authState: AuthUiState,
    onBack: () -> Unit,
    onSendResetLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    val canSubmit = email.isNotBlank() && !authState.isLoading

    AuthScaffold(
        modifier = modifier,
        title = "Quên mật khẩu",
        navigation = {
            CircleHeaderButton(icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", onClick = onBack)
        },
        compactHeader = false
    ) {
        Text(
            text = "Nhập email tài khoản. App sẽ kiểm tra email tồn tại rồi gửi email đặt lại mật khẩu. Sau đó bạn nhập mã oobCode trong link email.",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF5B5961),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Nhập email",
            keyboardType = KeyboardType.Email
        )
        Button(
            onClick = { onSendResetLink(email) },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(if (authState.isLoading) "Đang gửi..." else "Gửi email đặt lại mật khẩu", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
    }
}

@Composable
fun ResetPasswordCodeScreen(
    authState: AuthUiState,
    onBack: () -> Unit,
    onVerifyCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var codeOrLink by remember { mutableStateOf("") }

    AuthScaffold(
        modifier = modifier,
        title = "Xác nhận mã",
        navigation = {
            CircleHeaderButton(icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", onClick = onBack)
        },
        compactHeader = false
    ) {
        Text(
            text = "Mở email đặt lại mật khẩu, copy link hoặc riêng phần oobCode rồi dán vào đây.",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF5B5961),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        AuthTextField(
            value = codeOrLink,
            onValueChange = { codeOrLink = it },
            placeholder = "Nhập link email hoặc mã oobCode"
        )
        Button(
            onClick = { onVerifyCode(codeOrLink) },
            enabled = codeOrLink.isNotBlank() && !authState.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(if (authState.isLoading) "Đang kiểm tra..." else "Xác nhận mã", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
    }
}

@Composable
fun NewPasswordScreen(
    authState: AuthUiState,
    onBack: () -> Unit,
    onConfirmPasswordReset: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatVisible by remember { mutableStateOf(false) }
    val canSubmit = password.isNotBlank() && repeatPassword.isNotBlank() && !authState.isLoading

    AuthScaffold(
        modifier = modifier,
        title = "Mật khẩu mới",
        navigation = {
            CircleHeaderButton(icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", onClick = onBack)
        },
        compactHeader = false
    ) {
        Text(
            text = "Mã đã được xác nhận. Hãy tạo mật khẩu mới cho tài khoản.",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF5B5961),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Nhập mật khẩu mới",
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF7E7E86))
                }
            },
            keyboardType = KeyboardType.Password
        )
        AuthTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            placeholder = "Nhập lại mật khẩu mới",
            visualTransformation = if (repeatVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { repeatVisible = !repeatVisible }) {
                    Icon(if (repeatVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF7E7E86))
                }
            },
            keyboardType = KeyboardType.Password
        )
        if (authState.showRegisterPasswordRules) {
            PasswordRules()
        }
        Button(
            onClick = { onConfirmPasswordReset(password, repeatPassword) },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(if (authState.isLoading) "Đang đổi..." else "Đổi mật khẩu", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
    }
}

@Composable
private fun AuthScaffold(
    title: String,
    navigation: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    compactHeader: Boolean = false,
    mascot: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        val compactHeight = maxHeight < 720.dp
        val horizontalPadding = if (maxWidth < 360.dp) 20.dp else 34.dp
        val headerHeight = when {
            compactHeader -> if (compactHeight) 112.dp else 142.dp
            compactHeight -> 142.dp
            else -> 156.dp
        }
        val contentSpacing = if (compactHeight) 12.dp else 16.dp
        val titleStyle = MaterialTheme.typography.headlineMedium
        val shouldScroll = maxHeight < if (compactHeader) 760.dp else 700.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (shouldScroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(Evergreen)
                    .statusBarsPadding()
            ) {
                Box(modifier = Modifier.align(Alignment.TopStart).padding(horizontalPadding)) {
                    navigation()
                }
                Text(
                    text = title,
                    modifier = Modifier
                        .align(if (compactHeader) Alignment.Center else Alignment.BottomCenter)
                        .padding(
                            start = 0.dp,
                            bottom = if (compactHeader) 0.dp else 18.dp
                        ),
                    color = Color.White,
                    style = titleStyle,
                    fontWeight = FontWeight.ExtraBold
                )
                if (mascot) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = horizontalPadding + 42.dp).size(78.dp),
                        shape = CircleShape,
                        color = Color(0xFFFFC83D),
                        border = BorderStroke(2.dp, Color(0xFF5E5E5E).copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = if (compactHeight) 16.dp else 28.dp),
                verticalArrangement = Arrangement.spacedBy(contentSpacing),
                content = content
            )
            if (!shouldScroll) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CircleHeaderButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(38.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().height(56.dp),
        placeholder = { Text(placeholder, color = Color(0xFFB8B8C0), fontSize = 14.sp) },
        singleLine = true,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF8E8E93),
            unfocusedBorderColor = Color(0xFF9E9EA3),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun PasswordRules() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFAF7FF),
        border = BorderStroke(1.dp, Color(0xFFE95868))
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mật khẩu chưa hợp lệ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            listOf(
                "Tu 8 - 32 ky tu",
                "Bao gom chu thuong va so",
                "Bao gom ky tu dac biet (!,$,@,%,...)",
                "Co it nhat 1 ky tu in hoa"
            ).forEach { rule ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = Color(0xFFE95868), modifier = Modifier.size(16.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("x", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Text(rule, color = Color(0xFF333238), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun AuthMessage(error: String?, info: String?) {
    val message = error ?: info ?: return
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth(),
        color = if (error != null) Color(0xFFE95868) else Evergreen,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )
}


