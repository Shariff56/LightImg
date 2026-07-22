package com.example.lightimg.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lightimg.theme.Background
import com.example.lightimg.theme.ErrorRed
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SuccessGreen
import com.example.lightimg.theme.SurfaceCard
import com.example.lightimg.theme.SurfaceElevated
import com.example.lightimg.theme.TextMuted
import com.example.lightimg.theme.TextPrimary
import com.example.lightimg.theme.TextSecondary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var compressionQuality by androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.75f) }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                }
                Text(
                    text = "Settings",
                    color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        // ── Storage & Output ──────────────────────────────────────────────────
        item {
            SectionLabel("STORAGE & OUTPUT")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp),
            ) {
                SettingsRow(
                    icon  = Icons.Default.Folder,
                    title = "Default Output Folder",
                    subtitle = "/Internal Storage/LightImg/Exports",
                    iconTint = Color(0xFFEF4444),
                    onClick = { /* TODO: SAF folder picker */ },
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Processing ────────────────────────────────────────────────────────
        item {
            SectionLabel("PROCESSING")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HighQuality, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                            Text("Compression Quality", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("Balance between file size and visual clarity", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Slider(
                        value = compressionQuality, onValueChange = { compressionQuality = it },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFF59E0B), activeTrackColor = Color(0xFFF59E0B), inactiveTrackColor = SurfaceElevated),
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("FAST (LOW)", color = TextMuted, fontSize = 11.sp)
                        Spacer(Modifier.weight(1f))
                        Text("STANDARD", color = TextMuted, fontSize = 11.sp)
                        Spacer(Modifier.weight(1f))
                        Text("LOSSLESS (HIGH)", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Ads & Privacy ─────────────────────────────────────────────────────
        item {
            SectionLabel("ADS & PRIVACY")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp),
            ) {
                SettingsRow(
                    icon     = Icons.Default.Policy,
                    title    = "Consent & Ad Preferences",
                    subtitle = "Manage your ad personalisation settings",
                    iconTint = Color(0xFFEF4444),
                    onClick  = { /* TODO: open UMP consent form */ },
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── About ─────────────────────────────────────────────────────────────
        item {
            SectionLabel("ABOUT")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp),
            ) {
                // Privacy trust badge
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp).padding(top = 2.dp))
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text("Local Processing Only", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "No data collected. Your files never leave your device. Ads are served by Google AdMob per Google's own privacy policy.",
                                color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp,
                            )
                        }
                    }
                }
                androidx.compose.material3.HorizontalDivider(color = SurfaceElevated, thickness = 1.dp)
                SettingsRow(
                    icon  = Icons.Default.Info,
                    title = "Version",
                    subtitle = "LightImg 1.0.0",
                    iconTint = TextMuted,
                    onClick = {},
                    showChevron = false,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFEF4444),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit,
    showChevron: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        if (showChevron) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}
