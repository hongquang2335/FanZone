package com.example.fanzone.domain.repository

import com.example.fanzone.domain.models.*
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getTrendingEvents(): Flow<List<Event>>
    fun getEventById(id: String): Flow<Event?>
}

interface TicketRepository {
    fun getTicketZones(eventId: String): Flow<List<TicketZone>>
    suspend fun bookTicket(userId: String, zoneId: String): Result<Unit>
}

interface PostRepository {
    fun getFeedPosts(): Flow<List<Post>>
    suspend fun createPost(post: Post): Result<Unit>
}

interface UserRepository {
    fun getUserProfile(userId: String): Flow<User?>
}
