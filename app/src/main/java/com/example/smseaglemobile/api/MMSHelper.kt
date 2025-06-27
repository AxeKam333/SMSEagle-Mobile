package com.example.smseaglemobile.api

import java.io.File
import android.util.Base64

object MMSHelper {

    // Obsługiwane typy MIME
    object SupportedMimeTypes {
        const val JPEG = "image/jpeg"
        const val GIF = "image/gif"
        const val PNG = "image/png"
        const val BMP = "image/bmp"
    }

    // Funkcja do konwersji pliku na base64
    fun fileToBase64(filePath: String): String? {
        return try {
            val file = File(filePath)
            val bytes = file.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    // Funkcja do określenia typu MIME na podstawie rozszerzenia
    fun getMimeTypeFromExtension(fileName: String): String? {
        return when (fileName.substringAfterLast('.').lowercase()) {
            "jpg", "jpeg" -> SupportedMimeTypes.JPEG
            "gif" -> SupportedMimeTypes.GIF
            "png" -> SupportedMimeTypes.PNG
            "bmp" -> SupportedMimeTypes.BMP
            else -> null
        }
    }

    // Funkcja do tworzenia MMSContent z pliku
    fun createMMSContentFromFile(filePath: String): MMSContent? {
        val fileName = File(filePath).name
        val mimeType = getMimeTypeFromExtension(fileName) ?: return null
        val base64Content = fileToBase64(filePath) ?: return null

        return MMSContent(
            content_type = mimeType,
            content = base64Content
        )
    }

    // Funkcja do tworzenia MMSContent z ByteArray
    fun createMMSContentFromBytes(
        bytes: ByteArray,
        mimeType: String
    ): MMSContent {
        return MMSContent(
            content_type = mimeType,
            content = Base64.encodeToString(bytes, Base64.NO_WRAP)
        )
    }
}
