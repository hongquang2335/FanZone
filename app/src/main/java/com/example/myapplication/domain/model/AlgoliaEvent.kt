package com.example.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AlgoliaEvent(
    val objectID: String,
    val title: String = "",
    val venue: String = "",
    val address: String = "",
    val startTime: String = "",
    val artists: List<Artist> = emptyList(),
    val category: List<String> = emptyList(),
    val ticketTypes: List<TicketType> = emptyList(),
    val orgName: String = "",
    val endTime: String = "",
    val isFree: Boolean = false,
    val status: String = "",
    val priceFrom: Int = 0
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