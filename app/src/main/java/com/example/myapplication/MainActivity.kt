package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.app.FanZoneApp
import com.example.myapplication.core.designsystem.theme.FanZoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FanZoneTheme {
                FanZoneApp()
            }
        }
    }
}

