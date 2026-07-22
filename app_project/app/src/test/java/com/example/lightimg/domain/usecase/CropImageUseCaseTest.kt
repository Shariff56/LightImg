package com.example.lightimg.domain.usecase

import android.graphics.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CropImageUseCase] rect clamping logic.
 */
class CropImageUseCaseTest {

    private val useCase = CropImageUseCase(context = android.app.Application())

    @Test
    fun `clamp keeps rect within bounds`() {
        val rect = Rect(-10, -5, 200, 300)
        val clamped = useCase.run { rect.clampTo(100, 100) }
        assertEquals(0, clamped.left)
        assertEquals(0, clamped.top)
        assertEquals(100, clamped.right)
        assertEquals(100, clamped.bottom)
    }

    @Test
    fun `clamp unchanged when rect is within bounds`() {
        val rect = Rect(10, 20, 80, 90)
        val clamped = useCase.run { rect.clampTo(100, 100) }
        assertEquals(rect, clamped)
    }

    @Test
    fun `empty rect after clamping`() {
        // Rect outside bounds entirely — left will equal right after clamping
        val rect = Rect(200, 200, 300, 300)
        val clamped = useCase.run { rect.clampTo(100, 100) }
        assertTrue(clamped.isEmpty)
    }
}
