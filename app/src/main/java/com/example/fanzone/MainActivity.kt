package com.example.fanzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fanzone.ui.screens.*
import com.example.fanzone.ui.theme.FanZoneTheme
import com.example.fanzone.ui.theme.Primary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FanZoneTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Feed : Screen("feed", "Feed", Icons.Outlined.DynamicFeed, Icons.Filled.DynamicFeed)
    object Tickets : Screen("event", "Tickets", Icons.Outlined.ConfirmationNumber, Icons.Filled.ConfirmationNumber)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Feed,
        Screen.Tickets,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            // Only show bottom bar on main screens
            val showBottomBar = items.any { it.route == currentDestination?.route }
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    containerColor = Color(0xFF1A1919).copy(alpha = 0.8f),
                    tonalElevation = 0.dp
                ) {
                    items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (isSelected) screen.selectedIcon else screen.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(screen.label, fontSize = 10.sp) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = Color(0xFFADAAAA),
                                unselectedTextColor = Color(0xFFADAAAA),
                                indicatorColor = Primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onEventClick = { navController.navigate("event_detail") },
                    onCreateClick = { navController.navigate("create_post") }
                )
            }
            composable(Screen.Feed.route) {
                FeedScreen(onCreatePost = { navController.navigate("create_post") })
            }
            composable(Screen.Tickets.route) {
                // For simplicity, reusing EventDetail as the landing page for Tickets in this mockup
                EventDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSelectSeats = { navController.navigate("seats") }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable("event_detail") {
                EventDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSelectSeats = { navController.navigate("seats") }
                )
            }
            composable("seats") {
                SeatsScreen(onBack = { navController.popBackStack() })
            }
            composable("create_post") {
                CreatePostScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
