package com.example.eventticket.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Auth : AppRoute("auth")
    data object Main : AppRoute("main")
    data object EventDetail : AppRoute("event/{eventId}") {
        fun create(eventId: String) = "event/$eventId"
    }
    data object EventCommunity : AppRoute("event/{eventId}/community") {
        fun create(eventId: String) = "event/$eventId/community"
    }
    data object PostDetail : AppRoute("post/{postId}") {
        fun create(postId: String) = "post/$postId"
    }
    data object Booking : AppRoute("booking/{eventId}") {
        fun create(eventId: String) = "booking/$eventId"
    }
}

sealed class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomTab("home", "Trang chủ", Icons.Default.Home)
    data object Community : BottomTab("community", "Cộng đồng", Icons.Default.Groups)
    data object Tickets : BottomTab("tickets", "Vé", Icons.Default.ConfirmationNumber)
    data object Profile : BottomTab("profile", "Hồ sơ", Icons.Default.AccountCircle)
}

val bottomTabs = listOf(
    BottomTab.Home,
    BottomTab.Community,
    BottomTab.Tickets,
    BottomTab.Profile
)
