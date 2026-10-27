package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.ui.componentes.EstadoVacio
import com.example.sistematicket.ui.componentes.TarjetaDocumento
import com.example.sistematicket.viewmodel.DocumentoViewModel
import com.example.sistematicket.viewmodel.FiltroTipo

@Composable
fun PantallaVisualizar(
    viewModel: DocumentoViewModel,
    onAbrirDocumento: (Long) -> Unit,
    onCargar: () -> Unit
) {
    val documentos by viewModel.documentosFiltrados.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargarDocumentos() }

    Column(Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FiltroTipo.entries.forEach { opcion ->
                FilterChip(
                    selected = filtro == opcion,
                    onClick = { viewModel.filtrar(opcion) },
                    label = { Text(opcion.etiqueta) }
                )
            }
        }

        if (documentos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.FolderOpen,
                titulo = "No hay documentos",
                mensaje = "Carga un PDF o Word para verlo aquí.",
                accion = {
                    Button(onClick = onCargar) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null)
                        Text("  Cargar documento")
                    }
                }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documentos, key = { it.id }) { documento ->
                    TarjetaDocumento(
                        documento = documento,
                        onClick = { onAbrirDocumento(documento.id) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Abrir")
                    }
                }
            }
        }
    }
}
