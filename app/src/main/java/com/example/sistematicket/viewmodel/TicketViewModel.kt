package com.example.sistematicket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistematicket.data.Almacen
import com.example.sistematicket.data.Ticket
import com.example.sistematicket.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class NuevoTicketUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: String = "Media",
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null
)

class TicketViewModel : ViewModel() {

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    private val _filtroEstado = MutableStateFlow<String?>(null)
    val filtroEstado: StateFlow<String?> = _filtroEstado.asStateFlow()

    val ticketsFiltrados: StateFlow<List<Ticket>> =
        combine(_tickets, _filtroEstado) { lista, filtro ->
            if (filtro == null) lista else lista.filter { it.estado == filtro }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _nuevo = MutableStateFlow(NuevoTicketUiState())
    val nuevo: StateFlow<NuevoTicketUiState> = _nuevo.asStateFlow()

    private val _detalle = MutableStateFlow<Ticket?>(null)
    val detalle: StateFlow<Ticket?> = _detalle.asStateFlow()

    fun cargarTickets() {
        val usuario = Almacen.usuarioActual ?: return
        _tickets.value = Almacen.tickets
            .filter { it.usuarioId == usuario.id }
            .sortedByDescending { it.fechaCreacion }
    }

    fun filtrar(estado: String?) {
        _filtroEstado.value = estado
    }

    fun onTitulo(valor: String) = _nuevo.update { it.copy(titulo = valor, errorTitulo = null) }

    fun onDescripcion(valor: String) =
        _nuevo.update { it.copy(descripcion = valor, errorDescripcion = null) }

    fun onCategoria(valor: String) =
        _nuevo.update { it.copy(categoria = valor, errorCategoria = null) }

    fun onPrioridad(valor: String) = _nuevo.update { it.copy(prioridad = valor) }

    fun crearTicket(onExito: () -> Unit) {
        val estado = _nuevo.value
        val conErrores = estado.copy(
            errorTitulo = Validaciones.titulo(estado.titulo),
            errorDescripcion = Validaciones.descripcion(estado.descripcion),
            errorCategoria = Validaciones.seleccion(estado.categoria, "una categoría")
        )
        if (conErrores.errorTitulo != null || conErrores.errorDescripcion != null ||
            conErrores.errorCategoria != null
        ) {
            _nuevo.value = conErrores
            return
        }

        val usuario = Almacen.usuarioActual ?: return
        Almacen.tickets.add(
            Ticket(
                id = Almacen.nuevoIdTicket(),
                usuarioId = usuario.id,
                titulo = estado.titulo.trim(),
                descripcion = estado.descripcion.trim(),
                categoria = estado.categoria,
                prioridad = estado.prioridad
            )
        )
        _nuevo.value = NuevoTicketUiState()
        cargarTickets()
        onExito()
    }

    fun cargarDetalle(id: Long) {
        _detalle.value = Almacen.tickets.find { it.id == id }
    }

    fun cambiarEstado(estado: String) {
        val ticket = _detalle.value ?: return
        val actualizado = ticket.copy(estado = estado)
        val posicion = Almacen.tickets.indexOfFirst { it.id == ticket.id }
        if (posicion >= 0) Almacen.tickets[posicion] = actualizado
        _detalle.value = actualizado
        cargarTickets()
    }

    fun eliminarTicket(onExito: () -> Unit) {
        val ticket = _detalle.value ?: return
        Almacen.tickets.removeAll { it.id == ticket.id }
        Almacen.documentos.replaceAll { if (it.ticketId == ticket.id) it.copy(ticketId = null) else it }
        _tickets.update { lista -> lista.filterNot { it.id == ticket.id } }
        onExito()
    }

    fun limpiar() {
        _tickets.value = emptyList()
        _filtroEstado.value = null
        _nuevo.value = NuevoTicketUiState()
        _detalle.value = null
    }
}
