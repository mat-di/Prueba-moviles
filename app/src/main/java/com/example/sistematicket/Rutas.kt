package com.example.sistematicket

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val TICKETS = "tickets"
    const val NUEVO_TICKET = "nuevo_ticket"
    const val DETALLE_TICKET = "detalle_ticket/{ticketId}"
    const val CARGAR = "cargar"
    const val VISUALIZAR = "visualizar"
    const val VISOR = "visor/{documentoId}"
    const val ELIMINAR = "eliminar"
    const val CERRAR_SESION = "cerrar_sesion"

    fun detalleTicket(id: Long) = "detalle_ticket/$id"
    fun visor(id: Long) = "visor/$id"

    val SIN_BARRAS = setOf(SPLASH, LOGIN, REGISTRO)

    val PRINCIPALES = setOf(TICKETS, CARGAR, VISUALIZAR, ELIMINAR)

    fun titulo(ruta: String?): String = when (ruta) {
        TICKETS -> "Mis tickets"
        NUEVO_TICKET -> "Nuevo ticket"
        DETALLE_TICKET -> "Detalle del ticket"
        CARGAR -> "Cargar documento"
        VISUALIZAR -> "Mis documentos"
        VISOR -> "Visor de documento"
        ELIMINAR -> "Eliminar documentos"
        CERRAR_SESION -> "Cerrar sesión"
        else -> "Sistema de Tickets"
    }
}
