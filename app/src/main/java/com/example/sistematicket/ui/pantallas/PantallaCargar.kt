package com.example.sistematicket.ui.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.archivos.ArchivoManager
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.data.TiposArchivo
import com.example.sistematicket.ui.componentes.CampoTexto
import com.example.sistematicket.ui.componentes.SelectorDesplegable
import com.example.sistematicket.ui.componentes.TarjetaInfo
import com.example.sistematicket.ui.componentes.TituloSeccion
import com.example.sistematicket.util.Formato
import com.example.sistematicket.util.Validaciones
import com.example.sistematicket.viewmodel.DocumentoViewModel
import com.example.sistematicket.viewmodel.TicketViewModel

@Composable
fun PantallaCargar(
    documentoViewModel: DocumentoViewModel,
    ticketViewModel: TicketViewModel,
    onSubido: () -> Unit
) {
    val estado by documentoViewModel.carga.collectAsStateWithLifecycle()
    val tickets by ticketViewModel.tickets.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { ticketViewModel.cargarTickets() }

    val selectorArchivo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) documentoViewModel.seleccionarArchivo(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            onClick = { selectorArchivo.launch(TiposArchivo.PERMITIDOS) },
            border = BorderStroke(
                2.dp,
                if (estado.errorArchivo != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val archivo = estado.archivo
            if (archivo == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp)
                ) {
                    Icon(
                        Icons.Filled.CloudUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Toca para elegir un archivo", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "PDF, DOC o DOCX · máximo 10 MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                VistaArchivo(archivo, onQuitar = documentoViewModel::quitarArchivo)
            }
        }

        if (estado.errorArchivo != null) {
            Text(
                estado.errorArchivo.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TarjetaInfo {
            TituloSeccion("Información del documento")
            CampoTexto(
                valor = estado.nombre,
                onCambio = documentoViewModel::onNombre,
                etiqueta = "Nombre del documento",
                icono = Icons.Filled.Edit,
                error = estado.errorNombre,
                maxCaracteres = Validaciones.MAX_NOMBRE_DOCUMENTO
            )
            SelectorDesplegable(
                etiqueta = "Tipo de documento",
                valor = estado.categoria,
                opciones = Catalogo.TIPOS_DOCUMENTO,
                onSeleccion = { documentoViewModel.onCategoria(Catalogo.TIPOS_DOCUMENTO[it]) },
                icono = Icons.Filled.Category
            )
            val opcionesTicket = listOf("Sin ticket") + tickets.map { "#${it.id} · ${it.titulo}" }
            val ticketElegido = tickets.firstOrNull { it.id == estado.ticketId }
            SelectorDesplegable(
                etiqueta = "Asociar a un ticket (opcional)",
                valor = ticketElegido?.let { "#${it.id} · ${it.titulo}" } ?: "Sin ticket",
                opciones = opcionesTicket,
                onSeleccion = { indice ->
                    documentoViewModel.onTicket(if (indice == 0) null else tickets[indice - 1].id)
                },
                icono = Icons.Filled.ConfirmationNumber
            )
        }

        Button(
            onClick = { documentoViewModel.subirDocumento(onSubido) },
            enabled = !estado.subiendo,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (estado.subiendo) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                Icon(Icons.Filled.UploadFile, contentDescription = null)
                Text("  Cargar documento")
            }
        }
    }
}

@Composable
private fun VistaArchivo(archivo: ArchivoManager.InfoArchivo, onQuitar: () -> Unit) {
    val esPdf = archivo.tipoMime == TiposArchivo.PDF
    val color = if (esPdf) Color(0xFFD32F2F) else Color(0xFF1565C0)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Icon(
            if (esPdf) Icons.Filled.PictureAsPdf else Icons.Filled.Description,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                archivo.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${if (esPdf) "PDF" else "Word"} · ${Formato.tamano(archivo.tamano)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text("Toca para cambiar el archivo", style = MaterialTheme.typography.labelSmall, color = color)
        }
        IconButton(onClick = onQuitar) {
            Icon(Icons.Filled.Close, contentDescription = "Quitar archivo")
        }
    }
}
