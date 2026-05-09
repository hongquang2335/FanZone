package com.example.myapplication.app

import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.myapplication.core.navigation.FanZoneNavHost
import com.example.myapplication.core.navigation.bottomDestinations
import com.example.myapplication.ui.state.FanZoneViewModel

@Composable
fun FanZoneApp(
    viewModel: FanZoneViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomDestinations.map { it.route }

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
        }
    ) {
        FanZoneNavHost(
            navController = navController,
            uiState = uiState,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}
