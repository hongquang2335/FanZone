package com.example.myapplication.util

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirestoreMigrator {

    /**
     * Khởi tạo sub-collection 'seats' cho các document trong 'venue_templates'.
     * logic:
     * - rap_1: 6 hàng (A-F), 12 cột (1-12) -> 72 ghế.
     * - rap_2: 10 hàng (A-J), 10 cột (1-10) -> 100 ghế.
     * - zoneId: Hàng A, B, C là 'khan_dai_vip', còn lại là 'khan_dai_thuong'.
     */
    suspend fun seedVenueSeats() {
        val db = Firebase.firestore
        
        val venueConfigs = listOf(
            Triple("rap_1", 6, 12),
            Triple("rap_2", 10, 10)
        )

        try {
            for ((venueId, rows, cols) in venueConfigs) {
                val batch = db.batch()
                val venueRef = db.collection("venue_templates").document(venueId)
                val seatsRef = venueRef.collection("seats")

                Log.d("Migrator", "Bắt đầu tạo dữ liệu ghế cho $venueId...")

                for (r in 0 until rows) {
                    val rowLetter = ('A' + r).toString()
                    // Hàng A, B, C (index 0, 1, 2) là VIP
                    val zoneId = if (r < 3) "khan_dai_vip" else "khan_dai_thuong"

                    for (c in 1..cols) {
                        val seatId = "$rowLetter$c"
                        val seatData = mapOf(
                            "row" to rowLetter,
                            "col" to c,
                            "zoneId" to zoneId
                        )
                        // Thêm vào batch
                        batch.set(seatsRef.document(seatId), seatData)
                    }
                }
                
                // Thực thi batch và đợi kết quả
                val task: Task<Void> = batch.commit()
                task.await()
                Log.d("Migrator", "Đã commit thành công dữ liệu ghế cho $venueId")
            }
            Log.d("Migrator", "Hoàn tất khởi tạo toàn bộ dữ liệu ghế cho venue_templates!")
        } catch (e: Exception) {
            Log.e("Migrator", "Lỗi khi khởi tạo dữ liệu ghế: ${e.message}", e)
        }
    }

    /**
     * Khởi tạo collection 'event_seats' cho 10 sự kiện.
     * Tổng cộng: (5 * 72) + (5 * 100) = 860 documents.
     */
    suspend fun seedEventSeats() {
        val db = Firebase.firestore
        val eventSeatsRef = db.collection("event_seats")

        data class EventSeatConfig(
            val id: String,
            val venueType: String, // "rap_1" hoặc "rap_2"
            val vipPrice: Int,
            val normalPrice: Int
        )

        val events = listOf(
            // Rap 1 events (6 rows, 12 cols)
            EventSeatConfig("23663", "rap_1", 1100000, 30000),
            EventSeatConfig("24762", "rap_1", 800000, 400000),
            EventSeatConfig("25217", "rap_1", 1000000, 100000),
            EventSeatConfig("25541", "rap_1", 1100000, 100000),
            EventSeatConfig("25772", "rap_1", 1100000, 400000),
            // Rap 2 events (10 rows, 10 cols)
            EventSeatConfig("25795", "rap_2", 1900000, 200000),
            EventSeatConfig("25876", "rap_2", 1300000, 100000),
            EventSeatConfig("25897", "rap_2", 1100000, 400000),
            EventSeatConfig("25941", "rap_2", 800000, 400000),
            EventSeatConfig("26007", "rap_2", 1900000, 300000)
        )

        try {
            var currentBatch = db.batch()
            var operationCount = 0

            Log.d("Migrator", "Bắt đầu khởi tạo dữ liệu cho collection 'event_seats'...")

            for (event in events) {
                val rows = if (event.venueType == "rap_1") 6 else 10
                val cols = if (event.venueType == "rap_1") 12 else 10

                for (r in 0 until rows) {
                    val rowLetter = ('A' + r).toString()
                    val zoneId = if (r < 3) "khan_dai_vip" else "khan_dai_thuong"
                    val price = if (r < 3) event.vipPrice else event.normalPrice

                    for (c in 1..cols) {
                        val seatId = "$rowLetter$c"
                        val eventSeatId = "${event.id}_$seatId"

                        val data = mapOf(
                            "eventId" to event.id,
                            "seatId" to seatId,
                            "zoneId" to zoneId,
                            "price" to price,
                            "status" to "available",
                            "lockedUntil" to null,
                            "ownerId" to null
                        )

                        currentBatch.set(eventSeatsRef.document(eventSeatId), data)
                        operationCount++

                        // Commit nếu đạt giới hạn 500 operations
                        if (operationCount >= 500) {
                            val task: Task<Void> = currentBatch.commit()
                            task.await()
                            currentBatch = db.batch()
                            operationCount = 0
                            Log.d("Migrator", "Đã commit một batch (500 event_seats)...")
                        }
                    }
                }
            }

            // Commit các ghế còn lại
            if (operationCount > 0) {
                val task: Task<Void> = currentBatch.commit()
                task.await()
            }
            Log.d("Migrator", "Hoàn tất khởi tạo toàn bộ 860 ghế cho 10 sự kiện thành công!")
        } catch (e: Exception) {
            Log.e("Migrator", "Lỗi khi khởi tạo event_seats: ${e.message}", e)
        }
    }
}
