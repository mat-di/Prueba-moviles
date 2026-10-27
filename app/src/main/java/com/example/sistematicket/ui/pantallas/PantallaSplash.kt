package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(haySesion: Boolean, onTerminar: (haySesion: Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTerminar(haySesion)
    }

    val colores = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colores.primary, colores.tertiary)))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.ConfirmationNumber,
                contentDescription = null,
                tint = colores.onPrimary,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Sistema de Tickets",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colores.onPrimary
            )
            Text(
                "Gestión de solicitudes y documentos",
                style = MaterialTheme.typography.bodyMedium,
                color = colores.onPrimary.copy(alpha = 0.85f)
            )
        }
    }
}
