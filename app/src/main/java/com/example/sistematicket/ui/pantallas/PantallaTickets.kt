package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.data.Ticket
import com.example.sistematicket.ui.componentes.ChipEstado
import com.example.sistematicket.ui.componentes.EstadoVacio
import com.example.sistematicket.ui.componentes.EtiquetaPrioridad
import com.example.sistematicket.ui.componentes.colorEstado
import com.example.sistematicket.util.Formato
import com.example.sistematicket.viewmodel.TicketViewModel

@Composable
fun PantallaTickets(
    viewModel: TicketViewModel,
    nombreUsuario: String,
    onNuevoTicket: () -> Unit,
    onAbrirTicket: (Long) -> Unit
) {
    val todos by viewModel.tickets.collectAsStateWithLifecycle()
    val tickets by viewModel.ticketsFiltrados.collectAsStateWithLifecycle()
    val filtro by viewModel.filtroEstado.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargarTickets() }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    "Hola, ${nombreUsuario.substringBefore(' ')}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Revisa el estado de tus solicitudes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Catalogo.ESTADOS.forEach { estado ->
                        TarjetaResumen(
                            etiqueta = estado,
                            cantidad = todos.count { it.estado == estado },
                            color = colorEstado(estado),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filtro == null,
                            onClick = { viewModel.filtrar(null) },
                            label = { Text("Todos (${todos.size})") }
                        )
                    }
                    items(Catalogo.ESTADOS) { estado ->
                        FilterChip(
                            selected = filtro == estado,
                            onClick = { viewModel.filtrar(estado) },
                            label = { Text(estado) }
                        )
                    }
                }
            }

            if (tickets.isEmpty()) {
                item {
                    EstadoVacio(
                        icono = Icons.Filled.Inbox,
                        titulo = if (filtro == null) "Aún no tienes tickets" else "Sin tickets \"$filtro\"",
                        mensaje = "Crea un ticket para reportar un problema o solicitud.",
                        modifier = Modifier.height(320.dp)
                    )
                }
            }

            items(tickets, key = { it.id }) { ticket ->
                TarjetaTicket(
                    ticket = ticket,
                    onClick = { onAbrirTicket(ticket.id) }
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = onNuevoTicket,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Nuevo ticket") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun TarjetaResumen(etiqueta: String, cantidad: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("$cantidad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
private fun TarjetaTicket(ticket: Ticket, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "#${ticket.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(ticket.estado)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                ticket.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                ticket.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EtiquetaPrioridad(ticket.prioridad)
                Spacer(Modifier.width(12.dp))
                Icon(
                    Icons.Filled.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    ticket.categoria,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                Formato.fecha(ticket.fechaCreacion),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}
