package com.example.myapplication.app

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.core.designsystem.component.AppBottomBar
import com.example.myapplication.core.navigation.AppDestination
import com.example.myapplication.core.navigation.FanZoneNavHost
import com.example.myapplication.core.navigation.bottomDestinations
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDeep
import com.example.myapplication.ui.state.FanZoneViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FanZoneApp(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    viewModel: FanZoneViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == null || currentRoute in bottomDestinations.map { it.route }
    val isHome = currentRoute == AppDestination.Home.route || currentRoute == null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    items = bottomDestinations,
                    currentRoute = currentRoute
                ) { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
        floatingActionButton = {
            if (isHome) {
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestination.Support.route) },
                    containerColor = VibeGreen,
                    contentColor = VibeGreenDeep,
                    shape = androidx.compose.foundation.shape.CircleShape,
                ) {
                    Icon(Icons.Default.ChatBubble, contentDescription = "Support Chat")
                }
            }
        }
    ) { padding ->
        FanZoneNavHost(
            navController = navController,
            uiState = uiState,
            viewModel = viewModel,
            darkTheme = darkTheme,
            onDarkThemeChange = onDarkThemeChange,
            modifier = Modifier.fillMaxSize().padding(padding)
        )
    }
}
