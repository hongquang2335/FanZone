package com.example.myapplication.domain.model

import androidx.annotation.DrawableRes

data class Event(
    val id: String,
    val title: String,
    val subtitle: String,
    val schedule: String,
    val venue: String,
    val city: String,
    val description: String,
    val artists: List<Artist>,
    val timeline: List<EventMoment>,
    val notices: List<String>,
    @param:DrawableRes val imageRes: Int,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList(),
    val category: List<String> = emptyList(),
    val performances: List<PerformanceSchedule> = emptyList(),
    val resaleTickets: List<ResaleTicket> = emptyList(),
    val orgName: String = "",
    val orgLogo: String? = null,
    val orgDescription: String = "",
    val startTime: String = "",
    val endTime: String = ""
)

data class EventMoment(
    val time: String,
    val title: String
)

data class PerformanceSchedule(
    val id: String,
    val time: String,
    val date: String,
    val ticketTiers: List<TicketTier> = emptyList()
)

data class ResaleTicket(
    val id: String,
    val tierName: String,
    val price: Int,
    val sellerName: String,
    val sellerAvatarUrl: String? = null
)
