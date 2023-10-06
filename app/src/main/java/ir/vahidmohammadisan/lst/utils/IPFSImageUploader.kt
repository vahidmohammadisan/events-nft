import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import ir.vahidmohammadisan.lst.utils.Constants.IPFS_FILE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class IPFSImageUploader(
    private val context: Context,
    private val apiKey: String,
    private val jwtToken: String
) {

    suspend fun uploadImageToIPFS(imageBitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {

                val tempFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "temp_image.jpg"
                )

                val outputStream = FileOutputStream(tempFile)
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()

                val client = OkHttpClient()

                val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", tempFile.name, requestBody)
                    .build()

                val request = Request.Builder()
                    .url(IPFS_FILE_URL)
                    .post(multipartBody)
                    .header("Authorization", "Bearer $jwtToken")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    tempFile.delete()
                    responseBody
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
