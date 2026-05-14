package com.example.myapplication.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirestoreMigrator {

    /**
     * Chỉ thực hiện xóa field 'tickets' cũ khỏi các document trong collection 'event'.
     * Field 'ticketTypes' đã được tạo ở lần chạy trước sẽ được giữ nguyên.
     */
    suspend fun migrateTicketData() {
        val db = Firebase.firestore
        val collectionRef = db.collection("event")

        try {
            val snapshot = collectionRef.get().await()
            Log.d("Migrator", "Bắt đầu dọn dẹp field cũ cho ${snapshot.size()} documents...")

            for (doc in snapshot.documents) {
                val docId = doc.id
                
                // Chỉ xóa field 'tickets', không ghi đè lại 'ticketTypes'
                val updates = mapOf(
                    "tickets" to FieldValue.delete()
                )

                db.collection("event").document(docId).update(updates).await()
                Log.d("Migrator", "Đã xóa field 'tickets' tại document: $docId")
            }
            Log.d("Migrator", "Hoàn tất việc dọn dẹp dữ liệu cũ thành công!")
        } catch (e: Exception) {
            Log.e("Migrator", "Lỗi khi dọn dẹp dữ liệu: ${e.message}", e)
        }
    }
}
