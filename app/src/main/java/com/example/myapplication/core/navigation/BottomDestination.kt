package com.example.myapplication.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomDestinations = listOf(
    BottomDestination(AppDestination.Home.route, "Trang chủ", Icons.Default.Home),
    BottomDestination(AppDestination.Community.route, "Cộng đồng", Icons.Default.ChatBubbleOutline),
    BottomDestination(AppDestination.Tickets.route, "Vé của tôi", Icons.Default.ConfirmationNumber),
    BottomDestination(AppDestination.Profile.route, "Tài khoản", Icons.Default.AccountCircle)
)
