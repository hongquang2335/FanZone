package com.example.myapplication.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.feature.authentication.AccountProfile
import com.example.myapplication.feature.authentication.AuthUiState
import com.example.myapplication.feature.authentication.AuthUser
import com.example.myapplication.feature.authentication.GoogleSignInButton
import java.util.Calendar

@Composable
fun AccountInfoScreen(
    authUser: AuthUser?,
    accountProfile: AccountProfile,
    authState: AuthUiState,
    onSave: (String, String, String, String, String?, () -> Unit) -> Unit,
    onLinkGoogle: (String) -> Unit,
    onGoogleError: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storageDataSource = remember(context) { AppDependencies.communityStorageDataSource(context) }
    val screenBackground = Color.White
    val primaryText = Color(0xFF232323)
    val secondaryText = Color(0xFF5B5961)
    val fieldBackground = Color(0xFFF8F7FC)
    val fieldBorder = Color(0xFFD9D7E0)
    var fullName by remember(authUser?.uid, accountProfile.fullName) { mutableStateOf(accountProfile.fullName.ifBlank { authUser?.displayName.orEmpty() }) }
    var phone by remember(authUser?.uid, accountProfile.phone) { mutableStateOf(accountProfile.phone) }
    var birthday by remember(authUser?.uid, accountProfile.birthday) { mutableStateOf(accountProfile.birthday) }
    var gender by remember(authUser?.uid, accountProfile.gender) { mutableStateOf(accountProfile.gender) }
    var avatarUrl by remember(authUser?.uid, accountProfile.avatarUrl) { mutableStateOf(accountProfile.avatarUrl) }
    var isUploadingAvatar by remember { mutableStateOf(false) }
    var avatarUploadError by remember { mutableStateOf<String?>(null) }
    var showBirthdayPicker by remember { mutableStateOf(false) }
    val initial = fullName.firstOrNull() ?: authUser?.email?.firstOrNull()
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        isUploadingAvatar = true
        avatarUploadError = null
        val mediaType = context.contentResolver.getType(uri) ?: "image/jpeg"
        storageDataSource.uploadCommunityMedia(
            mediaUri = uri,
            mediaType = mediaType,
            onSuccess = { url, _ ->
                avatarUrl = url
                isUploadingAvatar = false
            },
            onError = { throwable ->
                avatarUploadError = throwable.localizedMessage ?: "Không thể tải ảnh đại diện."
                isUploadingAvatar = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
            .navigationBarsPadding()
    ) {
        SettingsHeader(title = "Thông tin tài khoản", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(112.dp).clickable { avatarPicker.launch(arrayOf("image/*")) },
                    shape = CircleShape,
                    color = Color(0xFF078E81)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (!avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Ảnh đại diện",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (initial != null) {
                            Text(
                                text = initial.uppercaseChar().toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.displayMedium
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(58.dp))
                        }
                    }
                }
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(start = 76.dp).size(34.dp).clickable {
                        avatarPicker.launch(arrayOf("image/*"))
                    },
                    shape = CircleShape,
                    color = Evergreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            if (isUploadingAvatar) {
                Text(
                    text = "Đang tải ảnh đại diện...",
                    modifier = Modifier.fillMaxWidth(),
                    color = Evergreen,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Cung cấp thông tin chính xác sẽ hỗ trợ bạn trong quá trình mua vé, hoặc khi cần xác thực vé",
                modifier = Modifier.fillMaxWidth(),
                color = secondaryText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            FieldLabel("Họ và tên", color = primaryText)
            ProfileTextField(value = fullName, onValueChange = { fullName = it }, borderColor = fieldBorder, containerColor = fieldBackground)

            FieldLabel("Số điện thoại", color = primaryText)
            ProfileTextField(
                value = phone,
                onValueChange = { value -> phone = value.filter { it.isDigit() }.take(10) },
                keyboardType = KeyboardType.Phone,
                borderColor = fieldBorder,
                containerColor = fieldBackground
            )

            FieldLabel("Email", color = primaryText)
            ProfileTextField(
                value = authUser?.email.orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                borderColor = fieldBorder,
                containerColor = fieldBackground,
                trailingIcon = {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Evergreen)
                }
            )

            FieldLabel("Ngày sinh *", color = primaryText)
            ProfileTextField(
                value = birthday,
                onValueChange = {},
                readOnly = true,
                onClick = { showBirthdayPicker = true },
                borderColor = fieldBorder,
                containerColor = fieldBackground,
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Evergreen)
                }
            )

            FieldLabel("Giới tính", color = primaryText)
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf("Nam", "Nữ", "Khác").forEach { option ->
                    Row(
                        modifier = Modifier.clickable { gender = option },
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = gender == option,
                            onClick = { gender = option },
                            colors = RadioButtonDefaults.colors(selectedColor = Evergreen)
                        )
                        Text(option, color = primaryText, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            AuthInlineMessage(error = authState.errorMessage ?: avatarUploadError, info = authState.infoMessage)

            Button(
                onClick = { onSave(fullName, phone, birthday, gender, avatarUrl, onBack) },
                enabled = !authState.isLoading && !isUploadingAvatar,
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Evergreen,
                    disabledContainerColor = Evergreen.copy(alpha = 0.38f),
                    disabledContentColor = Color.White.copy(alpha = 0.78f)
                )
            ) {
                Text(
                    when {
                        isUploadingAvatar -> "Đang tải ảnh..."
                        authState.isLoading -> "Đang lưu..."
                        else -> "Hoàn thành"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showBirthdayPicker) {
        BirthdayPickerSheet(
            initialValue = birthday,
            onDismiss = { showBirthdayPicker = false },
            onApply = { selectedDate ->
                birthday = selectedDate
                showBirthdayPicker = false
            }
        )
    }
}

@Composable
fun NotificationSettingsScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enabled by remember { mutableStateOf(true) }
    val screenBackground = if (darkTheme) Color.Black else Color(0xFFF3F5F7)
    val panelBackground = if (darkTheme) Color(0xFF3A3940) else Color.White
    val primaryText = if (darkTheme) Color.White else Color(0xFF232323)
    val secondaryText = if (darkTheme) Color.White else Color(0xFF5B5961)

    Column(
        modifier = modifier.fillMaxSize().background(screenBackground).navigationBarsPadding()
    ) {
        SettingsHeader(title = "Cài đặt", onBack = onBack)
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 54.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bật thông báo để không bỏ lỡ các cập nhật mới nhất về đơn hàng và sự kiện",
                color = secondaryText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            SettingsSwitchRow(
                title = "Thông báo",
                checked = enabled,
                onCheckedChange = { enabled = it },
                panelBackground = panelBackground,
                textColor = primaryText
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    panelBackground: Color,
    textColor: Color,
    value: String? = null
) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = panelBackground) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (value != null) {
                    Text(value, color = textColor.copy(alpha = 0.72f), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Evergreen
                )
            )
        }
    }
}

