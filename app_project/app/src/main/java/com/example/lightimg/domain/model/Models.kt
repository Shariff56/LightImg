package com.example.lightimg.domain.model

import android.net.Uri

/** Represents a user-selected image with its metadata. */
data class ImageItem(
    val uri: Uri,
    val displayName: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val mimeType: String,
)

/** Result of a compression operation. */
sealed class CompressResult {
    data class Success(
        val outputUri: Uri,
        val originalSizeBytes: Long?,
        val compressedSizeBytes: Long?,
    ) : CompressResult()
    data class Error(val message: String) : CompressResult()
}

/** Result of a resize or crop operation. */
sealed class ResizeResult {
    data class Success(
        val outputUri: Uri,
        val newWidth: Int,
        val newHeight: Int,
    ) : ResizeResult()
    data class Error(val message: String) : ResizeResult()
}

/** Result of a format conversion. */
sealed class ConvertResult {
    data class Success(
        val outputUri: Uri,
        val newMimeType: String,
        val originalSizeBytes: Long?,
        val convertedSizeBytes: Long?,
    ) : ConvertResult()
    data class Error(val message: String) : ConvertResult()
}

/** Result of a PDF creation job. */
sealed class PdfResult {
    data class Success(
        val outputUri: Uri,
        val pageCount: Int,
    ) : PdfResult()
    data class Error(val message: String) : PdfResult()
}

/** Supported output image formats. */
enum class ImageFormat(val mimeType: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
}

/** Page size for PDF creation. */
enum class PdfPageSize(val label: String) {
    A4("A4 Standard"),
    LETTER("US Letter"),
    FIT_TO_IMAGE("Fit to Image"),
}

/** Processing progress for a single image in a batch. */
data class BatchProgress(
    val imageUri: Uri,
    val state: BatchItemState,
    val result: CompressResult? = null,
)

enum class BatchItemState { PENDING, PROCESSING, DONE, ERROR }
