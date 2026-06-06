package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.myapplication.app.FanZoneApp
import com.example.myapplication.core.designsystem.theme.FanZoneTheme
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        // Initialize Firestore
        val db = Firebase.firestore
        Log.d("FirestoreSetup", "Firestore initialized: $db")

        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }

            FanZoneTheme(darkTheme = darkTheme) {
                FanZoneApp(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it }
                )
            }
        }
    }
}

