package com.example.myapplication.feature.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.Artist
import com.example.myapplication.domain.model.PerformanceSchedule
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TierStatus
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class EventDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        Log.d("EventDetailVM", "--- Bắt đầu loadEvent ---")
        Log.d("EventDetailVM", "Yêu cầu ID: $eventId")
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                var doc = db.collection("event").document(eventId).get().await()
                
                // Nếu không tìm thấy bằng ID cụ thể (do ID neon-night là ảo)
                if (!doc.exists()) {
                    Log.w("EventDetailVM", "Không tìm thấy document ID: $eventId. Đang thử lấy document đầu tiên trong collection...")
                    val firstDocSnapshot = db.collection("event").limit(1).get().await()
                    if (!firstDocSnapshot.isEmpty) {
                        doc = firstDocSnapshot.documents[0]
                        Log.d("EventDetailVM", "Đã tìm thấy document thay thế với ID thật: ${doc.id}")
                    }
                }

                if (doc.exists()) {
                    parseEventDocument(doc)
                } else {
                    Log.e("EventDetailVM", "Collection 'event' hoàn toàn trống trên Firestore!")
                    _uiState.update { it.copy(isLoading = false, error = "Không có dữ liệu sự kiện") }
                }
            } catch (e: Exception) {
                Log.e("EventDetailVM", "Lỗi kết nối hoặc bảo mật: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = "Lỗi kết nối Firestore. Kiểm tra SHA-1 và Rules.") }
            }
        }
    }

    private fun parseEventDocument(doc: DocumentSnapshot) {
        try {
            val rawStartTime = doc.getString("startTime") ?: ""
            val rawEndTime = doc.getString("endTime") ?: ""
            
            val formattedSchedule = if (rawStartTime.isNotEmpty()) {
                formatEventSchedule(rawStartTime, rawEndTime)
            } else doc.getString("schedule") ?: "Đang cập nhật"

            // Bóc tách vé từ ticketTypes mới
            val rawTicketTypes = doc.get("ticketTypes") as? List<*>
            val parsedTiers = rawTicketTypes?.mapNotNull { item ->
                val ticketMap = item as? Map<*, *> ?: return@mapNotNull null
                TicketTier(
                    id = ticketMap["typeId"]?.toString() ?: "",
                    eventId = doc.id,
                    name = ticketMap["typeId"]?.toString() ?: "Hạng vé",
                    benefits = ticketMap["zoneName"]?.toString() ?: "Vé bán trên Event Hub",
                    price = (ticketMap["price"] as? Number)?.toInt() ?: 0,
                    status = TierStatus.AVAILABLE
                )
            } ?: emptyList()

            val event = Event(
                id = doc.id, 
                title = doc.getString("title") ?: "Sự kiện không tên",
                subtitle = doc.getString("orgName") ?: doc.getString("subtitle") ?: "Ban tổ chức",
                schedule = formattedSchedule,
                venue = doc.getString("venue") ?: "Chưa rõ địa điểm",
                city = doc.getString("address") ?: doc.getString("city") ?: "",
                description = doc.getString("description") ?: "Không có mô tả.",
                artists = (doc.get("artists") as? List<*>)?.mapNotNull { artistObj ->
                    val map = artistObj as? Map<*, *> ?: return@mapNotNull null
                    Artist(
                        id = map["id"]?.toString() ?: "",
                        name = map["name"]?.toString() ?: "Nghệ sĩ",
                        image = map["image"]?.toString() ?: ""
                    )
                } ?: emptyList(),
                timeline = emptyList(),
                notices = (doc.get("notices") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                imageRes = 0,
                imageUrl = doc.getString("banner") ?: doc.getString("imageUrl"),
                tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                category = (doc.get("category") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                orgName = doc.getString("orgName") ?: "",
                orgLogo = doc.getString("orgLogo"),
                orgDescription = doc.getString("orgDescription") ?: "",
                startTime = rawStartTime,
                endTime = rawEndTime
            )
            
            _uiState.update { 
                it.copy(isLoading = false, event = event, tiers = parsedTiers) 
            }
            Log.d("EventDetailVM", "Parse dữ liệu thành công cho: ${event.title}")
        } catch (e: Exception) {
            Log.e("EventDetailVM", "Lỗi khi parse dữ liệu từ Firestore: ${e.message}")
            _uiState.update { it.copy(isLoading = false, error = "Dữ liệu Firestore sai định dạng") }
        }
    }

    private fun formatEventSchedule(startTimeStr: String, endTimeStr: String): String {
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US)
            val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            val startDate = sdfInput.parse(startTimeStr) ?: return startTimeStr
            val endDate = if (endTimeStr.isNotEmpty()) sdfInput.parse(endTimeStr) else null
            
            val timeFormat = SimpleDateFormat("HH:mm", Locale.US).apply {
                timeZone = vietnamTimeZone
            }
            val dateFormat = SimpleDateFormat("dd 'tháng' MM, yyyy", Locale("vi", "VN")).apply {
                timeZone = vietnamTimeZone
            }
            
            val getDayOfWeek = { date: java.util.Date ->
                val cal = Calendar.getInstance(vietnamTimeZone)
                cal.time = date
                when (cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Thứ hai"
                    Calendar.TUESDAY -> "Thứ ba"
                    Calendar.WEDNESDAY -> "Thứ tư"
                    Calendar.THURSDAY -> "Thứ năm"
                    Calendar.FRIDAY -> "Thứ sáu"
                    Calendar.SATURDAY -> "Thứ bảy"
                    Calendar.SUNDAY -> "Chủ nhật"
                    else -> ""
                }
            }

            val startDayOfWeek = getDayOfWeek(startDate)

            if (endDate != null) {
                val calStart = Calendar.getInstance(vietnamTimeZone).apply { time = startDate }
                val calEnd = Calendar.getInstance(vietnamTimeZone).apply { time = endDate }
                val isSameDay = calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR) &&
                        calStart.get(Calendar.DAY_OF_YEAR) == calEnd.get(Calendar.DAY_OF_YEAR)
                
                if (isSameDay) {
                    "${timeFormat.format(startDate)}-${timeFormat.format(endDate)}, $startDayOfWeek|${dateFormat.format(startDate)}"
                } else {
                    val endDayOfWeek = getDayOfWeek(endDate)
                    "${timeFormat.format(startDate)} $startDayOfWeek - ${timeFormat.format(endDate)} $endDayOfWeek|${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
                }
            } else {
                "${timeFormat.format(startDate)}, $startDayOfWeek|${dateFormat.format(startDate)}"
            }
        } catch (e: Exception) {
            startTimeStr
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
        } catch (e: Exception) { dateString }
    }
}
