package com.example.myapplication.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.UserProfile
import com.example.myapplication.core.designsystem.component.CircleAvatar
import com.example.myapplication.core.designsystem.component.ProfileActionRow
import com.example.myapplication.core.designsystem.component.SectionHeader
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftText

@Composable
fun ProfileScreen(
    user: UserProfile,
    unreadSupport: Int,
    onOpenSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isExpanded = maxWidth >= 720.dp
        val entries = listOf(
            "Thong tin ca nhan" to "Cap nhat email, so dien thoai, dia chi",
            "Thong bao" to "Nhan canh bao flash sale va su kien quan tam",
            "Lich su thanh toan" to "Kiem tra hoa don, giao dich va hoan tien",
            "Ho tro truc tuyen" to if (unreadSupport > 0) "$unreadSupport tin nhan chua doc" else "Mo trung tam tro giup"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleAvatar(size = 64.dp)
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(user.membership, color = Evergreen)
                            Text(user.city, color = SoftText)
                        }
                        AssistChip(onClick = {}, label = { Text("Premium") })
                    }
                }
            }
            item { SectionHeader("Tai khoan", "Quan ly ho so, thanh toan va ho tro") }
            if (isExpanded) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        entries.chunked(2).forEach { group ->
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                group.forEach { (title, subtitle) ->
                                    ProfileActionRow(title, subtitle, title == "Ho tro truc tuyen") {
                                        if (title == "Ho tro truc tuyen") onOpenSupport()
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                items(entries) { (title, subtitle) ->
                    ProfileActionRow(title, subtitle, title == "Ho tro truc tuyen") {
                        if (title == "Ho tro truc tuyen") onOpenSupport()
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
                ) {
                    Text("Dang xuat")
                }
            }
        }
    }
}

