package com.example.lightimg.presentation.convert

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.lightimg.domain.model.ConvertResult
import com.example.lightimg.domain.model.ImageFormat
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.usecase.ConvertImageUseCase
import com.example.lightimg.presentation.components.CtaButton
import com.example.lightimg.theme.Background
import com.example.lightimg.theme.GradientPurple
import com.example.lightimg.theme.SuccessGreen
import com.example.lightimg.theme.SurfaceCard
import com.example.lightimg.theme.SurfaceElevated
import com.example.lightimg.theme.TextMuted
import com.example.lightimg.theme.TextPrimary
import com.example.lightimg.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── ViewModel ────────────────────────────────────────────────────────────────

data class ConvertUiState(
    val selectedImage: ImageItem? = null,
    val targetFormat: ImageFormat = ImageFormat.JPEG,
    val isProcessing: Boolean     = false,
    val result: ConvertResult?    = null,
)

class ConvertViewModel(private val convertUseCase: ConvertImageUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ConvertUiState())
    val uiState: StateFlow<ConvertUiState> = _uiState.asStateFlow()

    fun onImageSelected(image: ImageItem) {
        // Auto-select the opposite format
        val inferredFormat = if (image.mimeType == "image/png") ImageFormat.JPEG else ImageFormat.PNG
        _uiState.update { it.copy(selectedImage = image, targetFormat = inferredFormat, result = null) }
    }

    fun onImageCleared() { _uiState.update { it.copy(selectedImage = null, result = null) } }
    fun onFormatToggled(format: ImageFormat) { _uiState.update { it.copy(targetFormat = format) } }

    fun onConvertClicked() {
        val image = _uiState.value.selectedImage ?: return
        _uiState.update { it.copy(isProcessing = true) }
        viewModelScope.launch {
            val result = convertUseCase(image, _uiState.value.targetFormat)
            _uiState.update { it.copy(isProcessing = false, result = result) }
        }
    }

    fun onSaveToDevice(context: android.content.Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val result = _uiState.value.result
            var success = false
            if (result is ConvertResult.Success) {
                val mime = if (result.outputUri.toString().endsWith("png")) "image/png" else "image/jpeg"
                success = com.example.lightimg.data.files.ScopedStorageHelper.saveToMediaStore(context, result.outputUri, mime)
            }
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onComplete(success)
            }
        }
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ConvertScreen(viewModel: ConvertViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            var name = "image.jpg"; var size = 0L; var mime = "image/jpeg"
            cursor?.use { c ->
                val ni = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val si = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (c.moveToFirst()) { if (ni >= 0) name = c.getString(ni); if (si >= 0) size = c.getLong(si) }
            }
            mime = context.contentResolver.getType(it) ?: "image/jpeg"
            viewModel.onImageSelected(ImageItem(it, name, size, 0, 0, mime))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Text(
                text = "Selected Image",
                color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
            )
        }

        item {
            if (state.selectedImage == null) {
                // Pick slot
                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp)).background(SurfaceCard)
                        .border(1.dp, GradientPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Tap to select an image", color = TextSecondary, fontSize = 15.sp)
                }
            } else {
                SelectedImageCard(image = state.selectedImage!!, onClear = { viewModel.onImageCleared() })
            }
        }

        if (state.selectedImage != null) {
            item {
                Text(
                    "Convert to",
                    color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
                )
            }

            item {
                FormatToggle(
                    selected  = state.targetFormat,
                    onSelect  = { viewModel.onFormatToggled(it) },
                    modifier  = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                val hint = if (state.targetFormat == ImageFormat.JPEG)
                    "JPG uses lossy compression to significantly reduce file size while maintaining visual quality."
                else
                    "PNG is lossless — no quality loss, but file size will be larger."
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Text(hint, color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp), lineHeight = 20.sp)
                }
            }

            item {
                CtaButton(
                    text = "Convert & Save",
                    onClick = { viewModel.onConvertClicked() },
                    enabled = !state.isProcessing,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }

        if (state.isProcessing) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GradientPurple)
                }
            }
        }

        state.result?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape  = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        when (result) {
                            is ConvertResult.Success -> {
                                Text("✓ Converted successfully!", color = SuccessGreen, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                if (result.originalSizeBytes != null && result.convertedSizeBytes != null) {
                                    val savings = result.originalSizeBytes - result.convertedSizeBytes
                                    if (savings > 0) {
                                        Text("Saved ${formatBytes(savings)}", color = TextSecondary, fontSize = 14.sp)
                                    } else if (savings < 0) {
                                        Text("Size increased by ${formatBytes(-savings)}", color = TextSecondary, fontSize = 14.sp)
                                    } else {
                                        Text("No size change", color = TextSecondary, fontSize = 14.sp)
                                    }
                                } else {
                                    Text("Size information unavailable", color = TextSecondary, fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(16.dp))
                                androidx.compose.material3.Button(
                                    onClick = {
                                        viewModel.onSaveToDevice(context) { success ->
                                            val msg = if (success) "Saved to Gallery!" else "Failed to save"
                                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GradientPurple)
                                ) {
                                    Text("Download to Gallery", color = Color.White)
                                }
                            }
                            is ConvertResult.Error   -> Text("✗ ${result.message}", color = com.example.lightimg.theme.ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedImageCard(image: ImageItem, onClear: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(280.dp).padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp)).background(SurfaceCard),
    ) {
        AsyncImage(
            model = image.uri, contentDescription = image.displayName,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(Color(0xCC000000)).padding(12.dp),
        ) {
            Column {
                Text(image.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("${image.sizeBytes / 1024} KB", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
        IconButton(
            onClick = onClear,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp),
        ) {
            Box(Modifier.fillMaxSize().clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xAA000000)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun FormatToggle(selected: ImageFormat, onSelect: (ImageFormat) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceCard),
    ) {
        listOf(ImageFormat.JPEG to "JPG\nSmaller Size", ImageFormat.PNG to "PNG\nMax Quality").forEach { (fmt, label) ->
            val isSelected = selected == fmt
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) SurfaceElevated else Color.Transparent)
                    .clickable { onSelect(fmt) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) TextPrimary else TextMuted,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576f)
    bytes >= 1_024     -> "${bytes / 1_024} KB"
    else               -> "$bytes B"
}
