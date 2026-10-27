package com.example.sistematicket.data

object TiposArchivo {
    const val PDF = "application/pdf"
    const val DOC = "application/msword"
    const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    val PERMITIDOS = arrayOf(PDF, DOC, DOCX)
    const val TAMANO_MAXIMO = 10L * 1024 * 1024
}

object Catalogo {
    const val ABIERTO = "Abierto"
    const val EN_PROCESO = "En proceso"
    const val CERRADO = "Cerrado"

    val ESTADOS = listOf(ABIERTO, EN_PROCESO, CERRADO)
    val PRIORIDADES = listOf("Baja", "Media", "Alta")
    val CATEGORIAS = listOf(
        "Soporte técnico", "Software", "Hardware", "Red / Internet", "Cuenta / Acceso", "Otro"
    )

    val TIPOS_DOCUMENTO = listOf("Evidencia", "Informe", "Rúbrica", "Otro")
}
