package com.example.lightimg.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightimg.theme.CTAGradientBrush
import com.example.lightimg.theme.PrimaryGradientBrush

/**
 * A full-width gradient CTA button with a press-scale micro-animation.
 *
 * @param text    Button label
 * @param onClick Click handler
 * @param brush   The gradient brush to use (defaults to purple→pink primary gradient)
 * @param enabled Whether the button is interactive
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = PrimaryGradientBrush,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(100.dp))
            .background(if (enabled) brush else Brush.horizontalGradient(listOf(Color(0xFF3A3A4A), Color(0xFF3A3A4A))))
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                enabled           = enabled,
                onClick           = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = text.uppercase(),
            color      = Color.White,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            letterSpacing = 1.sp,
        )
    }
}

/**
 * Orange→Red CTA gradient (used for Compress & Save, Create PDF, etc.)
 */
@Composable
fun CtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    GradientButton(
        text    = text,
        onClick = onClick,
        modifier = modifier,
        brush   = CTAGradientBrush,
        enabled = enabled,
    )
}
