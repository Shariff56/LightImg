package com.example.lightimg.data.files

import android.content.Context
import android.content.Intent
import android.net.Uri
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

            val dir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "LightImg"
            ).also { it.mkdirs() }

            val file = File(dir, fileName)
            val uri  = FileProvider.getUriForFile(context, AUTHORITY, file)
            Pair(uri, file.outputStream())
        } catch (e: Exception) {
            null
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
