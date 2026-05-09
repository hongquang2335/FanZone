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
    val artists: List<String>,
    val timeline: List<EventMoment>,
    val notices: List<String>,
    @param:DrawableRes val imageRes: Int
)

data class EventMoment(
    val time: String,
    val title: String
)
