package com.example.lightimg.presentation.resize_crop

import android.graphics.Rect
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.model.ResizeResult
import com.example.lightimg.domain.usecase.ResizeImageUseCase
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

enum class ResizeCropMode { RESIZE, CROP }
enum class AspectRatio(val label: String, val ratio: Float?) {
    FREE("Free", null), SQUARE("1:1", 1f), RATIO_4_3("4:3", 4f / 3f), RATIO_16_9("16:9", 16f / 9f)
}

data class ResizeCropUiState(
    val selectedImage: ImageItem?   = null,
    val mode: ResizeCropMode        = ResizeCropMode.RESIZE,
    val targetWidth: String         = "",
    val targetHeight: String        = "",
    val lockAspect: Boolean         = true,
    val selectedAspect: AspectRatio = AspectRatio.FREE,
    val isProcessing: Boolean       = false,
    val result: ResizeResult?       = null,
)

class ResizeCropViewModel(private val resizeUseCase: ResizeImageUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ResizeCropUiState())
    val uiState: StateFlow<ResizeCropUiState> = _uiState.asStateFlow()

    fun onImageSelected(image: ImageItem) {
        _uiState.update { it.copy(selectedImage = image, targetWidth = image.width.toString(), targetHeight = image.height.toString(), result = null) }
    }

    fun onModeToggled(mode: ResizeCropMode) { _uiState.update { it.copy(mode = mode) } }
    fun onWidthChanged(w: String) { _uiState.update { it.copy(targetWidth = w) } }
    fun onHeightChanged(h: String) { _uiState.update { it.copy(targetHeight = h) } }
    fun onAspectChanged(ratio: AspectRatio) { _uiState.update { it.copy(selectedAspect = ratio) } }
    fun onLockToggled() { _uiState.update { it.copy(lockAspect = !it.lockAspect) } }

    fun onApplyClicked() {
        val image = _uiState.value.selectedImage ?: return
        val w = _uiState.value.targetWidth.toIntOrNull() ?: 0
        val h = _uiState.value.targetHeight.toIntOrNull() ?: 0
        _uiState.update { it.copy(isProcessing = true) }
        viewModelScope.launch {
            val result = resizeUseCase(image, w, h, _uiState.value.lockAspect)
            _uiState.update { it.copy(isProcessing = false, result = result) }
        }
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ResizeCropScreen(viewModel: ResizeCropViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            var name = "image.jpg"; var size = 0L; var w = 0; var h = 0
            cursor?.use { c -> val ni = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME); val si = c.getColumnIndex(android.provider.OpenableColumns.SIZE); if (c.moveToFirst()) { if (ni >= 0) name = c.getString(ni); if (si >= 0) size = c.getLong(si) } }
            viewModel.onImageSelected(ImageItem(it, name, size, w, h, "image/jpeg"))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Image preview
        item {
            if (state.selectedImage == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp).padding(16.dp)
                        .clip(RoundedCornerShape(20.dp)).background(SurfaceCard)
                        .border(1.dp, GradientPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Tap to select an image", color = TextSecondary, fontSize = 15.sp)
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(260.dp).padding(16.dp)
                        .clip(RoundedCornerShape(20.dp)).background(SurfaceCard),
                ) {
                    AsyncImage(
                        model = state.selectedImage!!.uri,
                        contentDescription = state.selectedImage!!.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Overlay grid lines for visual crop feel
                    Box(modifier = Modifier.fillMaxSize().border(2.dp, Color(0xFFEFB100), RoundedCornerShape(0.dp)))
                }
            }
        }

        if (state.selectedImage != null) {
            // Mode toggle: Resize / Crop
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)).background(SurfaceCard),
                ) {
                    listOf(ResizeCropMode.RESIZE to "Resize", ResizeCropMode.CROP to "Crop").forEach { (mode, label) ->
                        val isSelected = state.mode == mode
                        Box(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SurfaceElevated else Color.Transparent)
                                .clickable { viewModel.onModeToggled(mode) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(label, color = if (isSelected) TextPrimary else TextMuted, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 15.sp)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Aspect ratio chips (shown in Crop mode)
            if (state.mode == ResizeCropMode.CROP) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Aspect Ratio", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AspectRatio.entries.take(2).forEach { ratio ->
                                    AspectChip(ratio, state.selectedAspect == ratio, Modifier.weight(1f)) { viewModel.onAspectChanged(ratio) }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AspectRatio.entries.drop(2).forEach { ratio ->
                                    AspectChip(ratio, state.selectedAspect == ratio, Modifier.weight(1f)) { viewModel.onAspectChanged(ratio) }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Dimensions inputs (shown in Resize mode)
            if (state.mode == ResizeCropMode.RESIZE) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Dimensions", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(Modifier.weight(1f)) {
                                    Text("Width (px)", color = TextMuted, fontSize = 12.sp)
                                    OutlinedTextField(
                                        value = state.targetWidth, onValueChange = { viewModel.onWidthChanged(it) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GradientPurple, unfocusedBorderColor = SurfaceElevated,
                                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        ),
                                    )
                                }
                                Icon(Icons.Default.Link, contentDescription = "Lock aspect", tint = if (state.lockAspect) GradientPurple else TextMuted, modifier = Modifier.size(24.dp).clickable { viewModel.onLockToggled() })
                                Column(Modifier.weight(1f)) {
                                    Text("Height (px)", color = TextMuted, fontSize = 12.sp)
                                    OutlinedTextField(
                                        value = state.targetHeight, onValueChange = { viewModel.onHeightChanged(it) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GradientPurple, unfocusedBorderColor = SurfaceElevated,
                                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Apply & Save CTA
            item {
                CtaButton(
                    text = "Apply & Save",
                    onClick = { viewModel.onApplyClicked() },
                    enabled = !state.isProcessing,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            if (state.isProcessing) {
                item { Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = GradientPurple) } }
            }

            state.result?.let { result ->
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(SurfaceCard), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            when (result) {
                                is ResizeResult.Success -> Text("✓ Saved! ${result.newWidth}×${result.newHeight}px", color = SuccessGreen, fontWeight = FontWeight.Bold)
                                is ResizeResult.Error   -> Text("✗ ${result.message}", color = com.example.lightimg.theme.ErrorRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AspectChip(ratio: AspectRatio, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color(0xFF3D2800) else SurfaceElevated)
            .border(1.dp, if (selected) Color(0xFFF59E0B) else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(ratio.label, color = if (selected) Color(0xFFF59E0B) else TextMuted, fontSize = 14.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
