package com.example.lightimg.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.lightimg.data.imageprocessing.BitmapUtils
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.model.ResizeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Resizes an image to the specified dimensions.
 *
 * If [keepAspectRatio] is true, the image is scaled so that neither side
 * exceeds [targetWidth] × [targetHeight] while preserving the original ratio.
 */
class ResizeImageUseCase(private val context: Context) {

    /**
     * @param image         Source image.
     * @param targetWidth   Desired width in pixels. Pass 0 to derive from height.
     * @param targetHeight  Desired height in pixels. Pass 0 to derive from width.
     * @param keepAspectRatio  Whether to maintain the original aspect ratio.
     */
    suspend operator fun invoke(
        image: ImageItem,
        targetWidth: Int,
        targetHeight: Int,
        keepAspectRatio: Boolean = true,
    ): ResizeResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = BitmapUtils.decodeSampledBitmap(context, image.uri)
                ?: return@withContext ResizeResult.Error("Could not decode image: ${image.displayName}")

            val (finalW, finalH) = calculateDimensions(
                srcW         = bitmap.width,
                srcH         = bitmap.height,
                targetW      = targetWidth,
                targetH      = targetHeight,
                keepAspect   = keepAspectRatio,
            )

            val resized = Bitmap.createScaledBitmap(bitmap, finalW, finalH, true)
            bitmap.recycle()

            val baseName = ScopedStorageHelper.stripExtension(image.displayName)
            val output   = ScopedStorageHelper.createOutputFile(
                context, baseName, "_resized", "jpg"
            ) ?: return@withContext ResizeResult.Error("Could not create output file")

            val (uri, stream) = output
            stream.use { resized.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            resized.recycle()

            ResizeResult.Success(outputUri = uri, newWidth = finalW, newHeight = finalH)
        } catch (e: OutOfMemoryError) {
            ResizeResult.Error("Image too large to process on this device")
        } catch (e: Exception) {
            ResizeResult.Error("Resize failed: ${e.message}")
        }
    }

    /**
     * Computes final (width, height) respecting constraints and aspect ratio.
     * Exported for unit testing.
     */
    internal fun calculateDimensions(
        srcW: Int,
        srcH: Int,
        targetW: Int,
        targetH: Int,
        keepAspect: Boolean,
    ): Pair<Int, Int> {
        if (!keepAspect) return Pair(targetW.coerceAtLeast(1), targetH.coerceAtLeast(1))

        val aspect = srcW.toFloat() / srcH.toFloat()
        return when {
            targetW > 0 && targetH > 0 -> {
                // Fit within box
                val byWidth  = Pair(targetW, (targetW / aspect).roundToInt())
                val byHeight = Pair((targetH * aspect).roundToInt(), targetH)
                if (byWidth.first <= targetW && byWidth.second <= targetH) byWidth else byHeight
            }
            targetW > 0 -> Pair(targetW, (targetW / aspect).roundToInt())
            targetH > 0 -> Pair((targetH * aspect).roundToInt(), targetH)
            else        -> Pair(srcW, srcH)
        }
    }
}
