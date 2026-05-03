package com.example.myapplication.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.designsystem.theme.Evergreen
import com.example.myapplication.core.designsystem.theme.SoftText
import com.example.myapplication.core.navigation.BottomDestination

@Composable
fun AppBottomBar(
    items: List<BottomDestination>,
    currentRoute: String?,
    onSelect: (BottomDestination) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { destination ->
            BottomBarItem(
                destination = destination,
                selected = currentRoute == destination.route,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    destination: BottomDestination,
    selected: Boolean,
    onSelect: (BottomDestination) -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = { onSelect(destination) },
        icon = { Icon(destination.icon, contentDescription = destination.label) },
        label = { Text(destination.label, maxLines = 1) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lai")
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun BookingFooter(
    label: String,
    priceLabel: String?,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(shadowElevation = 10.dp, color = Color.White) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (priceLabel != null) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                    Text("Tong thanh toan", color = SoftText)
                    Text(
                        priceLabel,
                        style = MaterialTheme.typography.titleLarge,
                        color = Evergreen
                    )
                }
            }
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier.weight(if (priceLabel != null) 1.2f else 1f)
            ) {
                Text(label)
                if (enabled) {
                    Icon(
                        imageVector = if (priceLabel == null) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}

