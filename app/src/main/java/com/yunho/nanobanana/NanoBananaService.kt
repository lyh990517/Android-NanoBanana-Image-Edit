package com.yunho.nanobanana

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class NanoBananaService(
    context: Context
) {
    private val client = OkHttpClient()
    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    var apiKey by mutableStateOf("")

    init {
        apiKey = sharedPreferences.getString("api_key", "") ?: ""
    }

    fun onSaveKey() {
        sharedPreferences.edit().apply {
            putString("api_key", apiKey)
            apply()
        }
    }

    suspend fun editImage(
        prompt: String,
        bitmaps: List<Bitmap>
    ): Bitmap? {
        val imageBase64List = bitmaps.map { bitmapToBase64(it) }

        return editImageWithPrompt(prompt, imageBase64List)
    }

    suspend fun editImageWithPrompt(
        prompt: String,
        imageBase64List: List<String>
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image-preview:generateContent"
            val media = "application/json; charset=utf-8".toMediaType()

            val bodyJson = buildString {
                append("{")
                append("\"contents\": [{")
                append("\"parts\":[")
                append("{ \"text\": \"$prompt\" }")

                imageBase64List.forEach { imageBase64 ->
                    if (imageBase64.isNotEmpty()) {
                        append(", { \"inline_data\": { \"mime_type\": \"image/jpeg\", \"data\": \"$imageBase64\" } }")
                    }
                }

                append("]")
                append("}]")
                append("}")
            }

            val body = bodyJson.toRequestBody(media)

            val req = Request.Builder()
                .url(url)
                .addHeader("x-goog-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            try {
                client.newCall(req).execute().use { response ->
                    val rawText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        Log.e("OkHttp", "HTTP ${response.code}: $rawText")
                        return@withContext null
                    }

                    Log.d("OkHttp", "Response: $rawText")

                    try {
                        val jsonResponse = JSONObject(rawText)
                        val candidatesArray = jsonResponse.optJSONArray("candidates")

                        if (candidatesArray != null && candidatesArray.length() > 0) {
                            val candidate = candidatesArray.getJSONObject(0)
                            val content = candidate.optJSONObject("content")
                            val partsArray = content?.optJSONArray("parts")

                            if (partsArray != null) {
                                for (i in 0 until partsArray.length()) {
                                    val part = partsArray.getJSONObject(i)
                                    val inlineData = part.optJSONObject("inlineData") ?: part.optJSONObject("inline_data")

                                    if (inlineData != null) {
                                        val base64Image = inlineData.optString("data")

                                        if (base64Image != null && base64Image.isNotEmpty()) {
                                            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

                                            return@withContext BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("OkHttp", "Failed to parse image from response: ", e)
                    }

                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("OkHttp", "Request failed: ", e)
                return@withContext null
            }
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    companion object {
        @Composable
        fun rememberNanoBananaService(context: Context) = remember { NanoBananaService(context) }
    }
}
