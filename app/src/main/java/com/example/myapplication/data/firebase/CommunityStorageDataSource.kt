package com.example.myapplication.data.firebase

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import org.json.JSONObject
import java.io.IOException

class CommunityStorageDataSource(
    private val context: Context,
    private val cloudName: String,
    private val uploadPreset: String,
    private val client: OkHttpClient = OkHttpClient()
) {
    fun uploadCommunityMedia(
        mediaUri: Uri,
        mediaType: String,
        onSuccess: (String, String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val fileName = mediaUri.displayName() ?: "community-upload"
        val requestBody = mediaUri.asRequestBody(mediaType)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", uploadPreset)
            .addFormDataPart("file", fileName, requestBody)
            .build()
        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/auto/upload")
            .post(body)
            .build()

        Log.d(TAG, "Uploading community media to Cloudinary cloud=$cloudName preset=$uploadPreset file=$fileName contentType=$mediaType")

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Failed to upload community media to Cloudinary", e)
                    onError(IllegalStateException("Khong the tai tep len Cloudinary.", e))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val responseBody = it.body?.string().orEmpty()
                        if (!it.isSuccessful) {
                            Log.e(TAG, "Cloudinary upload failed code=${it.code} body=$responseBody")
                            onError(IllegalStateException("Cloudinary upload loi ${it.code}: $responseBody"))
                            return
                        }

                        val json = JSONObject(responseBody)
                        val secureUrl = json.optString("secure_url")
                        val resourceType = json.optString("resource_type")
                        if (secureUrl.isBlank()) {
                            onError(IllegalStateException("Cloudinary khong tra ve secure_url."))
                            return
                        }
                        val normalizedMediaType = when (resourceType) {
                            "image" -> "image/cloudinary"
                            "video" -> "video/cloudinary"
                            else -> mediaType
                        }
                        onSuccess(secureUrl, normalizedMediaType)
                    }
                }
            }
        )
    }

    private fun Uri.displayName(): String? {
        return context.contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
    }

    private fun Uri.asRequestBody(mediaType: String): RequestBody {
        val contentType = mediaType.toMediaTypeOrNull()
        return object : RequestBody() {
            override fun contentType() = contentType

            override fun contentLength(): Long {
                return context.contentResolver.openAssetFileDescriptor(this@asRequestBody, "r")
                    ?.use { it.length }
                    ?.takeIf { it >= 0L }
                    ?: -1L
            }

            override fun writeTo(sink: BufferedSink) {
                context.contentResolver.openInputStream(this@asRequestBody)?.use { input ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        sink.write(buffer, 0, read)
                    }
                } ?: throw IOException("Khong the doc tep da chon.")
            }
        }
    }

    private companion object {
        const val TAG = "CommunityStorage"
    }
}
