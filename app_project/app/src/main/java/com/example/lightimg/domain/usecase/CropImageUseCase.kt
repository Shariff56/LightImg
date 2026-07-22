package com.example.lightimg.domain.usecase

import android.content.Context
import android.graphics.Rect
import com.example.lightimg.data.imageprocessing.BitmapUtils
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.model.ResizeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Crops an image to the specified rectangle in pixel coordinates.
 *
 * [cropRect] is in the coordinate space of the decoded bitmap.
 * Caller should map from the UI preview coordinates before invoking.
 */
class CropImageUseCase(private val context: Context) {

    /**
     * @param image    Source image.
     * @param cropRect Crop rectangle in bitmap pixel coordinates (left, top, right, bottom).
     */
    suspend operator fun invoke(
        image: ImageItem,
        cropRect: Rect,
    ): ResizeResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = BitmapUtils.decodeSampledBitmap(context, image.uri)
                ?: return@withContext ResizeResult.Error("Could not decode image: ${image.displayName}")

            val safeRect = cropRect.clampTo(bitmap.width, bitmap.height)
            if (safeRect.isEmpty) {
                return@withContext ResizeResult.Error("Invalid crop region")
            }

            val cropped = Bitmap.createBitmap(
                bitmap,
                safeRect.left,
                safeRect.top,
                safeRect.width(),
                safeRect.height(),
            )
            bitmap.recycle()

            val baseName = ScopedStorageHelper.stripExtension(image.displayName)
            val output   = ScopedStorageHelper.createOutputFile(
                context, baseName, "_cropped", "jpg"
            ) ?: return@withContext ResizeResult.Error("Could not create output file")

            val (uri, stream) = output
            stream.use { cropped.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            cropped.recycle()

            ResizeResult.Success(
                outputUri = uri,
                newWidth  = safeRect.width(),
                newHeight = safeRect.height(),
            )
        } catch (e: OutOfMemoryError) {
            ResizeResult.Error("Image too large to crop on this device")
        } catch (e: Exception) {
            ResizeResult.Error("Crop failed: ${e.message}")
        }
    }

    /** Clamps a [Rect] to fit within the given [maxWidth] × [maxHeight]. */
    internal fun Rect.clampTo(maxWidth: Int, maxHeight: Int): Rect = Rect(
        left.coerceIn(0, maxWidth),
        top.coerceIn(0, maxHeight),
        right.coerceIn(0, maxWidth),
        bottom.coerceIn(0, maxHeight),
    )
}
