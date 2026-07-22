package com.example.lightimg.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightimg.R
import com.example.lightimg.core.navigation.Screen
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SurfaceCard
import com.example.lightimg.theme.TextMuted
import com.example.lightimg.theme.TextPrimary

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconResId: Int,
)

@Composable
fun LightImgBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
) {
    val items = listOf(
        BottomNavItem(Screen.Compress,   "Compress", R.drawable.ic_compress),
        BottomNavItem(Screen.ResizeCrop, "Resize",   R.drawable.ic_resize),
        BottomNavItem(Screen.Convert,    "Convert",  R.drawable.ic_convert),
        BottomNavItem(Screen.Pdf,        "PDF",      R.drawable.ic_pdf),
    )

    NavigationBar(
        containerColor = SurfaceCard,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val selected = currentScreen == item.screen
            NavigationBarItem(
                selected  = selected,
                onClick   = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        painter           = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text     = item.label,
                        fontSize = 12.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = GradientPurple,
                    selectedTextColor   = GradientPurple,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor      = Color(0xFF2D1B69),
                ),
            )
        }
    }
}
