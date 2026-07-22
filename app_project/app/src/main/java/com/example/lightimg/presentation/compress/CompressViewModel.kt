package com.example.lightimg.presentation.compress

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightimg.domain.model.BatchItemState
import com.example.lightimg.domain.model.BatchProgress
import com.example.lightimg.domain.model.CompressResult
import com.example.lightimg.domain.model.ImageItem
import com.example.lightimg.domain.usecase.CompressImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompressUiState(
    val selectedImages: List<ImageItem>   = emptyList(),
    val targetSizeKb: Int                 = 500,
    val useKilobytes: Boolean             = true,
    val isProcessing: Boolean             = false,
    val batchProgress: List<BatchProgress> = emptyList(),
    val results: List<CompressResult>     = emptyList(),
    val showResultSheet: Boolean          = false,
    val errorMessage: String?             = null,
)

class CompressViewModel(
    private val compressUseCase: CompressImageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompressUiState())
    val uiState: StateFlow<CompressUiState> = _uiState.asStateFlow()

    fun onImagesSelected(images: List<ImageItem>) {
        _uiState.update { it.copy(selectedImages = it.selectedImages + images) }
    }

    fun onImageRemoved(uri: Uri) {
        _uiState.update { state ->
            state.copy(selectedImages = state.selectedImages.filterNot { it.uri == uri })
        }
    }

    fun onTargetSizeChanged(kb: Int) {
        _uiState.update { it.copy(targetSizeKb = kb.coerceAtLeast(1)) }
    }

    fun onUnitToggled() {
        _uiState.update { it.copy(useKilobytes = !it.useKilobytes) }
    }

    fun onCompressClicked() {
        val images = _uiState.value.selectedImages
        if (images.isEmpty()) return

        val initialProgress = images.map { img ->
            BatchProgress(imageUri = img.uri, state = BatchItemState.PENDING)
        }
        _uiState.update {
            it.copy(isProcessing = true, batchProgress = initialProgress, results = emptyList())
        }

        viewModelScope.launch {
            val results = mutableListOf<CompressResult>()

            images.forEachIndexed { index, image ->
                // Mark as PROCESSING
                _uiState.update { state ->
                    state.copy(
                        batchProgress = state.batchProgress.toMutableList().also { list ->
                            list[index] = list[index].copy(state = BatchItemState.PROCESSING)
                        }
                    )
                }

                val result = compressUseCase(image, _uiState.value.targetSizeKb)
                results.add(result)

                // Mark as DONE or ERROR
                _uiState.update { state ->
                    state.copy(
                        batchProgress = state.batchProgress.toMutableList().also { list ->
                            list[index] = list[index].copy(
                                state  = if (result is CompressResult.Success) BatchItemState.DONE else BatchItemState.ERROR,
                                result = result,
                            )
                        }
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isProcessing    = false,
                    results         = results,
                    showResultSheet = true,
                )
            }
        }
    }

    fun onResultSheetDismissed() {
        _uiState.update {
            it.copy(showResultSheet = false, selectedImages = emptyList(), batchProgress = emptyList(), results = emptyList())
        }
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onSaveToDevice(context: android.content.Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            var allSuccess = true
            var anySaved = false
            _uiState.value.results.forEach { result ->
                if (result is CompressResult.Success) {
                    val success = com.example.lightimg.data.files.ScopedStorageHelper.saveToMediaStore(context, result.outputUri, "image/jpeg")
                    if (success) {
                        anySaved = true
                    } else {
                        allSuccess = false
                    }
                }
            }
            val finalSuccess = anySaved && allSuccess
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onComplete(finalSuccess)
            }
        }
    }
}
