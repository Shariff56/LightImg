package com.example.lightimg.data.files

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles writing output files to the user's chosen folder or the default
 * app-managed Pictures/LightImg directory.
 *
 * Never overwrites the original file — always creates a new file.
 */
object ScopedStorageHelper {

    private const val AUTHORITY = "com.example.lightimg.fileprovider"

    /**
     * Creates a new output file in the default Pictures/LightImg directory
     * and returns an OutputStream to write into it.
     *
     * @param context   Application context
     * @param baseName  Original file name (without extension), used to derive output name
     * @param suffix    A suffix to append (e.g. "_compressed", "_resized")
     * @param extension File extension (e.g. "jpg", "png", "pdf")
     * @return Pair of (outputUri, OutputStream) — caller must close the stream
     */
    fun createOutputFile(
        context: Context,
        baseName: String,
        suffix: String,
        extension: String,
    ): Pair<Uri, OutputStream>? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName  = "${baseName}${suffix}_$timestamp.$extension"

            val dir = File(context.cacheDir, "LightImg_Cache").also { it.mkdirs() }

            val file = File(dir, fileName)
            val uri  = FileProvider.getUriForFile(context, AUTHORITY, file)
            Pair(uri, file.outputStream())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Copies a cached Uri to the public MediaStore.
     * @return True if successfully saved to MediaStore.
     */
    fun saveToMediaStore(context: Context, cachedUri: Uri, mimeType: String): Boolean {
        return try {
            val resolver = context.contentResolver
            // Get original filename from cache Uri
            var displayName = "LightImg_${System.currentTimeMillis()}"
            val cursor = resolver.query(cachedUri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) displayName = it.getString(idx)
                }
            }

            val isPdf = mimeType == "application/pdf"
            val collection = if (isPdf) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Files.getContentUri("external")
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val folder = if (isPdf) Environment.DIRECTORY_DOCUMENTS else Environment.DIRECTORY_PICTURES
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$folder/LightImg")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val destUri = resolver.insert(collection, values) ?: return false

            resolver.openInputStream(cachedUri)?.use { input ->
                resolver.openOutputStream(destUri)?.use { output ->
                    input.copyTo(output)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(destUri, values, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Creates a shareable URI for a file using FileProvider.
     */
    fun getShareableUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, AUTHORITY, file)

    /**
     * Builds a share Intent for a processed output file.
     */
    fun buildShareIntent(uri: Uri, mimeType: String): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    /**
     * Strips the extension from a file display name.
     * e.g. "IMG_001.jpg" → "IMG_001"
     */
    fun stripExtension(displayName: String): String =
        displayName.substringBeforeLast('.', displayName)
}
