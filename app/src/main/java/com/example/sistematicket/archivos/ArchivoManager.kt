package com.example.sistematicket.archivos

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.sistematicket.data.Documento
import com.example.sistematicket.data.TiposArchivo
import java.io.File
import java.io.IOException

class ArchivoManager(private val context: Context) {

    data class InfoArchivo(val nombre: String, val tamano: Long, val tipoMime: String)

    private val carpeta: File
        get() = File(context.cacheDir, "documentos").apply { mkdirs() }

    fun obtenerInfo(uri: Uri): InfoArchivo {
        var nombre = "documento"
        var tamano = 0L
        context.contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(0)?.let { nombre = it }
                if (!cursor.isNull(1)) tamano = cursor.getLong(1)
            }
        }
        val tipo = context.contentResolver.getType(uri) ?: tipoPorExtension(nombre)
        return InfoArchivo(nombre, tamano, tipo)
    }

    fun copiarAlAlmacenamientoInterno(uri: Uri, nombre: String): File {
        val destino = nuevoArchivo(nombre)
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        } ?: throw IOException("No se pudo leer el archivo seleccionado")
        return destino
    }

    fun nuevoArchivo(nombre: String): File {
        val nombreSeguro = nombre.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val subcarpeta = File(carpeta, System.currentTimeMillis().toString()).apply { mkdirs() }
        return File(subcarpeta, nombreSeguro)
    }

    fun eliminar(ruta: String): Boolean {
        val archivo = File(ruta)
        val eliminado = archivo.delete()
        archivo.parentFile?.takeIf { it != carpeta && it.list().isNullOrEmpty() }?.delete()
        return eliminado
    }

    fun abrirConOtraApp(documento: Documento): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriCompartible(documento), documento.tipoMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    fun compartir(documento: Documento) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = documento.tipoMime
            putExtra(Intent.EXTRA_STREAM, uriCompartible(documento))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, "Compartir documento")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun uriCompartible(documento: Documento): Uri =
        FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", File(documento.ruta)
        )

    private fun tipoPorExtension(nombre: String): String =
        when (nombre.substringAfterLast('.', "").lowercase()) {
            "pdf" -> TiposArchivo.PDF
            "doc" -> TiposArchivo.DOC
            "docx" -> TiposArchivo.DOCX
            else -> "application/octet-stream"
        }
}
