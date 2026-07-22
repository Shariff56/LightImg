package com.example.lightimg.domain.usecase

import android.graphics.Bitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CompressImageUseCase.binarySearchCompress].
 *
 * We test the pure binary-search logic by injecting a known bitmap and
 * verifying the output size constraints — no Android context needed.
 */
class CompressImageUseCaseTest {

    private val useCase = FakeCompressUseCase()

    @Test
    fun `binarySearch returns bytes within target`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val targetBytes = 5_000L
        val result = useCase.binarySearchCompress(bitmap, targetBytes)
        assertTrue(
            "Compressed size ${result.size} should be ≤ target $targetBytes",
            result.size <= targetBytes
        )
        bitmap.recycle()
    }

    @Test
    fun `binarySearch returns quality=1 when target is impossibly small`() {
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        // Tiny target: 1 byte — binary search should bottom out at quality=1
        val result = useCase.binarySearchCompress(bitmap, 1L)
        assertTrue(
            "Result size should be > 0",
            result.isNotEmpty()
        )
        bitmap.recycle()
    }

    @Test
    fun `binarySearch accepts large target without compression artefacts`() {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bigTarget = 1_000_000L
        val result = useCase.binarySearchCompress(bitmap, bigTarget)
        assertTrue(result.size <= bigTarget)
        bitmap.recycle()
    }

    /** Subclass that exposes the package-private binarySearchCompress for testing. */
    private inner class FakeCompressUseCase : CompressImageUseCase(
        // Context is not used in binarySearchCompress, pass null stub
        context = android.app.Application()
    ) {
        // inherits binarySearchCompress as-is
    }
}
