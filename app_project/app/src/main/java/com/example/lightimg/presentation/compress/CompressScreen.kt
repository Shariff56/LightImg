package com.example.lightimg.presentation.compress

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lightimg.domain.model.BatchItemState
import com.example.lightimg.domain.model.CompressResult
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.presentation.components.CtaButton
import com.example.lightimg.presentation.components.ProgressRing
import com.example.lightimg.theme.Background
import com.example.lightimg.theme.ErrorRed
import com.example.lightimg.theme.GradientPink
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SuccessGreen
import com.example.lightimg.theme.SurfaceCard
import com.example.lightimg.theme.SurfaceElevated
import com.example.lightimg.theme.TextMuted
import com.example.lightimg.theme.TextPrimary
import com.example.lightimg.theme.TextSecondary

@Composable
fun CompressScreen(
    viewModel: CompressViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val items = uris.map { uri ->
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                var name = "image.jpg"
                var size = 0L
                cursor?.use {
                    val nameIdx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (it.moveToFirst()) {
                        if (nameIdx >= 0) name = it.getString(nameIdx)
                        if (sizeIdx >= 0) size = it.getLong(sizeIdx)
                    }
                }
                ImageItem(uri = uri, displayName = name, sizeBytes = size, width = 0, height = 0, mimeType = "image/jpeg")
            }
            viewModel.onImagesSelected(items)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Live preview banner
        item {
            LivePreviewBanner(state = state)
        }

        // Selected images grid header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Selected Files (${state.selectedImages.size})",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Row(
                    modifier = Modifier.clickable { imagePicker.launch("image/*") },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add More", tint = GradientPurple, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Add More", color = GradientPurple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Images grid (fixed height, not scrollable – inside LazyColumn)
        item {
            if (state.selectedImages.isEmpty()) {
                EmptyPickerSlot(onClick = { imagePicker.launch("image/*") })
            } else {
                ImageThumbnailGrid(
                    images       = state.selectedImages,
                    batchProgress = state.batchProgress,
                    onRemove     = { viewModel.onImageRemoved(it) },
                )
            }
        }

        // Compression settings card
        item {
            CompressionSettingsCard(
                targetSizeKb  = state.targetSizeKb,
                useKilobytes  = state.useKilobytes,
                onSizeChanged = { viewModel.onTargetSizeChanged(it) },
                onUnitToggled = { viewModel.onUnitToggled() },
            )
        }

        // CTA
        item {
            CtaButton(
                text    = "Compress & Save",
                onClick = { viewModel.onCompressClicked() },
                enabled = state.selectedImages.isNotEmpty() && !state.isProcessing,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }

    // Result bottom sheet
    if (state.showResultSheet) {
        CompressResultSheet(
            results   = state.results,
            onDismiss = { viewModel.onResultSheetDismissed() },
            onSave    = {
                viewModel.onSaveToDevice(context) { success ->
                    val msg = if (success) "Saved to Gallery!" else "Failed to save"
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
private fun LivePreviewBanner(state: CompressUiState) {
    val totalOriginal = state.selectedImages.sumOf { it.sizeBytes }
    val reduction = if (totalOriginal > 0) {
        val target = state.targetSizeKb * 1024L * state.selectedImages.size
        ((1f - target.coerceAtMost(totalOriginal).toFloat() / totalOriginal) * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text  = "COMPRESSION LIVE PREVIEW",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text           = formatBytes(totalOriginal),
                        color          = TextMuted,
                        fontSize       = 20.sp,
                        fontWeight     = FontWeight.Bold,
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Text(text = " → ", color = TextSecondary, fontSize = 18.sp)
                    Text(
                        text       = "${state.targetSizeKb} KB",
                        color      = SuccessGreen,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            if (reduction > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFF3D1A1A))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text       = "-$reduction%",
                        color      = ErrorRed,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnailGrid(
    images: List<ImageItem>,
    batchProgress: List<com.example.lightimg.domain.model.BatchProgress>,
    onRemove: (Uri) -> Unit,
) {
    val progressMap = batchProgress.associateBy { it.imageUri }

    // Use a fixed-height grid (non-scrollable inside LazyColumn)
    val rowCount = (images.size + 1) / 2
    val gridHeight = (rowCount * 180).dp

    LazyVerticalGrid(
        columns          = GridCells.Fixed(2),
        contentPadding   = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement   = Arrangement.spacedBy(12.dp),
        modifier         = Modifier.height(gridHeight),
        userScrollEnabled = false,
    ) {
        items(images, key = { it.uri.toString() }) { image ->
            val progress = progressMap[image.uri]
            ImageThumbnailCard(
                image    = image,
                state    = progress?.state ?: BatchItemState.PENDING,
                onRemove = { onRemove(image.uri) },
            )
        }
    }
}

@Composable
private fun ImageThumbnailCard(
    image: ImageItem,
    state: BatchItemState,
    onRemove: () -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard),
    ) {
        AsyncImage(
            model             = image.uri,
            contentDescription = image.displayName,
            contentScale      = ContentScale.Crop,
            modifier          = Modifier.fillMaxSize(),
        )

        // Filename overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xAA000000))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text     = image.displayName,
                color    = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
            )
        }

        // Remove button
        if (state == BatchItemState.PENDING) {
            IconButton(
                onClick  = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector       = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint              = Color.White,
                        modifier          = Modifier.size(12.dp),
                    )
                }
            }
        }

        // Processing overlay
        if (state == BatchItemState.PROCESSING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xBB000000)),
                contentAlignment = Alignment.Center,
            ) {
                ProgressRing(modifier = Modifier.size(48.dp), strokeWidth = 6f)
            }
        }

        // Done/Error indicator
        if (state == BatchItemState.DONE) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(SuccessGreen), contentAlignment = Alignment.Center) {
                    Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyPickerSlot(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, GradientPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Add, contentDescription = null, tint = GradientPurple, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text("Tap to select images", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun CompressionSettingsCard(
    targetSizeKb: Int,
    useKilobytes: Boolean,
    onSizeChanged: (Int) -> Unit,
    onUnitToggled: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape  = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Compression Settings", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Target Size:", color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    text       = "$targetSizeKb KB",
                    color      = GradientPurple,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(8.dp))

            Slider(
                value         = targetSizeKb.toFloat(),
                onValueChange = { onSizeChanged(it.toInt()) },
                valueRange    = 50f..5000f,
                colors        = SliderDefaults.colors(
                    thumbColor       = GradientPurple,
                    activeTrackColor = GradientPurple,
                    inactiveTrackColor = SurfaceElevated,
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("50 KB", color = TextMuted, fontSize = 12.sp)
                Text("5000 KB", color = TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CompressResultSheet(
    results: List<CompressResult>,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val successCount = results.filterIsInstance<CompressResult.Success>().size
    val totalSaved = results.filterIsInstance<CompressResult.Success>()
        .sumOf { it.originalSizeBytes - it.compressedSizeBytes }

    // Simple bottom sheet using Box overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("✓ Done!", color = SuccessGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("$successCount image(s) compressed · Saved ${formatBytes(totalSaved)}", color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.compose.material3.Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GradientPurple)
                    ) {
                        Text("Download", color = Color.White)
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Compress Another", color = GradientPurple)
                    }
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576f)
    bytes >= 1_024     -> "${bytes / 1_024} KB"
    else               -> "$bytes B"
}
