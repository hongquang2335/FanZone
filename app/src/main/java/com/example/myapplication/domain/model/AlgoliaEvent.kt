package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AlgoliaEvent(
    val objectID: String,
    val title: String,
    val venue: String,
    val address: String,
    val startTime: String,
    val artists: List<Artist>,
    val category: List<String>,
    val ticketTypes: List<TicketType>,
    val orgName: String,
    val endTime: String,
    val isFree: Boolean,
    val status: String,
    val priceFrom: Int
)

@Serializable
data class Artist(
    val name: String,
    val id: String,
    val image: String? = null
)

@Serializable
data class TicketType(
    val price: Int,
    val zoneName: String,
    val typeId: String,
    val status: String
)

@Serializable
data class AlgoliaSearchResponse(
    val hits: List<AlgoliaEvent>,
    val nbHits: Int,
    val page: Int,
    val nbPages: Int
)