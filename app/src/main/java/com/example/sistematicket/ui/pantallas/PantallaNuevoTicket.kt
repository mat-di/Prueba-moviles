package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.ui.componentes.CampoTexto
import com.example.sistematicket.ui.componentes.SelectorDesplegable
import com.example.sistematicket.ui.componentes.TarjetaInfo
import com.example.sistematicket.ui.componentes.TituloSeccion
import com.example.sistematicket.ui.componentes.colorPrioridad
import com.example.sistematicket.util.Validaciones
import com.example.sistematicket.viewmodel.TicketViewModel

@Composable
fun PantallaNuevoTicket(viewModel: TicketViewModel, onCreado: () -> Unit) {
    val estado by viewModel.nuevo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TarjetaInfo {
            TituloSeccion("Datos del ticket")
            CampoTexto(
                valor = estado.titulo,
                onCambio = viewModel::onTitulo,
                etiqueta = "Título",
                icono = Icons.Filled.Title,
                error = estado.errorTitulo,
                maxCaracteres = Validaciones.MAX_TITULO
            )
            CampoTexto(
                valor = estado.descripcion,
                onCambio = viewModel::onDescripcion,
                etiqueta = "Descripción del problema",
                icono = Icons.Filled.Description,
                error = estado.errorDescripcion,
                lineasMinimas = 4,
                maxCaracteres = Validaciones.MAX_DESCRIPCION
            )
            SelectorDesplegable(
                etiqueta = "Categoría",
                valor = estado.categoria,
                opciones = Catalogo.CATEGORIAS,
                onSeleccion = { viewModel.onCategoria(Catalogo.CATEGORIAS[it]) },
                icono = Icons.Filled.Category,
                error = estado.errorCategoria
            )

            TituloSeccion("Prioridad")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Catalogo.PRIORIDADES.forEach { prioridad ->
                    val color = colorPrioridad(prioridad)
                    FilterChip(
                        selected = estado.prioridad == prioridad,
                        onClick = { viewModel.onPrioridad(prioridad) },
                        label = { Text(prioridad) },
                        leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = null, tint = color) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.crearTicket(onCreado) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            Text("  Crear ticket")
        }
        Text(
            "El ticket se crea con estado \"${Catalogo.ABIERTO}\".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
