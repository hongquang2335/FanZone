package com.example.myapplication.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.feature.booking.BookingRoute
import com.example.myapplication.feature.checkout.CheckoutRoute
import com.example.myapplication.feature.community.CommunityScreen
import com.example.myapplication.feature.event.EventDetailScreen
import com.example.myapplication.feature.home.HomeScreen
import com.example.myapplication.feature.profile.ProfileScreen
import com.example.myapplication.feature.success.PurchaseSuccessScreen
import com.example.myapplication.feature.support.SupportScreen
import com.example.myapplication.feature.tickets.TicketWalletRoute
import com.example.myapplication.ui.state.FanZoneUiState
import com.example.myapplication.ui.state.FanZoneViewModel

@Composable
fun FanZoneNavHost(
    navController: NavHostController,
    uiState: FanZoneUiState,
    viewModel: FanZoneViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(
                event = uiState.selectedEvent,
                events = uiState.events,
                categories = uiState.categories,
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
                onOpenCommunity = { navController.navigate(AppDestination.Community.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Community.route) {
            CommunityScreen(
                posts = uiState.posts,
                onOpenEvent = {
                    navController.navigate(AppDestination.EventDetail.create(uiState.selectedEvent.id))
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Tickets.route) {
            TicketWalletRoute(
                tickets = uiState.walletItems,
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                user = uiState.user,
                unreadSupport = uiState.unreadSupportCount,
                onOpenSupport = { navController.navigate(AppDestination.Support.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Support.route) {
            SupportScreen(
                supportShortcuts = uiState.supportShortcuts,
                unreadSupport = uiState.unreadSupportCount,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            entry.arguments?.getString("eventId")?.let(viewModel::selectEvent)
            EventDetailScreen(
                event = uiState.selectedEvent,
                tiers = uiState.tiersForSelectedEvent,
                onBack = { navController.popBackStack() },
                onBuyNow = { navController.navigate(AppDestination.Booking.create(uiState.selectedEvent.id)) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.Booking.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            entry.arguments?.getString("eventId")?.let(viewModel::selectEvent)
            BookingRoute(
                event = uiState.selectedEvent,
                tiers = uiState.tiersForSelectedEvent,
                initialQuantities = uiState.tierQuantities,
                onBack = { navController.popBackStack() },
                onCommitQuantities = viewModel::setTierQuantities,
                onContinue = { navController.navigate(AppDestination.Checkout.create(uiState.selectedEvent.id)) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.Checkout.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            entry.arguments?.getString("eventId")?.let(viewModel::selectEvent)
            CheckoutRoute(
                event = uiState.selectedEvent,
                tiers = uiState.tiersForSelectedEvent,
                quantities = uiState.tierQuantities,
                paymentMethods = uiState.paymentMethods,
                selectedPaymentMethod = uiState.selectedPaymentMethod,
                onBack = { navController.popBackStack() },
                onCommitPaymentMethod = viewModel::selectPaymentMethod,
                onConfirm = {
                    viewModel.confirmPurchase()
                    navController.navigate(AppDestination.Success.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Success.route) {
            PurchaseSuccessScreen(
                ticket = uiState.latestPurchasedTicket,
                onOpenWallet = {
                    navController.navigate(AppDestination.Tickets.route) {
                        popUpTo(AppDestination.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onGoHome = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
