package com.example.sistematicket.ui.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.data.TiposArchivo
import com.example.sistematicket.ui.componentes.ChipEstado
import com.example.sistematicket.ui.componentes.EtiquetaPrioridad
import com.example.sistematicket.ui.componentes.TarjetaDocumento
import com.example.sistematicket.ui.componentes.TarjetaInfo
import com.example.sistematicket.ui.componentes.TituloSeccion
import com.example.sistematicket.ui.componentes.colorEstado
import com.example.sistematicket.util.Formato
import com.example.sistematicket.viewmodel.DocumentoViewModel
import com.example.sistematicket.viewmodel.TicketViewModel

@Composable
fun PantallaDetalleTicket(
    ticketId: Long,
    ticketViewModel: TicketViewModel,
    documentoViewModel: DocumentoViewModel,
    onAbrirDocumento: (Long) -> Unit,
    onEliminado: () -> Unit,
    onMensaje: (String) -> Unit
) {
    val ticket by ticketViewModel.detalle.collectAsStateWithLifecycle()
    val adjuntos by documentoViewModel.adjuntos.collectAsStateWithLifecycle()
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(ticketId) {
        ticketViewModel.cargarDetalle(ticketId)
        documentoViewModel.cargarAdjuntos(ticketId)
    }

    val selectorArchivo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) documentoViewModel.adjuntarATicket(uri, ticketId, onMensaje)
    }

    val actual = ticket
    if (actual == null || actual.id != ticketId) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TarjetaInfo {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Ticket #${actual.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(actual.estado)
            }
            Spacer(Modifier.height(8.dp))
            Text(actual.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EtiquetaPrioridad(actual.prioridad)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(" ${actual.categoria}", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(" ${Formato.fecha(actual.fechaCreacion)}", style = MaterialTheme.typography.labelMedium)
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text("Descripción", style = MaterialTheme.typography.titleSmall)
            Text(actual.descripcion, style = MaterialTheme.typography.bodyMedium)
        }

        TarjetaInfo {
            TituloSeccion("Cambiar estado")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Catalogo.ESTADOS.forEach { estado ->
                    FilterChip(
                        selected = actual.estado == estado,
                        onClick = {
                            if (actual.estado != estado) {
                                ticketViewModel.cambiarEstado(estado)
                                onMensaje("Estado actualizado a \"$estado\"")
                            }
                        },
                        label = { Text(estado) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorEstado(estado).copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }

        TarjetaInfo {
            TituloSeccion("Documentos adjuntos (${adjuntos.size})")
            if (adjuntos.isEmpty()) {
                Text(
                    "Este ticket no tiene documentos. Adjunta un PDF o Word, o genera un reporte PDF.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            adjuntos.forEach { documento ->
                TarjetaDocumento(
                    documento = documento,
                    onClick = { onAbrirDocumento(documento.id) },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver")
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                    onClick = { selectorArchivo.launch(TiposArchivo.PERMITIDOS) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Adjuntar", maxLines = 1)
                }
                FilledTonalButton(
                    onClick = { documentoViewModel.exportarTicketPdf(actual, onMensaje) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Exportar", maxLines = 1)
                }
            }
        }

        OutlinedButton(
            onClick = { confirmarEliminar = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.DeleteForever, contentDescription = null)
            Text("  Eliminar ticket")
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null) },
            title = { Text("¿Eliminar ticket?") },
            text = { Text("Se eliminará \"${actual.titulo}\". Sus documentos se conservarán en \"Mis documentos\".") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    ticketViewModel.eliminarTicket(onEliminado)
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}
