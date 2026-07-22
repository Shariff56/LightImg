package com.example.lightimg.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Background & Surface ────────────────────────────────────────────────────
val Background     = Color(0xFF0E0E12)
val SurfaceCard    = Color(0xFF1A1A22)
val SurfaceElevated = Color(0xFF22222E)

// ── Primary Gradient (purple → pink) ────────────────────────────────────────
val GradientPurple = Color(0xFF7C3AED)
val GradientPink   = Color(0xFFEC4899)

// ── Secondary Gradient (blue → purple) ──────────────────────────────────────
val GradientBlue   = Color(0xFF3B82F6)

// ── Text ────────────────────────────────────────────────────────────────────
val TextPrimary    = Color(0xFFF5F5F7)
val TextSecondary  = Color(0xFFA0A0AC)
val TextMuted      = Color(0xFF5A5A6A)

// ── Semantic ────────────────────────────────────────────────────────────────
val SuccessGreen   = Color(0xFF34D399)
val ErrorRed       = Color(0xFFF87171)
val WarningAmber   = Color(0xFFFBBF24)

// ── Icon Container tints ─────────────────────────────────────────────────────
val IconContainerPurple = Color(0xFF2D1B69)
val IconContainerBlue   = Color(0xFF1E3A5F)

// ── Brushes ──────────────────────────────────────────────────────────────────
val PrimaryGradientBrush = Brush.horizontalGradient(
    colors = listOf(GradientPurple, GradientPink)
)

val SecondaryGradientBrush = Brush.horizontalGradient(
    colors = listOf(GradientBlue, GradientPurple)
)

val CTAGradientBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
)
