package com.example.sistematicket.archivos

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.sistematicket.data.Ticket
import com.example.sistematicket.data.Usuario
import com.example.sistematicket.util.Formato
import java.io.File

object GeneradorPdf {

    private const val ANCHO = 595
    private const val ALTO = 842
    private const val MARGEN = 48f

    fun generarTicket(ticket: Ticket, usuario: Usuario, destino: File) {
        val pdf = PdfDocument()
        val escritor = Escritor(pdf)

        escritor.linea("Sistema de Tickets", tamano = 22f, negrita = true, color = Color.rgb(102, 80, 164))
        escritor.linea("Reporte generado el ${Formato.fecha(System.currentTimeMillis())}", tamano = 10f, color = Color.GRAY)
        escritor.espacio(16f)
        escritor.linea("Ticket #${ticket.id}: ${ticket.titulo}", tamano = 16f, negrita = true)
        escritor.espacio(8f)
        escritor.linea("Estado: ${ticket.estado}")
        escritor.linea("Prioridad: ${ticket.prioridad}")
        escritor.linea("Categoría: ${ticket.categoria}")
        escritor.linea("Creado: ${Formato.fecha(ticket.fechaCreacion)}")
        escritor.linea("Solicitante: ${usuario.nombre} (${usuario.correo})")
        escritor.espacio(16f)
        escritor.linea("Descripción", tamano = 13f, negrita = true)
        escritor.parrafo(ticket.descripcion)

        escritor.terminar()
        destino.outputStream().use { pdf.writeTo(it) }
        pdf.close()
    }

    private class Escritor(private val pdf: PdfDocument) {
        private var numeroPagina = 0
        private lateinit var pagina: PdfDocument.Page
        private lateinit var lienzo: Canvas
        private var y = 0f

        init {
            nuevaPagina()
        }

        private fun nuevaPagina() {
            numeroPagina++
            val info = PdfDocument.PageInfo.Builder(ANCHO, ALTO, numeroPagina).create()
            pagina = pdf.startPage(info)
            lienzo = pagina.canvas
            y = MARGEN
        }

        fun linea(texto: String, tamano: Float = 12f, negrita: Boolean = false, color: Int = Color.DKGRAY) {
            val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = tamano
                this.color = color
                typeface = if (negrita) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            }
            if (y + tamano > ALTO - MARGEN) {
                pdf.finishPage(pagina)
                nuevaPagina()
            }
            y += tamano
            lienzo.drawText(texto, MARGEN, y, pincel)
            y += tamano * 0.5f
        }

        fun parrafo(texto: String) {
            val pincel = Paint().apply { textSize = 12f }
            val anchoMaximo = ANCHO - MARGEN * 2
            texto.lines().forEach { renglon ->
                var actual = ""
                renglon.split(" ").forEach { palabra ->
                    val prueba = if (actual.isEmpty()) palabra else "$actual $palabra"
                    if (pincel.measureText(prueba) > anchoMaximo && actual.isNotEmpty()) {
                        linea(actual)
                        actual = palabra
                    } else {
                        actual = prueba
                    }
                }
                linea(actual)
            }
        }

        fun espacio(alto: Float) {
            y += alto
        }

        fun terminar() {
            pdf.finishPage(pagina)
        }
    }
}
