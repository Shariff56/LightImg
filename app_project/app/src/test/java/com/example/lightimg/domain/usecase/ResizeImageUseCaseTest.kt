package com.example.lightimg.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [ResizeImageUseCase.calculateDimensions].
 * Pure Kotlin logic — no Android framework needed.
 */
class ResizeImageUseCaseTest {

    private val useCase = ResizeImageUseCase(context = android.app.Application())

    @Test
    fun `exact dimensions without aspect ratio`() {
        val (w, h) = useCase.calculateDimensions(
            srcW = 1920, srcH = 1080, targetW = 800, targetH = 600, keepAspect = false
        )
        assertEquals(800, w)
        assertEquals(600, h)
    }

    @Test
    fun `only width given with aspect ratio`() {
        val (w, h) = useCase.calculateDimensions(
            srcW = 1920, srcH = 1080, targetW = 960, targetH = 0, keepAspect = true
        )
        assertEquals(960, w)
        assertEquals(540, h)
    }

    @Test
    fun `only height given with aspect ratio`() {
        val (w, h) = useCase.calculateDimensions(
            srcW = 1080, srcH = 1920, targetW = 0, targetH = 480, keepAspect = true
        )
        assertEquals(270, w)
        assertEquals(480, h)
    }

    @Test
    fun `fit within box respects both constraints`() {
        // Landscape image 1920x1080 fitted into 800x800 box
        val (w, h) = useCase.calculateDimensions(
            srcW = 1920, srcH = 1080, targetW = 800, targetH = 800, keepAspect = true
        )
        assertTrue(w <= 800 && h <= 800)
    }

    @Test
    fun `no target returns original size`() {
        val (w, h) = useCase.calculateDimensions(
            srcW = 1920, srcH = 1080, targetW = 0, targetH = 0, keepAspect = true
        )
        assertEquals(1920, w)
        assertEquals(1080, h)
    }

    private fun assertTrue(b: Boolean) = org.junit.Assert.assertTrue(b)
}
