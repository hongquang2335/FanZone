package com.example.fanzone.domain.usecases

import com.example.fanzone.domain.models.Event
import com.example.fanzone.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetTrendingEventsUseCase(private val repository: EventRepository) {
    operator fun invoke(): Flow<List<Event>> {
        return repository.getTrendingEvents()
    }
}
