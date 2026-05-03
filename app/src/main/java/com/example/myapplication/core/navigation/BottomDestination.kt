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
    BottomDestination(AppDestination.Home.route, "Trang chu", Icons.Default.Home),
    BottomDestination(AppDestination.Community.route, "Cong dong", Icons.Default.ChatBubbleOutline),
    BottomDestination(AppDestination.Tickets.route, "Ve cua toi", Icons.Default.ConfirmationNumber),
    BottomDestination(AppDestination.Profile.route, "Tai khoan", Icons.Default.AccountCircle)
)
