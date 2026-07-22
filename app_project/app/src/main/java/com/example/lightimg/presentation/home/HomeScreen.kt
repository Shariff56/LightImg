package com.example.lightimg.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightimg.R
import com.example.lightimg.core.navigation.Screen
import com.example.lightimg.theme.Background
import com.example.lightimg.theme.GradientPink
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SurfaceCard
import com.example.lightimg.theme.TextMuted
import com.example.lightimg.theme.TextPrimary
import com.example.lightimg.theme.TextSecondary

data class ActionCard(
    val screen: Screen,
    val title: String,
    val description: String,
    val iconResId: Int,
    val gradient: Brush,
)

@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cards = listOf(
        ActionCard(Screen.Compress,   "Compress",  "Reduce file size\nwithout losing quality.",  R.drawable.ic_compress, Brush.linearGradient(listOf(GradientPurple, GradientPink))),
        ActionCard(Screen.ResizeCrop, "Resize",    "Adjust dimensions\nfor any platform.",       R.drawable.ic_resize,   Brush.linearGradient(listOf(Color(0xFF3B82F6), GradientPurple))),
        ActionCard(Screen.Convert,    "Convert",   "Swap between\nJPG and PNG.",                 R.drawable.ic_convert,  Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF3B82F6)))),
        ActionCard(Screen.Pdf,        "PDF",       "Export images\nto a PDF document.",          R.drawable.ic_pdf,      Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))),
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item { HomeHeader(onSettingsClick = onSettingsClick) }
        item { HomeHeroText() }
        item {
            ActionCardsGrid(
                cards     = cards,
                onCardClick = { onNavigate(it.screen) },
                modifier  = Modifier.padding(horizontal = 16.dp),
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        // AdMob banner placeholder at bottom
        item { AdBannerPlaceholder() }
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ✦ Logo + Name
        Text(text = "✦ ", color = GradientPurple, fontSize = 20.sp)
        Text(
            text = "Light",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Img",
            color = GradientPink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun HomeHeroText() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "Fast. Precise. Local.",
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your premium toolbox for image perfection.",
            color = TextSecondary,
            fontSize = 15.sp,
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ActionCardsGrid(
    cards: List<ActionCard>,
    onCardClick: (ActionCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        for (row in cards.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { card ->
                    ActionFeatureCard(
                        card     = card,
                        onClick  = { onCardClick(card) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // If odd number of cards, fill remaining space
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ActionFeatureCard(
    card: ActionCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Gradient icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(card.gradient),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter            = painterResource(id = card.iconResId),
                    contentDescription = card.title,
                    tint               = Color.White,
                    modifier           = Modifier.size(26.dp),
                )
            }
            Column {
                Text(
                    text       = card.title,
                    color      = TextPrimary,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = card.description,
                    color    = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun AdBannerPlaceholder() {
    // This Box is replaced by the real AdMob BannerAd composable when ads are loaded.
    // Kept as a placeholder to maintain layout structure.
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text     = "SPONSORED CONTENT",
            color    = TextMuted,
            fontSize = 10.sp,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCard),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Ad", color = TextMuted, fontSize = 13.sp)
        }
    }
}
