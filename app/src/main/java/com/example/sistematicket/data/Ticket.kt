package com.example.sistematicket.data

data class Ticket(
    val id: Long,
    val usuarioId: Long,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val prioridad: String,
    val estado: String = Catalogo.ABIERTO,
    val fechaCreacion: Long = System.currentTimeMillis()
)
