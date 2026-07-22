package com.example.lightimg.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import com.example.lightimg.data.imageprocessing.BitmapUtils
import com.example.lightimg.data.files.ScopedStorageHelper
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.model.PdfPageSize
import com.example.lightimg.domain.model.PdfResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Creates a PDF document from one or more images using Android's built-in [PdfDocument] API.
 * No third-party PDF library needed.
 *
 * Page sizes:
 * - A4: 595 × 842 points (at 72 DPI)
 * - Letter: 612 × 792 points
 * - Fit to Image: page matches the image dimensions in points
 */
class CreatePdfUseCase(private val context: Context) {

    /**
     * @param images   Ordered list of source images (one page each).
     * @param pageSize The page dimensions to use.
     * @return [PdfResult.Success] with the output URI, or [PdfResult.Error].
     */
    suspend operator fun invoke(
        images: List<ImageItem>,
        pageSize: PdfPageSize,
        applyDocumentFilter: Boolean = false,
    ): PdfResult = withContext(Dispatchers.Default) {
        if (images.isEmpty()) return@withContext PdfResult.Error("No images provided")

        val pdfDocument = PdfDocument()
        var pageNumber  = 1

        try {
            for (image in images) {
                val bitmap = BitmapUtils.decodeSampledBitmap(context, image.uri, maxDimension = 2048)
                    ?: continue  // skip unreadable images; don't abort the whole batch

                val (pageW, pageH) = resolvePageDimensions(bitmap, pageSize)

                val pageInfo = PdfDocument.PageInfo.Builder(pageW, pageH, pageNumber).create()
                val page     = pdfDocument.startPage(pageInfo)

                drawBitmapCentered(page.canvas, bitmap, pageW, pageH, applyDocumentFilter)
                pdfDocument.finishPage(page)
                bitmap.recycle()
                pageNumber++
            }

            val actualPages = pageNumber - 1
            if (actualPages == 0) {
                return@withContext PdfResult.Error("No images could be read")
            }

            val output = ScopedStorageHelper.createOutputFile(
                context, "LightImg_PDF", "", "pdf"
            ) ?: return@withContext PdfResult.Error("Could not create output file")

            val (uri, stream) = output
            stream.use { pdfDocument.writeTo(it) }

            PdfResult.Success(outputUri = uri, pageCount = actualPages)
        } catch (e: OutOfMemoryError) {
            PdfResult.Error("Image too large to process for PDF on this device")
        } catch (e: Exception) {
            PdfResult.Error("PDF creation failed: ${e.message}")
        } finally {
            pdfDocument.close()
        }
    }

    private fun resolvePageDimensions(bitmap: Bitmap, pageSize: PdfPageSize): Pair<Int, Int> =
        when (pageSize) {
            PdfPageSize.A4          -> Pair(595, 842)
            PdfPageSize.LETTER      -> Pair(612, 792)
            PdfPageSize.FIT_TO_IMAGE -> Pair(bitmap.width, bitmap.height)
        }

    private fun drawBitmapCentered(canvas: Canvas, bitmap: Bitmap, pageW: Int, pageH: Int, applyDocumentFilter: Boolean) {
        canvas.drawColor(Color.WHITE)
        val scale  = minOf(pageW.toFloat() / bitmap.width, pageH.toFloat() / bitmap.height)
        val drawW  = (bitmap.width  * scale).toInt()
        val drawH  = (bitmap.height * scale).toInt()
        val left   = (pageW - drawW) / 2
        val top    = (pageH - drawH) / 2
        val dest   = Rect(left, top, left + drawW, top + drawH)
        
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        if (applyDocumentFilter) {
            val cm = android.graphics.ColorMatrix()
            cm.setSaturation(0f)
            val contrast = 1.5f
            val translate = -50f
            val contrastMatrix = android.graphics.ColorMatrix(floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
            cm.postConcat(contrastMatrix)
            paint.colorFilter = android.graphics.ColorMatrixColorFilter(cm)
        }
        
        canvas.drawBitmap(bitmap, null, dest, paint)
    }
}
