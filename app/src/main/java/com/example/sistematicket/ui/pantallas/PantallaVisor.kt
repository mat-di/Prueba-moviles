package com.example.sistematicket.ui.pantallas

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sistematicket.archivos.ArchivoManager
import com.example.sistematicket.data.Documento
import com.example.sistematicket.data.TiposArchivo
import com.example.sistematicket.ui.componentes.EstadoVacio
import com.example.sistematicket.ui.componentes.IconoDocumento
import com.example.sistematicket.ui.componentes.VisorPdf
import com.example.sistematicket.ui.componentes.VisorWord
import com.example.sistematicket.util.Formato
import com.example.sistematicket.viewmodel.DocumentoViewModel
import java.io.File

@Composable
fun PantallaVisor(
    documentoId: Long,
    viewModel: DocumentoViewModel,
    onMensaje: (String) -> Unit
) {
    val context = LocalContext.current
    val archivos = remember { ArchivoManager(context) }
    var documento by remember { mutableStateOf<Documento?>(null) }
    var cargado by remember { mutableStateOf(false) }

    LaunchedEffect(documentoId) {
        documento = viewModel.obtenerDocumento(documentoId)
        cargado = true
    }

    val doc = documento
    if (!cargado) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (doc == null) {
        EstadoVacio(Icons.Filled.ErrorOutline, "Documento no encontrado", "Es posible que haya sido eliminado.")
        return
    }

    val archivo = remember(doc.ruta) { File(doc.ruta) }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoDocumento(doc, tamano = 52.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        doc.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${doc.categoria} · ${Formato.tamano(doc.tamano)} · ${Formato.fecha(doc.fechaCarga)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        if (!archivos.abrirConOtraApp(doc)) {
                            onMensaje("No hay una app instalada para abrir este tipo de archivo")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Abrir con…")
                }
                OutlinedButton(onClick = { archivos.compartir(doc) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Compartir")
                }
            }
        }

        when {
            !archivo.exists() -> EstadoVacio(
                Icons.Filled.ErrorOutline,
                "Archivo no encontrado",
                "El archivo ya no está en el almacenamiento del dispositivo."
            )
            doc.esPdf -> VisorPdf(archivo, Modifier.weight(1f))
            doc.tipoMime == TiposArchivo.DOCX -> VisorWord(archivo, Modifier.weight(1f))
            else -> EstadoVacio(
                icono = Icons.Filled.Description,
                titulo = "Vista previa no disponible",
                mensaje = "Los archivos .doc antiguos no se pueden mostrar aquí. Ábrelo con Word o Google Docs.",
                accion = {
                    Button(onClick = {
                        if (!archivos.abrirConOtraApp(doc)) {
                            onMensaje("No hay una app instalada para abrir archivos Word")
                        }
                    }) { Text("Abrir con otra app") }
                }
            )
        }
    }
}
