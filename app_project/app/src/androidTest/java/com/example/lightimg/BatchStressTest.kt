package com.example.lightimg

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.usecase.CompressImageUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BatchStressTest {

    private val context: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun compress50LargeImagesWithoutOutOfMemoryError() = runBlocking {
        val useCase = CompressImageUseCase(context)
        val dummyImages = mutableListOf<ImageItem>()
        val outputUris = mutableListOf<Uri>()

        try {
            // Generate 50 dummy image files
            val cacheDir = context.cacheDir
            for (i in 1..50) {
                val file = File(cacheDir, "stress_test_$i.jpg")
                if (!file.exists()) {
                    val bitmap = Bitmap.createBitmap(3000, 4000, Bitmap.Config.ARGB_8888)
                    file.outputStream().use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    }
                    bitmap.recycle()
                }
                dummyImages.add(
                    ImageItem(
                        uri = Uri.fromFile(file),
                        displayName = "stress_test_$i.jpg",
                        sizeBytes = file.length(),
                        width = 3000,
                        height = 4000,
                        mimeType = "image/jpeg"
                    )
                )
            }

            // Process sequentially
            for (item in dummyImages) {
                val result = useCase(item, 500) // target 500KB
                assertTrue("Compression should succeed, but got $result", result is com.example.lightimg.domain.model.CompressResult.Success)
                val success = result as com.example.lightimg.domain.model.CompressResult.Success
                outputUris.add(success.outputUri)
            }
        } finally {
            // Cleanup inputs
            dummyImages.forEach {
                it.uri.path?.let { path -> File(path).delete() }
            }
            // Cleanup outputs
            outputUris.forEach { uri ->
                try {
                    context.contentResolver.delete(uri, null, null)
                } catch (e: Exception) {
                    // Ignore cleanup failures
                }
            }
        }
    }
}
