package com.example.sistematicket.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistematicket.Rutas
import com.example.sistematicket.data.Usuario

data class ItemMenu(val ruta: String, val etiqueta: String, val icono: ImageVector)

val itemsBarraInferior = listOf(
    ItemMenu(Rutas.TICKETS, "Tickets", Icons.Filled.ConfirmationNumber),
    ItemMenu(Rutas.CARGAR, "Cargar", Icons.Filled.UploadFile),
    ItemMenu(Rutas.VISUALIZAR, "Ver", Icons.Filled.Visibility),
    ItemMenu(Rutas.ELIMINAR, "Eliminar", Icons.Filled.Delete)
)

private val itemsMenuLateral = listOf(
    ItemMenu(Rutas.TICKETS, "Mis tickets", Icons.Filled.ConfirmationNumber),
    ItemMenu(Rutas.NUEVO_TICKET, "Nuevo ticket", Icons.Filled.AddCircle),
    ItemMenu(Rutas.CARGAR, "Cargar documento", Icons.Filled.UploadFile),
    ItemMenu(Rutas.VISUALIZAR, "Ver documentos", Icons.Filled.Visibility),
    ItemMenu(Rutas.ELIMINAR, "Eliminar documentos", Icons.Filled.Delete)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    mostrarMenu: Boolean,
    usuario: Usuario?,
    onAbrirMenu: () -> Unit,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var menuUsuarioAbierto by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (mostrarMenu) {
                IconButton(onClick = onAbrirMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                }
            } else {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = {
            IconButton(onClick = { menuUsuarioAbierto = true }) {
                Avatar(nombre = usuario?.nombre.orEmpty(), tamano = 34.dp)
            }
            DropdownMenu(
                expanded = menuUsuarioAbierto,
                onDismissRequest = { menuUsuarioAbierto = false }
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(usuario?.nombre.orEmpty(), style = MaterialTheme.typography.titleSmall)
                    Text(
                        usuario?.correo.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    onClick = {
                        menuUsuarioAbierto = false
                        onCerrarSesion()
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun BarraInferior(rutaActual: String?, onSeleccion: (String) -> Unit) {
    NavigationBar {
        itemsBarraInferior.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onSeleccion(item.ruta) },
                icon = { Icon(item.icono, contentDescription = null) },
                label = { Text(item.etiqueta) }
            )
        }
    }
}

@Composable
fun MenuLateral(
    usuario: Usuario?,
    rutaActual: String?,
    onSeleccion: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet {
        Column(Modifier.padding(24.dp)) {
            Avatar(nombre = usuario?.nombre.orEmpty(), tamano = 64.dp)
            Spacer(Modifier.height(12.dp))
            Text(usuario?.nombre.orEmpty(), style = MaterialTheme.typography.titleMedium)
            Text(
                usuario?.correo.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        itemsMenuLateral.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.etiqueta) },
                icon = { Icon(item.icono, contentDescription = null) },
                selected = rutaActual == item.ruta,
                onClick = { onSeleccion(item.ruta) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            selected = rutaActual == Rutas.CERRAR_SESION,
            onClick = onCerrarSesion,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        Spacer(Modifier.weight(1f))
        Text(
            "Sistema de Tickets · v1.0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp)
        )
    }
}
