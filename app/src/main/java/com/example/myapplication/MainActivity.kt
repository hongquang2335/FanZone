package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication.ui.screens.home.EventDetailScreen
import com.example.myapplication.ui.screens.home.HomeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var selectedEventId by remember { mutableStateOf<String?>(null) }

                val eventId = selectedEventId
                if (eventId == null) {
                    HomeScreen(onEventClick = { selectedEventId = it })
                } else {
                    EventDetailScreen(
                        eventId = eventId,
                        onBack = { selectedEventId = null },
                        onBook = { selectedEventId = null }
                    )
                }
            }
        }
    }
}