@Composable
private fun SettingsHeader(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .background(Evergreen)
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp).size(46.dp).clip(CircleShape).clickable(onClick = onBack),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FieldLabel(text: String, color: Color = Color.White) {
    Text(text, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onClick: (() -> Unit)? = null,
    borderColor: Color = Color.White,
    containerColor: Color = Color(0xFFF8F7FC),
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxWidth().height(58.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            readOnly = readOnly,
            enabled = enabled,
            singleLine = true,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Evergreen,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                disabledTextColor = Color(0xFF6B6870),
                disabledBorderColor = borderColor
            )
        )
        if (onClick != null) {
            Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))
        }
    }
}

@Composable
private fun BirthdayPickerSheet(
    initialValue: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    val initialDate = remember(initialValue) { parseBirthday(initialValue) ?: defaultBirthday() }
    var selectedDate by remember(initialValue) { mutableStateOf(initialDate.copyCalendar()) }
    var displayedMonth by remember(initialValue) {
        mutableStateOf(initialDate.copyCalendar().apply { set(Calendar.DAY_OF_MONTH, 1) })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.56f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                color = Color.White
            ) {
                Column {
                    CalendarHeader(
                        displayedMonth = displayedMonth,
                        onPreviousYear = { displayedMonth = displayedMonth.moved(years = -1) },
                        onPreviousMonth = { displayedMonth = displayedMonth.moved(months = -1) },
                        onNextMonth = { displayedMonth = displayedMonth.moved(months = 1) },
                        onNextYear = { displayedMonth = displayedMonth.moved(years = 1) }
                    )
                    WeekdayHeader()
                    CalendarMonthGrid(
                        displayedMonth = displayedMonth,
                        selectedDate = selectedDate,
                        onSelectDate = { selectedDate = it }
                    )
                    Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = Color(0xFFE0E0E0)) {}
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f).height(54.dp).clickable(onClick = onDismiss),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Evergreen)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Hủy", color = Evergreen, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clickable { onApply(formatBirthday(selectedDate)) },
                            shape = RoundedCornerShape(8.dp),
                            color = Evergreen
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Áp dụng", color = Color.White, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    displayedMonth: Calendar,
    onPreviousYear: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNextYear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarNavButton("<<", onPreviousYear)
        CalendarNavButton("<", onPreviousMonth)
        Text(
            text = "Tháng ${displayedMonth.get(Calendar.MONTH) + 1}, ${displayedMonth.get(Calendar.YEAR)}",
            modifier = Modifier.weight(1f),
            color = Color(0xFF232323),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        CalendarNavButton(">", onNextMonth)
        CalendarNavButton(">>", onNextYear)
    }
}

