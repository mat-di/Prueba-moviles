package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.data.Documento
import com.example.sistematicket.ui.componentes.EstadoVacio
import com.example.sistematicket.ui.componentes.TarjetaDocumento
import com.example.sistematicket.viewmodel.DocumentoViewModel

@Composable
fun PantallaEliminar(viewModel: DocumentoViewModel, onMensaje: (String) -> Unit) {
    val documentos by viewModel.documentos.collectAsStateWithLifecycle()
    var porEliminar by remember { mutableStateOf<Documento?>(null) }

    LaunchedEffect(Unit) { viewModel.cargarDocumentos() }

    Column(Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Los documentos eliminados se borran del dispositivo y no se pueden recuperar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        if (documentos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.DeleteSweep,
                titulo = "Nada que eliminar",
                mensaje = "No tienes documentos cargados."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documentos, key = { it.id }) { documento ->
                    TarjetaDocumento(documento = documento) {
                        IconButton(onClick = { porEliminar = documento }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar ${documento.nombre}",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { documento ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null) },
            title = { Text("¿Eliminar documento?") },
            text = { Text("Se eliminará \"${documento.nombre}\" de forma permanente.") },
            confirmButton = {
                TextButton(onClick = {
                    porEliminar = null
                    viewModel.eliminarDocumento(documento) { onMensaje("Documento eliminado") }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
