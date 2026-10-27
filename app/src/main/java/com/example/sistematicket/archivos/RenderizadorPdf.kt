package com.example.sistematicket.archivos

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File

class RenderizadorPdf(archivo: File) : Closeable {

    private val descriptor = ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY)
    private val renderer = PdfRenderer(descriptor)
    private var cerrado = false

    val totalPaginas: Int = renderer.pageCount

    suspend fun renderizar(indice: Int, anchoPx: Int): Bitmap? = withContext(Dispatchers.IO) {
        synchronized(this@RenderizadorPdf) {
            if (cerrado) return@withContext null
            renderer.openPage(indice).use { pagina ->
                val altoPx = anchoPx * pagina.height / pagina.width
                val imagen = Bitmap.createBitmap(anchoPx, altoPx, Bitmap.Config.ARGB_8888)
                imagen.eraseColor(Color.WHITE)
                pagina.render(imagen, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imagen
            }
        }
    }

    override fun close() {
        synchronized(this) {
            if (!cerrado) {
                cerrado = true
                renderer.close()
                descriptor.close()
            }
        }
    }
}
