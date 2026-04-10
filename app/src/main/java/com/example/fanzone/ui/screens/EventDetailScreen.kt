package com.example.fanzone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fanzone.ui.theme.Primary
import com.example.fanzone.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(onBack: () -> Unit, onSelectSeats: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("FANZONE", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Primary))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) }, // To balance center title
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Price starts from", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$45", style = MaterialTheme.typography.headlineLarge.copy(fontStyle = FontStyle.Italic))
                    }
                    Button(
                        onClick = onSelectSeats,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("SELECT SEATS", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Hero Image
            Box(modifier = Modifier.height(530.dp).fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                startY = 800f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        "NEON NIGHTS:",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp, fontStyle = FontStyle.Italic),
                        color = Color.White
                    )
                    Text(
                        "CITY REVIVAL",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp, fontStyle = FontStyle.Italic),
                        color = Primary
                    )
                }
            }

            // Event Info Cards
            Column(modifier = Modifier.padding(24.dp).offset(y = (-16).dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Primary.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Dec 24, 8:00 PM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
