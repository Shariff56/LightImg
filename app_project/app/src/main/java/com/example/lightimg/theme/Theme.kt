package com.example.lightimg.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = GradientPurple,
    onPrimary        = Color.White,
    primaryContainer = IconContainerPurple,
    onPrimaryContainer = TextPrimary,
    secondary        = GradientBlue,
    onSecondary      = Color.White,
    secondaryContainer = IconContainerBlue,
    onSecondaryContainer = TextPrimary,
    background       = Background,
    onBackground     = TextPrimary,
    surface          = SurfaceCard,
    onSurface        = TextPrimary,
    surfaceVariant   = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error            = ErrorRed,
    onError          = Color.White,
    outline          = TextMuted,
)

@Composable
fun LightImgTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
