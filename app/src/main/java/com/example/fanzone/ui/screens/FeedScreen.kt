package com.example.fanzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fanzone.ui.theme.Primary
import com.example.fanzone.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(onCreatePost: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "The Pulse",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePost,
                shape = CircleShape,
                containerColor = Color.Transparent,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(Primary, Secondary)),
                        shape = CircleShape
                    )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.Black, modifier = Modifier.size(36.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Feed content goes here...", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
