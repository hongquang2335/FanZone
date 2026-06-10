package com.example.myapplication.feature.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketWalletViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow(TicketWalletUiState())
    val uiState: StateFlow<TicketWalletUiState> = _uiState.asStateFlow()

    private var ticketsRegistration: ListenerRegistration? = null
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        observeTickets(firebaseAuth.currentUser)
    }

    init {
        auth.addAuthStateListener(authStateListener)
        observeTickets(auth.currentUser)
    }

    fun selectTab(tab: TicketTimeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    private fun observeTickets(user: FirebaseUser?) {
        ticketsRegistration?.remove()
        ticketsRegistration = null

        if (user == null) {
            _uiState.update {
                it.copy(eventGroups = emptyList(), isLoading = false, error = null)
            }
            return
        }

        val observedUid = user.uid
        _uiState.update { it.copy(isLoading = true, error = null) }
        ticketsRegistration = firestore.collection("users")
            .document(observedUid)
            .collection("my_tickets")
            .addSnapshotListener { snapshot, exception ->
                if (auth.currentUser?.uid != observedUid) return@addSnapshotListener
                if (exception != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Không thể tải vé của bạn. Vui lòng thử lại."
                        )
                    }
                    return@addSnapshotListener
                }

                val ticketDocuments = snapshot?.documents.orEmpty()
                viewModelScope.launch {
                    runCatching {
                        buildEventGroups(ticketDocuments)
                    }.onSuccess { groups ->
                        if (auth.currentUser?.uid == observedUid) {
                            _uiState.update {
                                it.copy(
                                    eventGroups = groups,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }.onFailure {
                        if (auth.currentUser?.uid == observedUid) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Không thể tải thông tin sự kiện của vé."
                                )
                            }
                        }
                    }
                }
            }
    }

    private suspend fun buildEventGroups(
        documents: List<DocumentSnapshot>
    ): List<OwnedEventTickets> {
        if (documents.isEmpty()) return emptyList()

        val ownerId = auth.currentUser?.uid ?: return emptyList()
        val tickets = documents.mapNotNull { document ->
            parseTicket(document, ownerId)
        }
        val eventDocuments = tickets
            .map(PurchasedTicket::eventId)
            .distinct()
            .map { eventId ->
                viewModelScope.async {
                    eventId to firestore.collection("event").document(eventId).get().await()
                }
            }
            .awaitAll()
            .toMap()

        val now = Date()
        return tickets.groupBy(PurchasedTicket::eventId)
            .map { (eventId, eventTickets) ->
                val event = eventDocuments[eventId]
                val firstTicket = eventTickets.first()
                val startTime = event.readDate("startTime") ?: firstTicket.startTime
                val endTime = event.readDate("endTime") ?: firstTicket.endTime
                val comparisonTime = endTime ?: startTime

                OwnedEventTickets(
                    eventId = eventId,
                    eventTitle = event?.getString("title")
                        .orEmpty()
                        .ifBlank { firstTicket.eventTitle },
                    venueName = event?.getString("venue")
                        .orEmpty()
                        .ifBlank { firstTicket.venue },
                    address = event?.getString("address")
                        .orEmpty()
                        .ifBlank { event?.getString("city").orEmpty() },
                    imageUrl = event?.getString("banner")
                        ?: event?.getString("imageUrl")
                        ?: firstTicket.imageUrl,
                    startTime = startTime,
                    endTime = endTime,
                    tickets = eventTickets
                        .map { ticket ->
                            OwnedTicket(
                                ticketId = ticket.ticketId,
                                eventId = ticket.eventId,
                                ownerId = ticket.ownerId,
                                seatName = ticket.seatName,
                                price = ticket.price
                            )
                        }
                        .distinctBy(OwnedTicket::ticketId)
                        .sortedWith { left, right ->
                            compareSeatNames(left.seatName, right.seatName)
                        },
                    hasEnded = comparisonTime?.before(now) == true
                )
            }
            .sortedWith(
                compareBy<OwnedEventTickets> { it.hasEnded }
                    .thenBy { it.startTime ?: Date(Long.MAX_VALUE) }
            )
    }

    private fun parseTicket(
        document: DocumentSnapshot,
        ownerId: String
    ): PurchasedTicket? {
        val eventId = document.getString("eventId")?.takeIf(String::isNotBlank)
            ?: return null
        return PurchasedTicket(
            ticketId = document.getString("ticketId")
                ?.takeIf(String::isNotBlank)
                ?: document.id,
            eventId = eventId,
            ownerId = document.getString("ownerId")
                ?.takeIf(String::isNotBlank)
                ?: ownerId,
            eventTitle = document.getString("eventTitle").orEmpty(),
            venue = document.getString("venue").orEmpty(),
            imageUrl = document.getString("imageUrl"),
            seatName = document.getString("seatId")
                ?: document.getString("seatName")
                ?: "",
            price = (document.get("purchasePrice") as? Number)?.toInt()
                ?: (document.get("price") as? Number)?.toInt()
                ?: 0,
            startTime = document.readDate("startTime"),
            endTime = document.readDate("endTime")
        )
    }

    override fun onCleared() {
        ticketsRegistration?.remove()
        auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}

private data class PurchasedTicket(
    val ticketId: String,
    val eventId: String,
    val ownerId: String,
    val eventTitle: String,
    val venue: String,
    val imageUrl: String?,
    val seatName: String,
    val price: Int,
    val startTime: Date?,
    val endTime: Date?
)

private fun DocumentSnapshot?.readDate(field: String): Date? {
    val value = this?.get(field) ?: return null
    return when (value) {
        is Timestamp -> value.toDate()
        is Date -> value
        is String -> value.toEventDate()
        else -> null
    }
}

private fun String.toEventDate(): Date? {
    if (isBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX"
    )
    return patterns.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                isLenient = false
            }.parse(trim())
        }.getOrNull()
    }
}

private fun compareSeatNames(left: String, right: String): Int {
    val leftRow = left.takeWhile(Char::isLetter).uppercase()
    val rightRow = right.takeWhile(Char::isLetter).uppercase()
    val rowComparison = leftRow.compareTo(rightRow)
    if (rowComparison != 0) return rowComparison

    val leftNumber = left.dropWhile(Char::isLetter).toIntOrNull() ?: 0
    val rightNumber = right.dropWhile(Char::isLetter).toIntOrNull() ?: 0
    return leftNumber.compareTo(rightNumber)
}
