package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.data.Usuario
import com.example.sistematicket.ui.componentes.Avatar
import com.example.sistematicket.ui.componentes.TarjetaInfo
import com.example.sistematicket.viewmodel.DocumentoViewModel
import com.example.sistematicket.viewmodel.TicketViewModel

@Composable
fun PantallaCerrarSesion(
    usuario: Usuario?,
    ticketViewModel: TicketViewModel,
    documentoViewModel: DocumentoViewModel,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val tickets by ticketViewModel.tickets.collectAsStateWithLifecycle()
    val documentos by documentoViewModel.documentos.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ticketViewModel.cargarTickets()
        documentoViewModel.cargarDocumentos()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(Modifier.height(24.dp))

        TarjetaInfo {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(usuario?.nombre.orEmpty(), tamano = 56.dp)
                Column(Modifier.padding(start = 12.dp)) {
                    Text(usuario?.nombre.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Text(
                        usuario?.correo.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Estadistica("${tickets.size}", "Tickets")
                Estadistica("${documentos.size}", "Documentos")
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("¿Deseas cerrar tu sesión?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tus tickets y documentos seguirán disponibles mientras la app esté abierta.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        Column(Modifier.fillMaxWidth()) {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Text("  Sí, cerrar sesión")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(etiqueta, style = MaterialTheme.typography.labelMedium)
    }
}
