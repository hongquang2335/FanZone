package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.app.FanZoneApp
import com.example.myapplication.core.designsystem.theme.FanZoneTheme
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firestore
        val db = Firebase.firestore
        Log.d("FirestoreSetup", "Firestore initialized: $db")

        // Chạy script mồi dữ liệu (xóa dòng này sau khi chạy xong)
        com.example.myapplication.feature.event.FirestoreSeeder.seedMissingFields()

        setContent {
            FanZoneTheme {
                FanZoneApp()
            }
        }
    }
}

