package com.example.fanzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fanzone.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(onBack: () -> Unit) {
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Create Post", style = MaterialTheme.typography.headlineSmall.copy(color = Primary, fontWeight = FontWeight.ExtraBold))
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Post", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
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
                .padding(24.dp)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("What's happening?", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), fontSize = 20.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )
        }
    }
}
