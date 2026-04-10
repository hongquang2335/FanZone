package com.example.fanzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fanzone.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "Alex Rivers",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Text(
                "@rivers_vibe",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )
        }
    }
}
