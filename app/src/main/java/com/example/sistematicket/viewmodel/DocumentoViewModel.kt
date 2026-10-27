package com.example.sistematicket.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistematicket.archivos.ArchivoManager
import com.example.sistematicket.archivos.GeneradorPdf
import com.example.sistematicket.data.Almacen
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.data.Documento
import com.example.sistematicket.data.Ticket
import com.example.sistematicket.data.TiposArchivo
import com.example.sistematicket.util.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CargaUiState(
    val uri: Uri? = null,
    val archivo: ArchivoManager.InfoArchivo? = null,
    val nombre: String = "",
    val categoria: String = Catalogo.TIPOS_DOCUMENTO.first(),
    val ticketId: Long? = null,
    val errorArchivo: String? = null,
    val errorNombre: String? = null,
    val subiendo: Boolean = false
)

enum class FiltroTipo(val etiqueta: String) { TODOS("Todos"), PDF("PDF"), WORD("Word") }

class DocumentoViewModel(application: Application) : AndroidViewModel(application) {

    private val archivos = ArchivoManager(application)

    private val _documentos = MutableStateFlow<List<Documento>>(emptyList())
    val documentos: StateFlow<List<Documento>> = _documentos.asStateFlow()

    private val _filtro = MutableStateFlow(FiltroTipo.TODOS)
    val filtro: StateFlow<FiltroTipo> = _filtro.asStateFlow()

