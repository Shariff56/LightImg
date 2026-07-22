package com.example.lightimg.presentation.resize_crop

import android.graphics.Rect
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import coil.compose.AsyncImage

@Composable
fun InteractiveCropCanvas(
    imageUri: Uri,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier,
    onCropRectChanged: (Rect) -> Unit
) {
    var boxSize by remember { mutableStateOf(Size.Zero) }

    // Normalized crop rect (0f to 1f) representing the cropped area relative to the IMAGE bounds.
    var cropLeft by remember { mutableFloatStateOf(0.1f) }
    var cropTop by remember { mutableFloatStateOf(0.1f) }
    var cropRight by remember { mutableFloatStateOf(0.9f) }
    var cropBottom by remember { mutableFloatStateOf(0.9f) }

    // Initialize crop rect once
    LaunchedEffect(imageUri) {
        val realLeft = (cropLeft * imageWidth).toInt()
        val realTop = (cropTop * imageHeight).toInt()
        val realRight = (cropRight * imageWidth).toInt()
        val realBottom = (cropBottom * imageHeight).toInt()
        onCropRectChanged(Rect(realLeft, realTop, realRight, realBottom))
    }

    // Calculate actual displayed image bounds within the Box (ContentScale.Fit)
    val imageAspect = if (imageHeight > 0) imageWidth.toFloat() / imageHeight else 1f
    val boxAspect = if (boxSize.height > 0) boxSize.width / boxSize.height else 1f

    var drawWidth = boxSize.width
    var drawHeight = boxSize.height
    var offsetX = 0f
    var offsetY = 0f

    if (boxSize.width > 0 && boxSize.height > 0) {
        if (imageAspect > boxAspect) {
            // Image is wider than box
            drawWidth = boxSize.width
            drawHeight = drawWidth / imageAspect
            offsetY = (boxSize.height - drawHeight) / 2f
        } else {
            // Image is taller than box
            drawHeight = boxSize.height
            drawWidth = drawHeight * imageAspect
            offsetX = (boxSize.width - drawWidth) / 2f
        }
    }

    // Convert normalized crop rect to Box coordinates
    val cropBoxLeft = offsetX + cropLeft * drawWidth
    val cropBoxTop = offsetY + cropTop * drawHeight
    val cropBoxRight = offsetX + cropRight * drawWidth
    val cropBoxBottom = offsetY + cropBottom * drawHeight

    // Function to update normalized coords from a delta in Box coords
    fun updateCrop(dx: Float, dy: Float, edge: String) {
        val normDx = if (drawWidth > 0) dx / drawWidth else 0f
        val normDy = if (drawHeight > 0) dy / drawHeight else 0f

        when (edge) {
            "TOP_LEFT" -> {
                cropLeft = (cropLeft + normDx).coerceIn(0f, cropRight - 0.05f)
                cropTop = (cropTop + normDy).coerceIn(0f, cropBottom - 0.05f)
            }
            "TOP_RIGHT" -> {
                cropRight = (cropRight + normDx).coerceIn(cropLeft + 0.05f, 1f)
                cropTop = (cropTop + normDy).coerceIn(0f, cropBottom - 0.05f)
            }
            "BOTTOM_LEFT" -> {
                cropLeft = (cropLeft + normDx).coerceIn(0f, cropRight - 0.05f)
                cropBottom = (cropBottom + normDy).coerceIn(cropTop + 0.05f, 1f)
            }
            "BOTTOM_RIGHT" -> {
                cropRight = (cropRight + normDx).coerceIn(cropLeft + 0.05f, 1f)
                cropBottom = (cropBottom + normDy).coerceIn(cropTop + 0.05f, 1f)
            }
            "CENTER" -> {
                val moveX = normDx.coerceIn(-cropLeft, 1f - cropRight)
                val moveY = normDy.coerceIn(-cropTop, 1f - cropBottom)
                cropLeft += moveX
                cropRight += moveX
                cropTop += moveY
                cropBottom += moveY
            }
        }

        // Notify parent with actual Bitmap pixel coordinates
        val realLeft = (cropLeft * imageWidth).toInt()
        val realTop = (cropTop * imageHeight).toInt()
        val realRight = (cropRight * imageWidth).toInt()
        val realBottom = (cropBottom * imageHeight).toInt()
        onCropRectChanged(Rect(realLeft, realTop, realRight, realBottom))
    }

    val currentCropBoxLeft by rememberUpdatedState(cropBoxLeft)
    val currentCropBoxTop by rememberUpdatedState(cropBoxTop)
    val currentCropBoxRight by rememberUpdatedState(cropBoxRight)
    val currentCropBoxBottom by rememberUpdatedState(cropBoxBottom)
    val currentUpdateCrop by rememberUpdatedState { dx: Float, dy: Float, edge: String -> updateCrop(dx, dy, edge) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                boxSize = Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())
            }
            .pointerInput(Unit) {
                var draggedEdge: String? = null
                detectDragGestures(
                    onDragStart = { offset ->
                        val touchRadius = 80f
                        draggedEdge = when {
                            offset.x in (currentCropBoxLeft - touchRadius)..(currentCropBoxLeft + touchRadius) &&
                            offset.y in (currentCropBoxTop - touchRadius)..(currentCropBoxTop + touchRadius) -> "TOP_LEFT"
                            
                            offset.x in (currentCropBoxRight - touchRadius)..(currentCropBoxRight + touchRadius) &&
                            offset.y in (currentCropBoxTop - touchRadius)..(currentCropBoxTop + touchRadius) -> "TOP_RIGHT"
                            
                            offset.x in (currentCropBoxLeft - touchRadius)..(currentCropBoxLeft + touchRadius) &&
                            offset.y in (currentCropBoxBottom - touchRadius)..(currentCropBoxBottom + touchRadius) -> "BOTTOM_LEFT"
                            
                            offset.x in (currentCropBoxRight - touchRadius)..(currentCropBoxRight + touchRadius) &&
                            offset.y in (currentCropBoxBottom - touchRadius)..(currentCropBoxBottom + touchRadius) -> "BOTTOM_RIGHT"
                            
                            offset.x in currentCropBoxLeft..currentCropBoxRight && offset.y in currentCropBoxTop..currentCropBoxBottom -> "CENTER"
                            
                            else -> null
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        draggedEdge?.let { edge ->
                            currentUpdateCrop(dragAmount.x, dragAmount.y, edge)
                        }
                    },
                    onDragEnd = { draggedEdge = null }
                )
            }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val overlayColor = Color(0xAA000000)

            // Top overlay
            drawRect(
                color = overlayColor,
                topLeft = Offset(0f, 0f),
                size = Size(boxSize.width, cropBoxTop)
            )
            // Bottom overlay
            drawRect(
                color = overlayColor,
                topLeft = Offset(0f, cropBoxBottom),
                size = Size(boxSize.width, boxSize.height - cropBoxBottom)
            )
            // Left overlay (between top and bottom)
            drawRect(
                color = overlayColor,
                topLeft = Offset(0f, cropBoxTop),
                size = Size(cropBoxLeft, cropBoxBottom - cropBoxTop)
            )
            // Right overlay
            drawRect(
                color = overlayColor,
                topLeft = Offset(cropBoxRight, cropBoxTop),
                size = Size(boxSize.width - cropBoxRight, cropBoxBottom - cropBoxTop)
            )

            // Draw crop box border
            drawRect(
                color = Color(0xFFEFB100),
                topLeft = Offset(cropBoxLeft, cropBoxTop),
                size = Size(cropBoxRight - cropBoxLeft, cropBoxBottom - cropBoxTop),
                style = Stroke(width = 4f)
            )

            // Draw corners
            val cornerLength = 40f
            val cornerColor = Color(0xFFEFB100)
            
            // Top Left
            drawLine(cornerColor, Offset(cropBoxLeft, cropBoxTop), Offset(cropBoxLeft + cornerLength, cropBoxTop), strokeWidth = 10f)
            drawLine(cornerColor, Offset(cropBoxLeft, cropBoxTop), Offset(cropBoxLeft, cropBoxTop + cornerLength), strokeWidth = 10f)
            
            // Top Right
            drawLine(cornerColor, Offset(cropBoxRight, cropBoxTop), Offset(cropBoxRight - cornerLength, cropBoxTop), strokeWidth = 10f)
            drawLine(cornerColor, Offset(cropBoxRight, cropBoxTop), Offset(cropBoxRight, cropBoxTop + cornerLength), strokeWidth = 10f)
            
            // Bottom Left
            drawLine(cornerColor, Offset(cropBoxLeft, cropBoxBottom), Offset(cropBoxLeft + cornerLength, cropBoxBottom), strokeWidth = 10f)
            drawLine(cornerColor, Offset(cropBoxLeft, cropBoxBottom), Offset(cropBoxLeft, cropBoxBottom - cornerLength), strokeWidth = 10f)
            
            // Bottom Right
            drawLine(cornerColor, Offset(cropBoxRight, cropBoxBottom), Offset(cropBoxRight - cornerLength, cropBoxBottom), strokeWidth = 10f)
            drawLine(cornerColor, Offset(cropBoxRight, cropBoxBottom), Offset(cropBoxRight, cropBoxBottom - cornerLength), strokeWidth = 10f)
        }
    }
}
