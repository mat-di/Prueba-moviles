package com.example.sistematicket.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.sistematicket.archivos.LectorWord
import com.example.sistematicket.archivos.RenderizadorPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun VisorPdf(archivo: File, modifier: Modifier = Modifier) {
    val renderizador = remember(archivo) { runCatching { RenderizadorPdf(archivo) }.getOrNull() }
    DisposableEffect(renderizador) {
        onDispose { renderizador?.close() }
    }

    if (renderizador == null) {
        EstadoVacio(
            icono = Icons.Filled.ErrorOutline,
            titulo = "No se pudo abrir el PDF",
            mensaje = "El archivo puede estar dañado o protegido con contraseña.",
            modifier = modifier
        )
        return
    }

    val estadoLista = rememberLazyListState()
    val paginaActual by remember { derivedStateOf { estadoLista.firstVisibleItemIndex + 1 } }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val anchoPx = constraints.maxWidth.coerceIn(1, 1440)

        LazyColumn(
            state = estadoLista,
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(renderizador.totalPaginas) { indice ->
                PaginaPdf(renderizador, indice, anchoPx)
            }
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                "Página $paginaActual de ${renderizador.totalPaginas}",
                color = MaterialTheme.colorScheme.inverseOnSurface,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun PaginaPdf(renderizador: RenderizadorPdf, indice: Int, anchoPx: Int) {
    val pagina by produceState<ImageBitmap?>(null, renderizador, indice, anchoPx) {
        value = renderizador.renderizar(indice, anchoPx)?.asImageBitmap()
    }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        val imagen = pagina
        if (imagen == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.707f)
            ) { CircularProgressIndicator() }
        } else {
            Image(
                bitmap = imagen,
                contentDescription = "Página ${indice + 1}",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imagen.width.toFloat() / imagen.height)
            )
        }
    }
}

@Composable
fun VisorWord(archivo: File, modifier: Modifier = Modifier) {
    val parrafos by produceState<List<String>?>(null, archivo) {
        value = withContext(Dispatchers.IO) {
            runCatching { LectorWord.extraerParrafos(archivo) }.getOrDefault(emptyList())
        }
    }
    val lista = parrafos

    when {
        lista == null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        lista.all { it.isBlank() } -> EstadoVacio(
            icono = Icons.Filled.ErrorOutline,
            titulo = "Sin texto para mostrar",
            mensaje = "El documento está vacío o solo contiene imágenes. Ábrelo con otra app.",
            modifier = modifier
        )
        else -> Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF212121)),
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            LazyColumn(contentPadding = PaddingValues(24.dp)) {
                items(lista) { parrafo ->
                    if (parrafo.isBlank()) {
                        Spacer(Modifier.height(8.dp))
                    } else {
                        Text(
                            parrafo,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
