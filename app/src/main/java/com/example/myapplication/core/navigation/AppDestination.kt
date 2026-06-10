package com.example.myapplication.core.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Community : AppDestination("community")
    data object Tickets : AppDestination("tickets")
    data object Profile : AppDestination("profile")
    data object ViewedProfile : AppDestination("profile/{profileId}") {
        fun create(profileId: String) = "profile/$profileId"
    }
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object ForgotPassword : AppDestination("forgot-password")
    data object ResetPasswordCode : AppDestination("reset-password/code") {
        const val baseRoute = "reset-password/code"
        fun create(): String = baseRoute
    }
    data object ResetPasswordNew : AppDestination("reset-password/new") {
        const val baseRoute = "reset-password/new"
        fun create(): String = baseRoute
    }
    data object AccountInfo : AppDestination("profile/account-info")
    data object ProfileOptions : AppDestination("profile/options")
    data object NotificationSettings : AppDestination("profile/notification-settings")
    data object Notifications : AppDestination("profile/notifications")
    data object Support : AppDestination("support")
    data object EventCommunity : AppDestination("community/event/{eventId}") {
        fun create(eventId: String) = "community/event/$eventId"
    }
    data object EventDetail : AppDestination("event/{eventId}") {
        fun create(eventId: String) = "event/$eventId"
    }
    data object Booking : AppDestination("booking/{eventId}") {
        fun create(eventId: String) = "booking/$eventId"
    }
    data object Checkout : AppDestination("checkout/{eventId}") {
        fun create(eventId: String) = "checkout/$eventId"
    }
    data object Success : AppDestination("success")
}

