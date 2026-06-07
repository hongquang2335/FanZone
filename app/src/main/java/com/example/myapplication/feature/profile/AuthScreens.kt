package com.example.myapplication.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.core.designsystem.theme.Evergreen

@Composable
fun LoginScreen(
    authState: AuthUiState,
    onClose: () -> Unit,
    onOpenRegister: () -> Unit,
    onLogin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val canSubmit = account.isNotBlank() && password.isNotBlank()

    AuthScaffold(
        modifier = modifier,
        title = "Đăng nhập",
        navigation = {
            CircleHeaderButton(icon = Icons.Default.Close, contentDescription = "Đóng", onClick = onClose)
        },
        mascot = false
    ) {
        AuthTextField(
            value = account,
            onValueChange = { account = it },
            placeholder = "Nhập email hoặc số điện thoại",
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
            onClick = { onLogin(account, password) },
            enabled = canSubmit && !authState.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Evergreen,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8C8B92)
            )
        ) {
            Text(if (authState.isLoading) "Đang xử lý..." else "Đăng nhập", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
        Text(
            text = "Quên mật khẩu?",
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            color = Color(0xFF7D7B82),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Chưa có tài khoản?",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFAAA8AF),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tạo tài khoản ngay",
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenRegister).padding(top = 8.dp),
            color = Evergreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
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
            )
        ) {
            Text(if (authState.isLoading) "Đang xử lý..." else "Tiếp tục", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        AuthMessage(error = authState.errorMessage, info = authState.infoMessage)
        Text(
            text = "Đã có tài khoản?",
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            color = Color(0xFFAAA8AF),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Đăng nhập ngay",
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenLogin).padding(top = 8.dp),
            color = Evergreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
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
            TermsText(horizontalPadding = horizontalPadding)
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

@Composable
private fun TermsText(horizontalPadding: androidx.compose.ui.unit.Dp) {
    Text(
        text = "Bang viec tiep tuc, ban da doc va dong y voi Dieu khoan su dung va Chinh sach bao mat thong tin ca nhan cua Ticketbox",
        modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = 18.dp),
        color = Color(0xFF222222),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start
    )
}
