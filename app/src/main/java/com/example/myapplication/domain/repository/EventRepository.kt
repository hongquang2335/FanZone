package com.example.myapplication.domain.repository
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import com.example.myapplication.BuildConfig

object EventRepository {
    private val ALGOLIA_APP_ID = BuildConfig.ALGOLIA_APP_ID
    private val ALGOLIA_API_KEY = BuildConfig.ALGOLIA_API_KEY
    private const val ALGOLIA_INDEX = "event"

    suspend fun searchEvents(
        artistName: String? = null,
        title: String? = null,
        address: String? = null,
        month: String? = null  // Format: "2026-08" để tìm tháng
    ): JsonObject {
        Log.d("EventRepo", "🔍 searchEvents gọi với: artist=$artistName, title=$title, address=$address, month=$month")
        return try {
            val queries = mutableListOf<String>()

            // Xây dựng query string
            if (!artistName.isNullOrEmpty()) {
                queries.add(artistName)
            }
            if (!title.isNullOrEmpty()) {
                queries.add(title)
            }
            if (!address.isNullOrEmpty()) {
                queries.add(address)
            }

            // Nếu có month, filter startTime
            //val facetFilters = mutableListOf<String>()
            if (!month.isNullOrEmpty()) {
                queries.add(month)
            }

            val queryString = queries.joinToString(" ")
            Log.d("EventRepo", "📡 Query string tạo ra: '$queryString'")

            val results = callAlgoliaAPI(queryString)
            Log.d("EventRepo", "✅ Kết quả tìm kiếm thành công")
            results
        } catch (e: Exception) {
            Log.e("EventRepo", "❌ Lỗi trong searchEvents: ${e.message}", e)
            JsonObject(
                mapOf(
                    "error" to JsonPrimitive("Không thể tìm kiếm sự kiện: ${e.message}")
                )
            )
        }
    }

    private suspend fun callAlgoliaAPI(
        query: String,
//        facetFilters: List<String>?
    ): JsonObject = withContext(Dispatchers.IO) {
        Log.d("EventRepo", "🌐 Đang gọi Algolia API cho query: '$query'")
        val client = OkHttpClient()
        val requestBody = JsonObject(
            mapOf(
                "query" to JsonPrimitive(query),
//                "facetFilters" to JsonArray(facetFilters.map { JsonPrimitive(it) }),
                "hitsPerPage" to JsonPrimitive(10)
            )
        ).toString()

        val request = Request.Builder()
            .url("https://$ALGOLIA_APP_ID-dsn.algolia.net/1/indexes/$ALGOLIA_INDEX/query")
            .header("X-Algolia-API-Key", ALGOLIA_API_KEY)
            .header("X-Algolia-Application-Id", ALGOLIA_APP_ID)
            .post(RequestBody.create("application/json".toMediaType(), requestBody))
            .build()

        client.newCall(request).execute().use { response ->
            val json = response.body?.string() ?: "{}"
            Log.d("EventRepo", "📥 Raw Response từ Algolia: $json")
            Json.parseToJsonElement(json).jsonObject
        }
    }
}