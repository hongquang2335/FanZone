package com.example.eventticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eventticket.ui.FanZoneApp
import com.example.eventticket.ui.theme.EventTicketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventTicketTheme {
                FanZoneApp()
            }
        }
    }
}