@Composable
private fun CalendarNavButton(text: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(42.dp)) {
        Text(text, color = Evergreen, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp)) {
        listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                color = Color(0xFF77777D),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    displayedMonth: Calendar,
    selectedDate: Calendar,
    onSelectDate: (Calendar) -> Unit
) {
    val days = remember(displayedMonth.timeInMillis) { monthGridDays(displayedMonth) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 20.dp)) {
        repeat(6) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { column ->
                    val date = days[row * 7 + column]
                    val inMonth = date.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH) &&
                        date.get(Calendar.YEAR) == displayedMonth.get(Calendar.YEAR)
                    val selected = sameDate(date, selectedDate)
                    Box(
                        modifier = Modifier.weight(1f).height(58.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp).clickable { onSelectDate(date.copyCalendar()) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) Evergreen else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = date.get(Calendar.DAY_OF_MONTH).toString(),
                                    color = when {
                                        selected -> Color.White
                                        inMonth -> Color(0xFF2E2E33)
                                        else -> Color(0xFFC6C6CC)
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(58.dp))
    }
}

private fun parseBirthday(value: String): Calendar? {
    val parts = value.trim().split("/", "-", ".")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return Calendar.getInstance().apply {
        isLenient = false
        set(year, month - 1, day, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
        runCatching { time }.getOrNull() ?: return null
    }
}

private fun defaultBirthday(): Calendar = Calendar.getInstance().apply {
    add(Calendar.YEAR, -18)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun formatBirthday(date: Calendar): String {
    val day = date.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val month = (date.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    return "$day/$month/${date.get(Calendar.YEAR)}"
}

private fun monthGridDays(displayedMonth: Calendar): List<Calendar> {
    val firstDay = displayedMonth.copyCalendar().apply { set(Calendar.DAY_OF_MONTH, 1) }
    val startOffset = (firstDay.get(Calendar.DAY_OF_WEEK) + 5) % 7
    val start = firstDay.copyCalendar().apply { add(Calendar.DAY_OF_MONTH, -startOffset) }
    return List(42) { index -> start.copyCalendar().apply { add(Calendar.DAY_OF_MONTH, index) } }
}

private fun Calendar.moved(months: Int = 0, years: Int = 0): Calendar = copyCalendar().apply {
    add(Calendar.YEAR, years)
    add(Calendar.MONTH, months)
    set(Calendar.DAY_OF_MONTH, 1)
}

private fun Calendar.copyCalendar(): Calendar = (clone() as Calendar)

private fun sameDate(first: Calendar, second: Calendar): Boolean =
    first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.MONTH) == second.get(Calendar.MONTH) &&
        first.get(Calendar.DAY_OF_MONTH) == second.get(Calendar.DAY_OF_MONTH)

@Composable
private fun AuthInlineMessage(error: String?, info: String?) {
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
