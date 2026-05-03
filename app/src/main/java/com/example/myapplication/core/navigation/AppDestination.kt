package com.example.myapplication.core.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Community : AppDestination("community")
    data object Tickets : AppDestination("tickets")
    data object Profile : AppDestination("profile")
    data object Support : AppDestination("support")
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

