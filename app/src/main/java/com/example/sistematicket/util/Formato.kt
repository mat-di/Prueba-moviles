package com.example.sistematicket.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formato {

    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    fun fecha(millis: Long): String = formatoFecha.format(Instant.ofEpochMilli(millis))

    fun tamano(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
        else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    }

    fun iniciales(nombre: String): String =
        nombre.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }
}
