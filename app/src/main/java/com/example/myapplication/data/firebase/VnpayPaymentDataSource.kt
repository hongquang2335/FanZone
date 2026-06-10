package com.example.myapplication.data.firebase

import com.example.myapplication.BuildConfig
import com.example.myapplication.domain.model.EventSeat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class VnpayPaymentDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    suspend fun createPayment(
        eventId: String,
        selectedSeats: List<EventSeat>
    ): VnpayPaymentSession = authenticatedPost(
        endpointPath = CREATE_PAYMENT_PATH,
        body = JSONObject()
            .put("eventId", eventId)
            .put("seatIds", JSONArray(selectedSeats.map(EventSeat::id)))
    ) { json ->
        VnpayPaymentSession(
            paymentUrl = json.getString("paymentUrl"),
            txnRef = json.getString("txnRef")
        )
    }

    suspend fun getPaymentStatus(txnRef: String): VnpayPaymentStatus = authenticatedPost(
        endpointPath = PAYMENT_STATUS_PATH,
        body = JSONObject().put("txnRef", txnRef)
    ) { json ->
        VnpayPaymentStatus(
            txnRef = json.getString("txnRef"),
            status = json.getString("status"),
            responseCode = json.optString("responseCode").takeIf { it.isNotBlank() }
        )
    }

    private suspend fun <T> authenticatedPost(
        endpointPath: String,
        body: JSONObject,
        mapper: (JSONObject) -> T
    ): T {
        val backendBaseUrl = BuildConfig.VNPAY_BACKEND_URL.trimEnd('/')
        if (backendBaseUrl.contains("your-ngrok-domain")) {
            throw PaymentGatewayException(
                "Chưa cấu hình URL ngrok cho cổng thanh toán."
            )
        }
        val user = auth.currentUser ?: throw PaymentGatewayException(
            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        )
        val token = user.getIdToken(false).await().token
            ?: throw PaymentGatewayException("Không thể xác thực tài khoản.")

        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$backendBaseUrl$endpointPath")
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .post(body.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            client.newCall(request).execute().use { response ->
                val responseText = response.body?.string().orEmpty()
                val json = responseText.takeIf { it.isNotBlank() }
                    ?.let(::JSONObject)
                    ?: JSONObject()

                if (!response.isSuccessful) {
                    val serverError = json.optString("error")
                    throw PaymentGatewayException(serverError.toPaymentMessage())
                }
                mapper(json)
            }
        }
    }

    private fun String.toPaymentMessage(): String = when (this) {
        "seat_unavailable" -> "Một hoặc nhiều ghế đã được người khác mua. Vui lòng chọn lại."
        "seat_not_found", "seat_event_mismatch" -> "Thông tin ghế không còn hợp lệ."
        "event_not_found" -> "Không tìm thấy sự kiện."
        "unauthenticated" -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
        "payment_not_found" -> "Không tìm thấy giao dịch thanh toán."
        else -> "Không thể kết nối cổng thanh toán VNPAY. Vui lòng thử lại."
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        const val CREATE_PAYMENT_PATH = "/api/payments/vnpay"
        const val PAYMENT_STATUS_PATH = "/api/payments/vnpay/status"
    }
}

data class VnpayPaymentSession(
    val paymentUrl: String,
    val txnRef: String
)

data class VnpayPaymentStatus(
    val txnRef: String,
    val status: String,
    val responseCode: String?
)

class PaymentGatewayException(
    message: String,
    cause: Throwable? = null
) : IOException(message, cause)
