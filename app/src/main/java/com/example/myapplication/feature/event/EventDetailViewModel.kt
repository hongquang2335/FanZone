package com.example.myapplication.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PerformanceSchedule
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TierStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                val doc = db.collection("event").document(eventId).get().await()
                
                if (doc.exists()) {
                    val rawStartTime = doc.getString("startTime") ?: ""
                    val rawEndTime = doc.getString("endTime") ?: ""
                    
                    val formattedSchedule = if (rawStartTime.isNotEmpty()) {
                        formatDate(rawStartTime) + (if (rawEndTime.isNotEmpty()) " - " + formatDate(rawEndTime) else "")
                    } else ""

                    // Bóc tách vé an toàn
                    val rawTickets = doc.get("tickets") as? List<*>
                    val parsedTiers = rawTickets?.mapNotNull { item ->
                        try {
                            val ticketMap = item as? Map<*, *> ?: return@mapNotNull null
                            val statusStr = ticketMap["status"]?.toString() ?: ""
                            val tierStatus = when {
                                statusStr.contains("closed", true) || statusStr.contains("sold", true) -> TierStatus.SOLD_OUT
                                else -> TierStatus.AVAILABLE
                            }

                            TicketTier(
                                id = ticketMap["id"]?.toString() ?: "",
                                eventId = doc.id,
                                name = ticketMap["name"]?.toString() ?: "",
                                benefits = "Vé bán trên Event Hub",
                                price = (ticketMap["price"] as? Number)?.toInt() ?: 0,
                                status = tierStatus
                            )
                        } catch (e: Exception) { null }
                    } ?: emptyList()

                    // Nhóm vé vào lịch biểu diễn
                    val performance = PerformanceSchedule(
                        id = "perf-1",
                        time = if (rawStartTime.isNotEmpty()) rawStartTime.substringAfter("T").take(5) else "",
                        date = if (rawStartTime.isNotEmpty()) formatDate(rawStartTime) else "",
                        ticketTiers = parsedTiers
                    )

                    val event = Event(
                        id = doc.getLong("id")?.toString() ?: doc.id,
                        title = doc.getString("title") ?: "",
                        subtitle = doc.getString("orgName") ?: "",
                        schedule = formattedSchedule,
                        venue = doc.getString("venue") ?: "",
                        city = doc.getString("address") ?: "",
                        description = doc.getString("description") ?: "",
                        artists = (doc.get("artists") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        timeline = emptyList(),
                        notices = (doc.get("notices") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        imageRes = 0,
                        imageUrl = doc.getString("banner"),
                        tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() 
                            ?: listOfNotNull(doc.getString("category")),
                        performances = if (parsedTiers.isNotEmpty()) listOf(performance) else emptyList(),
                        resaleTickets = emptyList()
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            event = event,
                            tiers = parsedTiers
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không tìm thấy sự kiện") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Lỗi kết nối") }
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val parts = dateString.split("T")
            if (parts.size >= 2) {
                val date = parts[0]
                val dateParts = date.split("-")
                if (dateParts.size == 3) {
                    "${dateParts[2]}/${dateParts[1]}/${dateParts[0]}"
                } else dateString
            } else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
