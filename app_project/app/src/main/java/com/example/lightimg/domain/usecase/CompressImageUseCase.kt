package com.example.lightimg.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.lightimg.data.imageprocessing.BitmapUtils
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.CompressResult
import com.example.lightimg.domain.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Compresses an image to be at or below [targetSizeKb] kilobytes.
 *
 * Uses a binary-search over JPEG quality (1–95) to find the highest quality
 * that still fits within the target size. This provides "compress to exact size"
 * behaviour instead of a simple quality slider.
 *
 * All work is dispatched to [Dispatchers.Default] — safe to call from any coroutine.
 */
class CompressImageUseCase(private val context: Context) {

    /**
     * @param image       The source image to compress.
     * @param targetSizeKb  Maximum desired output size in kilobytes.
     * @return [CompressResult.Success] with the output URI, or [CompressResult.Error].
     */
    suspend operator fun invoke(
        image: ImageItem,
        targetSizeKb: Int,
    ): CompressResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = BitmapUtils.decodeSampledBitmap(context, image.uri)
                ?: return@withContext CompressResult.Error("Could not decode image: ${image.displayName}")

            val targetBytes = targetSizeKb * 1024L
            val compressedBytes = binarySearchCompress(bitmap, targetBytes)

            val baseName = ScopedStorageHelper.stripExtension(image.displayName)
            val output   = ScopedStorageHelper.createOutputFile(
                context, baseName, "_compressed", "jpg"
            ) ?: return@withContext CompressResult.Error("Could not create output file")

            val (uri, stream) = output
            stream.use { it.write(compressedBytes) }

            CompressResult.Success(
                outputUri            = uri,
                originalSizeBytes    = if (image.sizeBytes > 0) image.sizeBytes else null,
                compressedSizeBytes  = compressedBytes.size.toLong(),
            )
        } catch (e: OutOfMemoryError) {
            CompressResult.Error("Image too large to process on this device")
        } catch (e: Exception) {
            CompressResult.Error("Compression failed: ${e.message}")
        }
    }

    /**
     * Binary-search over JPEG quality 1–95 to find the highest quality
     * that produces output ≤ [targetBytes].
     *
     * If even quality=1 exceeds the target, returns quality=1 (best effort).
     */
    internal fun binarySearchCompress(bitmap: Bitmap, targetBytes: Long): ByteArray {
        var lo = 1
        var hi = 95
        var bestBytes = compressBitmap(bitmap, lo)

        while (lo <= hi) {
            val mid = (lo + hi) / 2
            val bytes = compressBitmap(bitmap, mid)
            if (bytes.size <= targetBytes) {
                bestBytes = bytes
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
        return bestBytes
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        return bos.toByteArray()
    }
}
