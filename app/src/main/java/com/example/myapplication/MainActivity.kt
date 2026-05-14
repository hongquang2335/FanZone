package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.app.FanZoneApp
import com.example.myapplication.core.designsystem.theme.FanZoneTheme
import com.example.myapplication.util.FirestoreMigrator
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firestore
        val db = Firebase.firestore
        Log.d("FirestoreSetup", "Firestore initialized: $db")

        // Chạy migration dữ liệu (Có thể comment lại sau khi chạy xong 1 lần)
        //lifecycleScope.launch {
        //    FirestoreMigrator.migrateTicketData()
        //}

        setContent {
            FanZoneTheme {
                FanZoneApp()
            }
        }
    }
}
