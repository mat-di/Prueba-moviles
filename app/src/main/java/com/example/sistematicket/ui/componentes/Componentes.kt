package com.example.sistematicket.ui.componentes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sistematicket.data.Catalogo
import com.example.sistematicket.data.Documento
import com.example.sistematicket.util.Formato

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    alPresionarListo: (() -> Unit)? = null,
    esContrasena: Boolean = false,
    lineasMinimas: Int = 1,
    maxCaracteres: Int? = null,
    habilitado: Boolean = true
) {
    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }
    val teclado = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (maxCaracteres == null || nuevo.length <= maxCaracteres) onCambio(nuevo)
        },
        label = { Text(etiqueta, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = if (esContrasena) {
            {
                IconButton(
                    onClick = { mostrarContrasena = !mostrarContrasena },
                    modifier = Modifier.focusProperties { canFocus = false }
                ) {
                    Icon(
                        if (mostrarContrasena) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        } else null,
        isError = error != null,
        supportingText = {
            Row {
                Text(error.orEmpty(), modifier = Modifier.weight(1f))
                if (maxCaracteres != null) Text("${valor.length}/$maxCaracteres")
            }
        },
        visualTransformation = if (esContrasena && !mostrarContrasena) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionIme),
        keyboardActions = if (alPresionarListo != null) {
            KeyboardActions(onDone = {
                teclado?.hide()
                alPresionarListo()
            })
        } else {
            KeyboardActions.Default
        },
        singleLine = lineasMinimas == 1,
        minLines = lineasMinimas,
        enabled = habilitado,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorDesplegable(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (indice: Int) -> Unit,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var abierto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = abierto,
        onExpandedChange = { abierto = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            isError = error != null,
            supportingText = { Text(error.orEmpty()) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEachIndexed { indice, opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        onSeleccion(indice)
                        abierto = false
                    }
                )
            }
        }
    }
}

fun colorEstado(estado: String): Color = when (estado) {
    Catalogo.ABIERTO -> Color(0xFF1E88E5)
    Catalogo.EN_PROCESO -> Color(0xFFF59E0B)
    else -> Color(0xFF43A047)
}

fun colorPrioridad(prioridad: String): Color = when (prioridad) {
    "Alta" -> Color(0xFFE53935)
    "Media" -> Color(0xFFF59E0B)
    else -> Color(0xFF43A047)
}

@Composable
fun ChipEstado(estado: String, modifier: Modifier = Modifier) {
    val color = colorEstado(estado)
    Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(estado, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EtiquetaPrioridad(prioridad: String) {
    val color = colorPrioridad(prioridad)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Flag, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(prioridad, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
fun IconoDocumento(documento: Documento, tamano: Dp = 48.dp) {
    val color = if (documento.esPdf) Color(0xFFD32F2F) else Color(0xFF1565C0)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(tamano)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
    ) {
        Icon(
            if (documento.esPdf) Icons.Filled.PictureAsPdf else Icons.Filled.Description,
            contentDescription = documento.extension,
            tint = color,
            modifier = Modifier.size(tamano * 0.55f)
        )
    }
}

@Composable
fun TarjetaDocumento(
    documento: Documento,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accion: @Composable () -> Unit = {}
) {
    val contenido: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            IconoDocumento(documento)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    documento.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${documento.categoria} · ${Formato.tamano(documento.tamano)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    Formato.fecha(documento.fechaCarga),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            accion()
        }
    }
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth()) { contenido() }
    } else {
        Card(modifier = modifier.fillMaxWidth()) { contenido() }
    }
}

@Composable
fun Avatar(nombre: String, tamano: Dp = 48.dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            Formato.iniciales(nombre),
            color = MaterialTheme.colorScheme.onPrimary,
            style = if (tamano > 56.dp) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (accion != null) {
            Spacer(Modifier.height(20.dp))
            accion()
        }
    }
}

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TarjetaInfo(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) { contenido() }
    }
}
