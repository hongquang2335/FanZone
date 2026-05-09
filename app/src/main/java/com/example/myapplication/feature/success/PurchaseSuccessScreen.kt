package com.example.myapplication.feature.success

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.core.designsystem.component.TicketCard
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.MintWash

@Composable
fun PurchaseSuccessScreen(
    ticket: TicketWalletItem?,
    onOpenWallet: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MintWash, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Evergreen, modifier = Modifier.size(40.dp))
            }
            Text("Mua ve thanh cong!", style = MaterialTheme.typography.headlineMedium)
            Text("Ve da duoc luu vao vi. Ban co the mo QR de check-in va nhan thong bao nhac lich truoc gio dien ra.")
            ticket?.let { TicketCard(item = it, onOpenEvent = {}) }
            Button(onClick = onOpenWallet, modifier = Modifier.fillMaxWidth()) { Text("Vao vi ve cua toi") }
            OutlinedButton(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) { Text("Ve trang chu") }
        }
    }
}

