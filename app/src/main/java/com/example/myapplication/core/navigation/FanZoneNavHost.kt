package com.example.myapplication.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.feature.booking.BookingRoute
import com.example.myapplication.feature.checkout.CheckoutRoute
import com.example.myapplication.feature.community.CommunityFeedViewModel
import com.example.myapplication.feature.community.CommunityScreen
import com.example.myapplication.feature.community.EventCommunityScreen
import com.example.myapplication.feature.event.EventDetailScreen
import com.example.myapplication.feature.home.HomeScreen
import com.example.myapplication.feature.profile.AccountInfoScreen
import com.example.myapplication.feature.profile.AuthViewModel
import com.example.myapplication.feature.profile.LoginScreen
import com.example.myapplication.feature.profile.NotificationSettingsScreen
import com.example.myapplication.feature.profile.PinSetupScreen
import com.example.myapplication.feature.profile.ProfileOptionsScreen
import com.example.myapplication.feature.profile.ProfileScreen
import com.example.myapplication.feature.profile.RegisterScreen
import com.example.myapplication.feature.success.PurchaseSuccessScreen
import com.example.myapplication.feature.support.ChatbotScreen
import com.example.myapplication.feature.tickets.TicketWalletRoute
import com.example.myapplication.ui.state.FanZoneUiState
import com.example.myapplication.ui.state.FanZoneViewModel

@Composable
fun FanZoneNavHost(
    navController: NavHostController,
    uiState: FanZoneUiState,
    viewModel: FanZoneViewModel,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    communityFeedViewModel: CommunityFeedViewModel = composeViewModel(),
    authViewModel: AuthViewModel = composeViewModel()
) {
    val communityFeedState by communityFeedViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.posts) {
        communityFeedViewModel.setFallbackPosts(uiState.posts)
    }

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
                posts = communityFeedState.posts,
                currentAuthorName = communityFeedState.currentAuthorName,
                currentUserId = communityFeedState.currentUserId,
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventCommunity.create(eventId))
                },
                onSharePost = communityFeedViewModel::sharePost,
                onToggleLike = communityFeedViewModel::toggleLike,
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.EventCommunity.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            val eventId = entry.arguments?.getString("eventId")
            val event = uiState.events.firstOrNull { it.id == eventId } ?: uiState.selectedEvent
            LaunchedEffect(eventId) {
                eventId?.let(viewModel::selectEvent)
            }
            EventCommunityScreen(
                event = event,
                posts = communityFeedState.posts.filter { it.eventId == eventId },
                currentAuthorName = communityFeedState.currentAuthorName,
                currentUserId = communityFeedState.currentUserId,
                onSharePost = communityFeedViewModel::sharePost,
                onToggleLike = communityFeedViewModel::toggleLike,
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Tickets.route) {
            TicketWalletRoute(
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
                authState = authState,
                unreadSupport = uiState.unreadSupportCount,
                posts = communityFeedState.posts,
                onOpenSupport = { navController.navigate(AppDestination.Support.route) },
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                onOpenAccountInfo = { navController.navigate(AppDestination.AccountInfo.route) },
                onOpenPinSetup = { navController.navigate(AppDestination.PinSetup.route) },
                onOpenNotificationSettings = { navController.navigate(AppDestination.NotificationSettings.route) },
                onOpenProfileOptions = { navController.navigate(AppDestination.ProfileOptions.route) },
                onSignOut = authViewModel::signOut,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.ProfileOptions.route) {
            ProfileOptionsScreen(
                onBack = { navController.popBackStack() },
                onOpenAccountInfo = { navController.navigate(AppDestination.AccountInfo.route) },
                onOpenPinSetup = { navController.navigate(AppDestination.PinSetup.route) },
                onOpenNotificationSettings = { navController.navigate(AppDestination.NotificationSettings.route) },
                onOpenSupport = { navController.navigate(AppDestination.Support.route) },
                onSignOut = {
                    authViewModel.signOut()
                    navController.popBackStack(AppDestination.Profile.route, inclusive = false)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.AccountInfo.route) {
            AccountInfoScreen(
                authUser = authState.user,
                accountProfile = authState.accountProfile,
                authState = authState,
                onSave = authViewModel::saveAccountProfile,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.PinSetup.route) {
            PinSetupScreen(
                authState = authState,
                onSavePin = authViewModel::savePin,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.NotificationSettings.route) {
            NotificationSettingsScreen(
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Login.route) {
            LoginScreen(
                authState = authState,
                onClose = { navController.popBackStack() },
                onOpenRegister = { navController.navigate(AppDestination.Register.route) },
                onLogin = { email, password ->
                    authViewModel.signIn(email, password) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(AppDestination.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Register.route) {
            RegisterScreen(
                authState = authState,
                onBack = { navController.popBackStack() },
                onOpenLogin = {
                    navController.popBackStack(AppDestination.Login.route, inclusive = false)
                },
                onRegister = { email, password, repeatPassword ->
                    authViewModel.register(email, password, repeatPassword) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(AppDestination.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Support.route) {
            ChatbotScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = AppDestination.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            val eventId = entry.arguments?.getString("eventId")
            val event = uiState.events.firstOrNull { it.id == eventId } ?: uiState.selectedEvent
            val tiers = uiState.tiers.filter { it.eventId == event.id }
            LaunchedEffect(eventId) {
                eventId?.let(viewModel::selectEvent)
            }
            EventDetailScreen(
                event = event,
                tiers = tiers,
                onBack = { navController.popBackStack() },
                onBuyNow = { navController.navigate(AppDestination.Booking.create(event.id)) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.Booking.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            val eventId = entry.arguments?.getString("eventId")
            val event = uiState.events.firstOrNull { it.id == eventId } ?: uiState.selectedEvent
            val tiers = uiState.tiers.filter { it.eventId == event.id }
            LaunchedEffect(eventId) {
                eventId?.let(viewModel::selectEvent)
            }
            BookingRoute(
                event = event,
                tiers = tiers,
                initialQuantities = uiState.tierQuantities,
                onBack = { navController.popBackStack() },
                onCommitQuantities = viewModel::setTierQuantities,
                onContinue = { navController.navigate(AppDestination.Checkout.create(event.id)) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.Checkout.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            val eventId = entry.arguments?.getString("eventId")
            val event = uiState.events.firstOrNull { it.id == eventId } ?: uiState.selectedEvent
            val tiers = uiState.tiers.filter { it.eventId == event.id }
            LaunchedEffect(eventId) {
                eventId?.let(viewModel::selectEvent)
            }
            CheckoutRoute(
                event = event,
                tiers = tiers,
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
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
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
