package com.example.lightimg.presentation.pdf

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.model.PdfPageSize
import com.example.lightimg.domain.model.PdfResult
import com.example.lightimg.domain.usecase.CreatePdfUseCase
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

data class PdfUiState(
    val images: List<ImageItem>  = emptyList(),
    val pageSize: PdfPageSize    = PdfPageSize.A4,
    val isProcessing: Boolean    = false,
    val result: PdfResult?       = null,
)

class PdfViewModel(private val createPdfUseCase: CreatePdfUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(PdfUiState())
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    fun onImagesAdded(images: List<ImageItem>) {
        _uiState.update { it.copy(images = it.images + images) }
    }

    fun onImageRemoved(index: Int) {
        _uiState.update { state ->
            state.copy(images = state.images.toMutableList().also { it.removeAt(index) })
        }
    }

    fun onPageSizeChanged(size: PdfPageSize) {
        _uiState.update { it.copy(pageSize = size) }
    }

    fun onCreatePdfClicked() {
        val images = _uiState.value.images
        if (images.isEmpty()) return
        _uiState.update { it.copy(isProcessing = true) }
        viewModelScope.launch {
            val result = createPdfUseCase(images, _uiState.value.pageSize)
            _uiState.update { it.copy(isProcessing = false, result = result) }
        }
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PdfScreen(viewModel: PdfViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val items = uris.mapIndexed { _, uri ->
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                var name = "image.jpg"; var size = 0L
                cursor?.use { c -> val ni = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME); val si = c.getColumnIndex(android.provider.OpenableColumns.SIZE); if (c.moveToFirst()) { if (ni >= 0) name = c.getString(ni); if (si >= 0) size = c.getLong(si) } }
                ImageItem(uri, name, size, 0, 0, "image/jpeg")
            }
            viewModel.onImagesAdded(items)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // Header with page count
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("PDF Creator", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (state.images.isNotEmpty()) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(100.dp)).background(Color(0xFF3D2800)).padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("📄 ${state.images.size} PAGES", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Page size chips
        item {
            Text("PAGE SIZE SETTINGS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PdfPageSize.entries.forEach { size ->
                    val isSelected = state.pageSize == size
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) Color(0xFFF59E0B) else SurfaceCard)
                            .clickable { viewModel.onPageSizeChanged(size) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = size.label,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Images grid
        if (state.images.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp).padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)).background(SurfaceCard)
                        .border(1.dp, GradientPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GradientPurple, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Add images for PDF", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        } else {
            item {
                val rowCount = (state.images.size + 1) / 2
                val gridHeight = (rowCount * 180 + 50).dp
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(gridHeight),
                    userScrollEnabled = false,
                ) {
                    itemsIndexed(state.images) { index, image ->
                        PdfImageTile(image = image, pageNumber = index + 1, onRemove = { viewModel.onImageRemoved(index) })
                    }
                }
            }
        }

        // Add more
        if (state.images.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)).background(SurfaceCard)
                        .clickable { imagePicker.launch("image/*") }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = GradientPurple, modifier = Modifier.size(20.dp))
                    Text("  Add Images", color = GradientPurple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // CTA
        item {
            CtaButton(
                text     = "Create PDF",
                onClick  = { viewModel.onCreatePdfClicked() },
                enabled  = state.images.isNotEmpty() && !state.isProcessing,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                            is PdfResult.Success -> Text("✓ PDF created! (${result.pageCount} pages)", color = SuccessGreen, fontWeight = FontWeight.Bold)
                            is PdfResult.Error   -> Text("✗ ${result.message}", color = com.example.lightimg.theme.ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfImageTile(image: ImageItem, pageNumber: Int, onRemove: () -> Unit) {
    Box(
        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(SurfaceCard),
    ) {
        AsyncImage(model = image.uri, contentDescription = image.displayName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        // Page number badge
        Box(
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                .clip(RoundedCornerShape(6.dp)).background(Color(0xAA000000)).padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text("%02d".format(pageNumber), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp).size(32.dp),
        ) {
            Box(Modifier.fillMaxSize().clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xAACC3333)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}
