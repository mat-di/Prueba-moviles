package com.example.sistematicket.data

object Almacen {
    val usuarios = mutableListOf<Usuario>()
    val tickets = mutableListOf<Ticket>()
    val documentos = mutableListOf<Documento>()
    var usuarioActual: Usuario? = null

    private var ultimoIdUsuario = 0L
    private var ultimoIdTicket = 0L
    private var ultimoIdDocumento = 0L

    fun nuevoIdUsuario() = ++ultimoIdUsuario
    fun nuevoIdTicket() = ++ultimoIdTicket
    fun nuevoIdDocumento() = ++ultimoIdDocumento
}
