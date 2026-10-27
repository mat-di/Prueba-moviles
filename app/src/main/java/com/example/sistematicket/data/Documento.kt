package com.example.sistematicket.data

data class Documento(
    val id: Long,
    val usuarioId: Long,
    val ticketId: Long? = null,
    val nombre: String,
    val categoria: String,
    val tipoMime: String,
    val ruta: String,
    val tamano: Long,
    val fechaCarga: Long = System.currentTimeMillis()
) {
    val esPdf: Boolean get() = tipoMime == TiposArchivo.PDF
    val esWord: Boolean get() = tipoMime == TiposArchivo.DOC || tipoMime == TiposArchivo.DOCX
    val extension: String get() = nombre.substringAfterLast('.', "").uppercase()
}