    val documentosFiltrados: StateFlow<List<Documento>> =
        combine(_documentos, _filtro) { lista, filtro ->
            when (filtro) {
                FiltroTipo.TODOS -> lista
                FiltroTipo.PDF -> lista.filter { it.esPdf }
                FiltroTipo.WORD -> lista.filter { it.esWord }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _adjuntos = MutableStateFlow<List<Documento>>(emptyList())
    val adjuntos: StateFlow<List<Documento>> = _adjuntos.asStateFlow()

    private val _carga = MutableStateFlow(CargaUiState())
    val carga: StateFlow<CargaUiState> = _carga.asStateFlow()

    fun cargarDocumentos() {
        val usuario = Almacen.usuarioActual ?: return
        _documentos.value = Almacen.documentos
            .filter { it.usuarioId == usuario.id }
            .sortedByDescending { it.fechaCarga }
    }

    fun cargarAdjuntos(ticketId: Long) {
        _adjuntos.value = Almacen.documentos
            .filter { it.ticketId == ticketId }
            .sortedByDescending { it.fechaCarga }
    }

    fun filtrar(filtro: FiltroTipo) {
        _filtro.value = filtro
    }

    fun obtenerDocumento(id: Long): Documento? = Almacen.documentos.find { it.id == id }

    fun seleccionarArchivo(uri: Uri) {
        viewModelScope.launch {
            val info = withContext(Dispatchers.IO) { archivos.obtenerInfo(uri) }
            val error = Validaciones.archivo(info.tipoMime, info.tamano)
            _carga.update {
                if (error != null) {
                    it.copy(uri = null, archivo = null, errorArchivo = error)
                } else {
                    it.copy(
                        uri = uri,
                        archivo = info,
                        nombre = info.nombre.substringBeforeLast('.').take(Validaciones.MAX_NOMBRE_DOCUMENTO),
                        errorArchivo = null,
                        errorNombre = null
                    )
                }
            }
        }
    }

    fun quitarArchivo() = _carga.update { it.copy(uri = null, archivo = null) }

    fun onNombre(valor: String) = _carga.update { it.copy(nombre = valor, errorNombre = null) }

    fun onCategoria(valor: String) = _carga.update { it.copy(categoria = valor) }

    fun onTicket(ticketId: Long?) = _carga.update { it.copy(ticketId = ticketId) }

    fun subirDocumento(onExito: () -> Unit) {
        val estado = _carga.value
        val uri = estado.uri
        val archivo = estado.archivo
        val errorArchivo = if (uri == null || archivo == null) Validaciones.archivo(null, 0) else null
        val errorNombre = Validaciones.nombreDocumento(estado.nombre)
        if (uri == null || archivo == null || errorNombre != null) {
            _carga.update { it.copy(errorArchivo = errorArchivo, errorNombre = errorNombre) }
            return
        }

        val usuario = Almacen.usuarioActual ?: return
        _carga.update { it.copy(subiendo = true) }
        viewModelScope.launch {
            val resultado = runCatching {
                val extension = archivo.nombre.substringAfterLast('.', "")
                val nombreFinal = estado.nombre.trim() +
                    if (extension.isNotEmpty()) ".$extension" else ""
                val copia = withContext(Dispatchers.IO) {
                    archivos.copiarAlAlmacenamientoInterno(uri, nombreFinal)
                }
                Almacen.documentos.add(
                    Documento(
                        id = Almacen.nuevoIdDocumento(),
                        usuarioId = usuario.id,
                        ticketId = estado.ticketId,
                        nombre = nombreFinal,
                        categoria = estado.categoria,
                        tipoMime = archivo.tipoMime,
                        ruta = copia.absolutePath,
                        tamano = copia.length()
                    )
                )
            }
            if (resultado.isSuccess) {
                _carga.value = CargaUiState()
                cargarDocumentos()
                onExito()
            } else {
                _carga.update {
                    it.copy(subiendo = false, errorArchivo = "No se pudo guardar el archivo")
                }
            }
        }
    }

    fun adjuntarATicket(uri: Uri, ticketId: Long, onResultado: (String) -> Unit) {
        val usuario = Almacen.usuarioActual ?: return
        viewModelScope.launch {
            val mensaje = runCatching {
                val info = withContext(Dispatchers.IO) { archivos.obtenerInfo(uri) }
                Validaciones.archivo(info.tipoMime, info.tamano) ?: run {
                    val copia = withContext(Dispatchers.IO) {
                        archivos.copiarAlAlmacenamientoInterno(uri, info.nombre)
                    }
                    Almacen.documentos.add(
                        Documento(
                            id = Almacen.nuevoIdDocumento(),
                            usuarioId = usuario.id,
                            ticketId = ticketId,
                            nombre = info.nombre,
                            categoria = "Evidencia",
                            tipoMime = info.tipoMime,
                            ruta = copia.absolutePath,
                            tamano = copia.length()
                        )
                    )
                    "Documento adjuntado"
                }
            }.getOrDefault("No se pudo adjuntar el archivo")
            cargarAdjuntos(ticketId)
            onResultado(mensaje)
        }
    }

    fun exportarTicketPdf(ticket: Ticket, onResultado: (String) -> Unit) {
        val usuario = Almacen.usuarioActual ?: return
        viewModelScope.launch {
            val mensaje = runCatching {
                val nombre = "Ticket_${ticket.id}.pdf"
                val destino = archivos.nuevoArchivo(nombre)
                withContext(Dispatchers.IO) { GeneradorPdf.generarTicket(ticket, usuario, destino) }
                Almacen.documentos.add(
                    Documento(
                        id = Almacen.nuevoIdDocumento(),
                        usuarioId = usuario.id,
                        ticketId = ticket.id,
                        nombre = nombre,
                        categoria = "Informe",
                        tipoMime = TiposArchivo.PDF,
                        ruta = destino.absolutePath,
                        tamano = destino.length()
                    )
                )
                "PDF generado y adjuntado al ticket"
            }.getOrDefault("No se pudo generar el PDF")
            cargarAdjuntos(ticket.id)
            onResultado(mensaje)
        }
    }

    fun eliminarDocumento(documento: Documento, onExito: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { archivos.eliminar(documento.ruta) }
            Almacen.documentos.removeAll { it.id == documento.id }
            _documentos.update { lista -> lista.filterNot { it.id == documento.id } }
            _adjuntos.update { lista -> lista.filterNot { it.id == documento.id } }
            onExito()
        }
    }

    fun limpiar() {
        _documentos.value = emptyList()
        _adjuntos.value = emptyList()
        _filtro.value = FiltroTipo.TODOS
        _carga.value = CargaUiState()
    }
}
