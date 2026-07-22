package com.example.lightimg.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.lightimg.theme.GradientPink
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SurfaceElevated

/**
 * Circular progress ring composable.
 *
 * - When [progress] is null, shows an infinite spinning indeterminate ring.
 * - When [progress] is 0f–1f, shows a determinate arc.
 */
@Composable
fun ProgressRing(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    trackColor: Color = SurfaceElevated,
    progressColor: Color = GradientPurple,
    strokeWidth: Float = 8f,
) {
    if (progress == null) {
        IndeterminateRing(modifier, trackColor, progressColor, strokeWidth)
    } else {
        DeterminateRing(modifier, progress.coerceIn(0f, 1f), trackColor, progressColor, strokeWidth)
    }
}

@Composable
private fun IndeterminateRing(
    modifier: Modifier,
    trackColor: Color,
    progressColor: Color,
    strokeWidth: Float,
) {
    val transition = rememberInfiniteTransition(label = "ring_spin")
    val angle by transition.animateFloat(
        initialValue   = 0f,
        targetValue    = 360f,
        animationSpec  = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label          = "ring_angle",
    )

    Canvas(modifier = modifier) {
        val sw = strokeWidth
        drawArc(
            color      = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter  = false,
            style      = Stroke(width = sw, cap = StrokeCap.Round),
        )
        drawArc(
            color      = progressColor,
            startAngle = angle,
            sweepAngle = 90f,
            useCenter  = false,
            style      = Stroke(width = sw, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun DeterminateRing(
    modifier: Modifier,
    progress: Float,
    trackColor: Color,
    progressColor: Color,
    strokeWidth: Float,
) {
    Canvas(modifier = modifier) {
        val sw = strokeWidth
        drawArc(
            color      = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter  = false,
            style      = Stroke(width = sw, cap = StrokeCap.Round),
        )
        drawArc(
            color      = progressColor,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter  = false,
            style      = Stroke(width = sw, cap = StrokeCap.Round),
        )
    }
}
