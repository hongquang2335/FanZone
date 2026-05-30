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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.core.designsystem.theme.Evergreen
import java.util.Calendar

@Composable
fun AccountInfoScreen(
    authUser: AuthUser?,
    accountProfile: AccountProfile,
    authState: AuthUiState,
    onSave: (String, String, String, String, () -> Unit) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkBackground = Color(0xFF232323)
    var fullName by remember(authUser?.uid, accountProfile.fullName) { mutableStateOf(accountProfile.fullName.ifBlank { authUser?.displayName.orEmpty() }) }
    var phone by remember(authUser?.uid, accountProfile.phone) { mutableStateOf(accountProfile.phone) }
    var birthday by remember(authUser?.uid, accountProfile.birthday) { mutableStateOf(accountProfile.birthday) }
    var gender by remember(authUser?.uid, accountProfile.gender) { mutableStateOf(accountProfile.gender) }
    var showBirthdayPicker by remember { mutableStateOf(false) }
    val initial = fullName.firstOrNull() ?: authUser?.email?.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
            .navigationBarsPadding()
    ) {
        SettingsHeader(title = "Thong tin tai khoan", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(112.dp), shape = CircleShape, color = Color(0xFF078E81)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (initial != null) {
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
                    modifier = Modifier.align(Alignment.BottomCenter).padding(start = 76.dp).size(34.dp),
                    shape = CircleShape,
                    color = Evergreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Text(
                text = "Cung cap thong tin chinh xac se ho tro ban trong qua trinh mua ve, hoac khi can xac thuc ve",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            FieldLabel("Ho va ten")
            ProfileTextField(value = fullName, onValueChange = { fullName = it })

            FieldLabel("So dien thoai")
            ProfileTextField(
                value = phone,
                onValueChange = { value -> phone = value.filter { it.isDigit() }.take(10) },
                keyboardType = KeyboardType.Phone
            )

            FieldLabel("Email")
            ProfileTextField(
                value = authUser?.email.orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                trailingIcon = {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Evergreen)
                }
            )

            FieldLabel("Ngay sinh *")
            ProfileTextField(
                value = birthday,
                onValueChange = {},
                readOnly = true,
                onClick = { showBirthdayPicker = true },
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Evergreen)
                }
            )

            FieldLabel("Gioi tinh")
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf("Nam", "Nu", "Khac").forEach { option ->
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
                        Text(option, color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            AuthInlineMessage(error = authState.errorMessage, info = authState.infoMessage)

            Button(
                onClick = { onSave(fullName, phone, birthday, gender, onBack) },
                enabled = !authState.isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Evergreen)
            ) {
                Text(if (authState.isLoading) "Dang luu..." else "Hoan thanh", style = MaterialTheme.typography.titleMedium)
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
fun PinSetupScreen(
    authState: AuthUiState,
    onSavePin: (String, () -> Unit) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    val canSubmit = pin.length == 6

    Column(
        modifier = modifier.fillMaxSize().background(Color.White).navigationBarsPadding()
    ) {
        SettingsHeader(title = "Thiet lap ma PIN", onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 78.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "Tao ma PIN khi truy cap trang \"Chi tiet ve\" de tang bao mat cho ve cua ban",
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Vui long khong chia se ma PIN voi nguoi khac",
                color = Color(0xFFE58A28),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            BasicTextField(
                value = pin,
                onValueChange = { value -> pin = value.filter { it.isDigit() }.take(6) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
                decorationBox = {
                    Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                        repeat(6) { index ->
                            Surface(
                                modifier = Modifier.size(20.dp),
                                shape = CircleShape,
                                color = if (index < pin.length) Evergreen else Color(0xFFE0E0E0)
                            ) {}
                        }
                    }
                }
            )
            Box(modifier = Modifier.weight(1f))
            Button(
                onClick = { onSavePin(pin, onBack) },
                enabled = canSubmit && !authState.isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Evergreen,
                    disabledContainerColor = Color(0xFFE8ECF7),
                    disabledContentColor = Color.White
                )
            ) {
                Text(if (authState.isLoading) "Dang luu..." else "Tiep tuc", style = MaterialTheme.typography.titleMedium)
            }
            AuthInlineMessage(error = authState.errorMessage, info = authState.infoMessage)
        }
    }
}

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize().background(Color.Black).navigationBarsPadding()
    ) {
        SettingsHeader(title = "Cai dat thong bao", onBack = onBack)
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 54.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bat thong bao de khong bo lo cac cap nhat moi nhat ve don hang va su kien",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Color(0xFF3A3940)) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thong bao", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Evergreen
                        )
                    )
                }
            }
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
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp).size(46.dp).clickable(onClick = onBack),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lai", tint = Color.White, modifier = Modifier.size(28.dp))
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
private fun FieldLabel(text: String) {
    Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
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
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedContainerColor = Color(0xFFF8F7FC),
                unfocusedContainerColor = Color(0xFFF8F7FC),
                disabledContainerColor = Color(0xFF6F6E75),
                disabledTextColor = Color(0xFFC8C6CE),
                disabledBorderColor = Color(0xFFC8C6CE)
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
                                Text("Huy", color = Evergreen, style = MaterialTheme.typography.titleMedium)
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
                                Text("Ap dung", color = Color.White, style = MaterialTheme.typography.titleMedium)
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
            text = "Thang ${displayedMonth.get(Calendar.MONTH) + 1}, ${displayedMonth.get(Calendar.YEAR)}",
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
