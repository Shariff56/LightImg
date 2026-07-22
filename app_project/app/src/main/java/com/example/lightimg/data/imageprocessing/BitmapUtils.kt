package com.example.lightimg.data.imageprocessing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.annotation.WorkerThread

/**
 * Utility for safely decoding bitmaps without running into OutOfMemoryError
 * on high-resolution camera images (e.g. 108MP sensors).
 */
object BitmapUtils {

    /**
     * Reads image dimensions WITHOUT loading the full bitmap into memory.
     */
    @WorkerThread
    fun readDimensions(context: Context, uri: Uri): Pair<Int, Int> {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, opts)
        }
        return Pair(opts.outWidth, opts.outHeight)
    }

    /**
     * Decodes a bitmap downsampled to at most [maxDimension] on either axis.
     * Uses [BitmapFactory.Options.inSampleSize] to avoid OOM on large images.
     *
     * Returns null if the image cannot be decoded.
     */
    @WorkerThread
    fun decodeSampledBitmap(
        context: Context,
        uri: Uri,
        maxDimension: Int = 4096,
    ): Bitmap? {
        // Pass 1: read bounds only
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { s ->
            BitmapFactory.decodeStream(s, null, opts)
        }

        opts.inSampleSize = calculateInSampleSize(opts.outWidth, opts.outHeight, maxDimension)
        opts.inJustDecodeBounds = false
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888

        var bitmap = try {
            context.contentResolver.openInputStream(uri)?.use { s ->
                BitmapFactory.decodeStream(s, null, opts)
            }
        } catch (e: OutOfMemoryError) {
            // Retry with a larger sample size
            opts.inSampleSize = opts.inSampleSize * 2
            try {
                context.contentResolver.openInputStream(uri)?.use { s ->
                    BitmapFactory.decodeStream(s, null, opts)
                }
            } catch (e2: OutOfMemoryError) {
                null
            }
        } ?: return null

        var orientation = ExifInterface.ORIENTATION_NORMAL
        try {
            context.contentResolver.openInputStream(uri)?.use { s ->
                val exif = ExifInterface(s)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotateBitmapIfNeeded(bitmap, orientation)
    }

    /**
     * Calculates the largest [inSampleSize] value that is a power of 2
     * and keeps both dimensions ≤ [maxDimension].
     */
    fun calculateInSampleSize(
        rawWidth: Int,
        rawHeight: Int,
        maxDimension: Int,
    ): Int {
        var sampleSize = 1
        while ((rawWidth / sampleSize) > maxDimension || (rawHeight / sampleSize) > maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = android.graphics.Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.preScale(1.0f, -1.0f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.preScale(-1.0f, 1.0f)
                matrix.postRotate(270f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.preScale(-1.0f, 1.0f)
                matrix.postRotate(90f)
            }
            else -> return bitmap
        }
        return try {
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
            }
            rotated
        } catch (e: OutOfMemoryError) {
            bitmap.recycle()
            throw e
        }
    }

    /**
     * Returns true if the given MIME type is supported for loading.
     */
    fun isSupportedFormat(mimeType: String?): Boolean =
        mimeType in setOf("image/jpeg", "image/png", "image/webp", "image/gif")
}
