package com.example.myapplication.core.navigation

import android.net.Uri
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
import com.example.myapplication.feature.profile.ViewedProfileRoute
import com.example.myapplication.feature.profile.NotificationViewModel
import com.example.myapplication.feature.profile.NotificationListScreen
import com.example.myapplication.feature.success.PurchaseSuccessScreen
import com.example.myapplication.feature.support.ChatViewModel
import com.example.myapplication.feature.support.ChatbotScreen
import com.example.myapplication.feature.tickets.TicketWalletRoute
import com.example.myapplication.app.navigateToRootDestination
import com.example.myapplication.ui.state.FanZoneUiState
import com.example.myapplication.ui.state.FanZoneViewModel
import com.example.myapplication.core.designsystem.theme.ElectricStageTheme

@Composable
fun FanZoneNavHost(
    navController: NavHostController,
    uiState: FanZoneUiState,
    viewModel: FanZoneViewModel,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    communityViewModel: CommunityViewModel = composeViewModel(),
    authViewModel: AuthViewModel = composeViewModel(),
    chatViewModel: ChatViewModel = composeViewModel()
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
                events = uiState.events,
                posts = communityState.posts,
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
                onNavigateToSearch = { navController.navigate("search_route") },
                onViewCategory = { category ->
                    navController.navigate("search_route?category=${Uri.encode(category)}")
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = "search_route?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            com.example.myapplication.feature.search.SearchScreen(
                events = uiState.events,
                searchHistory = authState.searchHistory,
                isSignedIn = authState.isSignedIn,
                initialCategory = entry.arguments?.getString("category"),
                onSearchSubmit = authViewModel::saveSearchQuery,
                onBackClick = { navController.popBackStack() },
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
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
                onOpenProfile = { profileId ->
                    if (profileId == authState.user?.uid) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(AppDestination.ViewedProfile.create(profileId))
                    }
                },
                viewModel = communityViewModel,
                onOpenNotifications = { navController.navigate(AppDestination.Notifications.route) },
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
                onOpenProfile = { profileId ->
                    if (profileId == authState.user?.uid) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(AppDestination.ViewedProfile.create(profileId))
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = communityViewModel,
                onOpenNotifications = { navController.navigate(AppDestination.Notifications.route) },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Tickets.route) {
            TicketWalletRoute(
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileRoute(
                user = uiState.user,
                authState = authState,
                unreadSupport = uiState.unreadSupportCount,
                posts = communityState.posts,
                commentsByPostId = communityState.mappedCommentsByPostId,
                onSharePost = communityViewModel::sharePost,
                onToggleLike = communityViewModel::toggleLike,
                onToggleFollow = communityViewModel::toggleFollow,
                onOpenComments = communityViewModel::observeComments,
                onAddComment = communityViewModel::addComment,
                onDeletePost = { id -> communityViewModel.deletePost(id) },
                onEditPost = { post -> communityViewModel.openEditPost(post) },
                onOpenSupport = { navController.navigate(AppDestination.Support.route) },
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
                onOpenAccountInfo = {
                    authViewModel.clearMessages()
                    navController.navigate(AppDestination.AccountInfo.route)
                },
                onOpenNotificationSettings = { navController.navigate(AppDestination.NotificationSettings.route) },
                onOpenProfileOptions = { navController.navigate(AppDestination.ProfileOptions.route) },
                onSignOut = authViewModel::signOut,
                onOpenProfile = { profileId ->
                    if (profileId == authState.user?.uid) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(AppDestination.ViewedProfile.create(profileId))
                    }
                },
                onOpenNotifications = { navController.navigate(AppDestination.Notifications.route) },
                unreadNotificationCount = communityState.unreadNotificationCount,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AppDestination.ViewedProfile.route,
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { entry ->
            val profileId = entry.arguments?.getString("profileId").orEmpty()
            ViewedProfileRoute(
                profileId = profileId,
                currentUserId = authState.user?.uid,
                posts = communityState.posts,
                commentsByPostId = communityState.mappedCommentsByPostId,
                onSharePost = communityViewModel::sharePost,
                onToggleLike = communityViewModel::toggleLike,
                onToggleFollow = communityViewModel::toggleFollow,
                onOpenComments = communityViewModel::observeComments,
                onAddComment = communityViewModel::addComment,
                onDeletePost = { id -> communityViewModel.deletePost(id) },
                onEditPost = { post -> communityViewModel.openEditPost(post) },
                onBack = { navController.popBackStack() },
                onOpenAuth = { navController.navigate(AppDestination.Login.route) },
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
            ElectricStageTheme {
                NotificationSettingsRoute(
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(AppDestination.Notifications.route) {
            val notificationViewModel: NotificationViewModel = composeViewModel()
            NotificationListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    communityViewModel.setTargetCommentsPostId(postId)
                    val posts = communityState.posts
                    val post = posts.firstOrNull { it.id == postId }
                    if (post?.eventId != null) {
                        navController.navigate(AppDestination.EventCommunity.create(post.eventId))
                    } else {
                        navController.navigate(AppDestination.Community.route)
                    }
                },
                onNavigateToProfile = { profileId ->
                    if (profileId == authState.user?.uid) {
                        navController.navigate(AppDestination.Profile.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(AppDestination.ViewedProfile.create(profileId))
                    }
                },
                viewModel = notificationViewModel,
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
                onBackClick = { navController.popBackStack() },
                viewModel = chatViewModel
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
                isUserSignedIn = authViewModel::isUserSignedIn,
                onNavigateToLogin = { navController.navigate(AppDestination.Login.route) },
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
                selectedSeats = uiState.selectedSeats,
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
            val purchasedTicket = uiState.latestPurchasedTicket
            val purchasedEvent = purchasedTicket?.let { ticket ->
                uiState.events.firstOrNull { it.id == ticket.eventId }
            } ?: uiState.selectedEvent
            PurchaseSuccessScreen(
                ticket = purchasedTicket,
                event = purchasedEvent,
                onOpenEvent = { eventId ->
                    viewModel.selectEvent(eventId)
                    navController.navigate(AppDestination.EventDetail.create(eventId))
                },
                onOpenWallet = {
                    navController.popBackStack(AppDestination.Home.route, inclusive = false)
                    navController.navigateToRootDestination(AppDestination.Tickets.route)
                },
                onGoHome = {
                    navController.popBackStack(AppDestination.Home.route, inclusive = false)
                    navController.navigateToRootDestination(AppDestination.Home.route)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    communityState.editingPost?.let { post ->
        com.example.myapplication.feature.community.component.EditPostDialog(
            post = post,
            onDismiss = communityViewModel::closeEditPost,
            onSave = { newText, newMedia ->
                communityViewModel.updatePost(post.id, newText, newMedia)
            }
        )
    }
}
