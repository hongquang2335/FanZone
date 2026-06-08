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
import com.example.myapplication.feature.community.CommunityRoute
import com.example.myapplication.feature.community.CommunityViewModel
import com.example.myapplication.feature.community.EventCommunityRoute
import com.example.myapplication.feature.event.EventDetailRoute
import com.example.myapplication.feature.event.EventDetailScreen
import com.example.myapplication.feature.home.HomeScreen
import com.example.myapplication.feature.authentication.AuthViewModel
import com.example.myapplication.feature.authentication.ForgotPasswordScreen
import com.example.myapplication.feature.authentication.LoginScreen
import com.example.myapplication.feature.authentication.NewPasswordScreen
import com.example.myapplication.feature.authentication.RegisterScreen
import com.example.myapplication.feature.authentication.ResetPasswordCodeScreen
import com.example.myapplication.feature.profile.AccountInfoRoute
import com.example.myapplication.feature.profile.NotificationSettingsRoute
import com.example.myapplication.feature.profile.ProfileOptionsRoute
import com.example.myapplication.feature.profile.ProfileRoute
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
    communityViewModel: CommunityViewModel = composeViewModel(),
    authViewModel: AuthViewModel = composeViewModel()
) {
    val communityState by communityViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

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
            CommunityRoute(
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventCommunity.create(eventId))
                },
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                viewModel = communityViewModel,
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
            EventCommunityRoute(
                event = event,
                eventId = eventId,
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                onBack = { navController.popBackStack() },
                viewModel = communityViewModel,
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
            ProfileRoute(
                user = uiState.user,
                authState = authState,
                unreadSupport = uiState.unreadSupportCount,
                posts = communityState.posts,
                onOpenSupport = { navController.navigate(AppDestination.Support.route) },
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                onOpenAccountInfo = {
                    authViewModel.clearMessages()
                    navController.navigate(AppDestination.AccountInfo.route)
                },
                onOpenNotificationSettings = { navController.navigate(AppDestination.NotificationSettings.route) },
                onOpenProfileOptions = { navController.navigate(AppDestination.ProfileOptions.route) },
                onSignOut = authViewModel::signOut,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.ProfileOptions.route) {
            ProfileOptionsRoute(
                onBack = { navController.popBackStack() },
                onOpenAccountInfo = {
                    authViewModel.clearMessages()
                    navController.navigate(AppDestination.AccountInfo.route)
                },
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
            LaunchedEffect(Unit) {
                authViewModel.clearMessages()
            }
            AccountInfoRoute(
                authState = authState,
                onSave = authViewModel::saveAccountProfile,
                onLinkGoogle = { idToken ->
                    authViewModel.signInWithGoogle(idToken) {
                        navController.popBackStack()
                    }
                },
                onGoogleError = authViewModel::showError,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.NotificationSettings.route) {
            NotificationSettingsRoute(
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Login.route) {
            LoginScreen(
                authState = authState,
                onClose = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onOpenRegister = {
                    authViewModel.clearMessages()
                    navController.navigate(AppDestination.Register.route)
                },
                onLogin = { email, password ->
                    authViewModel.signIn(email, password) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(AppDestination.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onForgotPassword = {
                    authViewModel.clearMessages()
                    navController.navigate(AppDestination.ForgotPassword.route)
                },
                onGoogleLogin = { idToken ->
                    authViewModel.signInWithGoogle(idToken) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(AppDestination.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onGoogleLoginError = authViewModel::showError,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Register.route) {
            RegisterScreen(
                authState = authState,
                onBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onOpenLogin = {
                    authViewModel.clearMessages()
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
        composable(AppDestination.ForgotPassword.route) {
            ForgotPasswordScreen(
                authState = authState,
                onBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onSendResetLink = { email ->
                    authViewModel.sendPasswordResetLink(email) {
                        authViewModel.clearMessages()
                        navController.navigate(AppDestination.ResetPasswordCode.create())
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.ResetPasswordCode.route) {
            ResetPasswordCodeScreen(
                authState = authState,
                onBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onVerifyCode = { codeOrLink ->
                    authViewModel.verifyPasswordResetCode(codeOrLink) {
                        authViewModel.clearMessages()
                        navController.navigate(AppDestination.ResetPasswordNew.create())
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.ResetPasswordNew.route) {
            NewPasswordScreen(
                authState = authState,
                onBack = {
                    authViewModel.clearMessages()
                    navController.popBackStack()
                },
                onConfirmPasswordReset = { password, repeatPassword ->
                    authViewModel.confirmVerifiedPasswordReset(password, repeatPassword) {
                        authViewModel.clearMessages()
                        navController.navigate(AppDestination.Login.route) {
                            popUpTo(AppDestination.Login.route) { inclusive = true }
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
            EventDetailRoute(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onNavigateToBooking = { id -> navController.navigate(AppDestination.Booking.create(id)) },
                onOpenCommunity = { id -> navController.navigate(AppDestination.EventCommunity.create(id)) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.Booking.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { entry ->
            val eventId = entry.arguments?.getString("eventId")
            LaunchedEffect(eventId) {
                eventId?.let(viewModel::selectEvent)
            }
            BookingRoute(
                event = uiState.selectedEvent,
                onBack = { navController.popBackStack() },
                onContinue = { selectedSeats ->
                    viewModel.setSelectedSeats(selectedSeats)
                    navController.navigate(AppDestination.Checkout.create(uiState.selectedEvent.id))
                },
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
