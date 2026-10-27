package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sistematicket.ui.componentes.CampoTexto
import com.example.sistematicket.viewmodel.AuthViewModel

@Composable
fun PantallaRegistro(
    viewModel: AuthViewModel,
    onRegistrado: () -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.registro.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.Filled.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                CampoTexto(
                    valor = estado.nombre,
                    onCambio = viewModel::onNombre,
                    etiqueta = "Nombre completo",
                    icono = Icons.Filled.Person,
                    error = estado.errorNombre,
                    maxCaracteres = 50
                )
                CampoTexto(
                    valor = estado.correo,
                    onCambio = viewModel::onCorreoRegistro,
                    etiqueta = "Correo institucional",
                    icono = Icons.Filled.Email,
                    error = estado.errorCorreo,
                    tipoTeclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = estado.contrasena,
                    onCambio = viewModel::onContrasenaRegistro,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    error = estado.errorContrasena,
                    tipoTeclado = KeyboardType.Password,
                    esContrasena = true
                )

                Requisito("Al menos 8 caracteres", estado.contrasena.length >= 8)
                Requisito("Una letra mayúscula", estado.contrasena.any { it.isUpperCase() })
                Requisito("Un número", estado.contrasena.any { it.isDigit() })
                Spacer(Modifier.height(12.dp))

                CampoTexto(
                    valor = estado.confirmacion,
                    onCambio = viewModel::onConfirmacion,
                    etiqueta = "Confirmar contraseña",
                    icono = Icons.Filled.LockReset,
                    error = estado.errorConfirmacion,
                    tipoTeclado = KeyboardType.Password,
                    accionIme = ImeAction.Done,
                    alPresionarListo = { viewModel.registrar(onRegistrado) },
                    esContrasena = true
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.registrar(onRegistrado) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Registrarme")
                }
            }
        }

        TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Ya tengo cuenta, iniciar sesión")
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Requisito(texto: String, cumplido: Boolean) {
    val color = if (cumplido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            if (cumplido) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = color)
    }
}
