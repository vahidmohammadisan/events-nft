package ir.vahidmohammadisan.lst.utils


import android.util.Log
import ir.vahidmohammadisan.lst.utils.Constants.IPFS_JSON_URL
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class IPFSJsonUploader(
    private val apiKey: String,
    private val jwtToken: String,
    private val json: JSONObject
) {

    fun pinJSONToIPFS(): String? {
        try {
            val client = OkHttpClient()

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(IPFS_JSON_URL)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $jwtToken")
                .build()

            val response = client.newCall(request).execute()

            return if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody
            } else {
                Log.e("Pinata", "API call failed with status code: ${response.code}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Pinata", "API call failed with exception: ${e.message}")
            return null
        }
    }
}
