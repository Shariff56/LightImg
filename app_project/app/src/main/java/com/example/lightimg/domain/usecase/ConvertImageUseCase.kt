package com.example.lightimg.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.lightimg.data.imageprocessing.BitmapUtils
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.ConvertResult
import com.example.lightimg.domain.model.ImageFormat
import com.example.lightimg.domain.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Converts an image between JPEG and PNG formats.
 *
 * All decoding and encoding is performed on [Dispatchers.Default].
 * The original file is never modified.
 */
class ConvertImageUseCase(private val context: Context) {

    /**
     * @param image        Source image.
     * @param targetFormat The desired output format.
     * @return [ConvertResult.Success] with the output URI, or [ConvertResult.Error].
     */
    suspend operator fun invoke(
        image: ImageItem,
        targetFormat: ImageFormat,
    ): ConvertResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = BitmapUtils.decodeSampledBitmap(context, image.uri)
                ?: return@withContext ConvertResult.Error("Could not decode image: ${image.displayName}")

            val (format, quality) = when (targetFormat) {
                ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG to 90
                ImageFormat.PNG  -> Bitmap.CompressFormat.PNG  to 100
            }

            val baseName = ScopedStorageHelper.stripExtension(image.displayName)
            val output   = ScopedStorageHelper.createOutputFile(
                context, baseName, "_converted", targetFormat.extension
            ) ?: return@withContext ConvertResult.Error("Could not create output file")

            val (uri, stream) = output
            stream.use { bitmap.compress(format, quality, it) }
            bitmap.recycle()

            ConvertResult.Success(outputUri = uri, newMimeType = targetFormat.mimeType)
        } catch (e: OutOfMemoryError) {
            ConvertResult.Error("Image too large to convert on this device")
        } catch (e: Exception) {
            ConvertResult.Error("Conversion failed: ${e.message}")
        }
    }
}
