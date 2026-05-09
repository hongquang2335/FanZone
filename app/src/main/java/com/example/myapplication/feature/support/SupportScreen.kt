package com.example.myapplication.feature.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.SupportShortcut
import com.example.myapplication.core.designsystem.component.AppTopBar
import com.example.myapplication.core.designsystem.component.MessageBubble

@Composable
fun SupportScreen(
    supportShortcuts: List<SupportShortcut>,
    unreadSupport: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppTopBar(title = "Trung tam ho tro", onBack = onBack) },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Tro ly FanZone", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                        Text("Ban dang co $unreadSupport hoi thoai dang xu ly. Chon mot loi nhac nhanh ben duoi hoac tiep tuc chat.")
                        androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            supportShortcuts.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { shortcut ->
                                        androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f, fill = false)) {
                                            AssistChip(onClick = {}, label = { Text(shortcut.title) })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { MessageBubble("He thong", "Don hang #FZ-2481 cua ban da duoc xac nhan. Ban can them hoa don VAT khong?", false) }
            item { MessageBubble("Ban", "Toi can huong dan lay hoa don VAT va cach check-in tai cong vao.", true) }
            item { MessageBubble("FanZone", "Ban co the tao yeu cau VAT trong muc lich su thanh toan. Ve check-in se nam trong Vi ve cua toi va QR hoat dong 24 gio truoc su kien.", false) }
        }
    }
}

