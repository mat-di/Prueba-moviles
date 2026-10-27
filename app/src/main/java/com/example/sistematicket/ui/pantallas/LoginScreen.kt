package com.example.sistematicket.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit,
    onRegistroClick: () -> Unit
) {
    val estado by viewModel.login.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Inicia sesión con tu correo institucional",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                CampoTexto(
                    valor = estado.correo,
                    onCambio = viewModel::onCorreoLogin,
                    etiqueta = "Correo",
                    icono = Icons.Filled.Email,
                    error = estado.errorCorreo,
                    tipoTeclado = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(4.dp))

                CampoTexto(
                    valor = estado.contrasena,
                    onCambio = viewModel::onContrasenaLogin,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    error = estado.errorContrasena,
                    tipoTeclado = KeyboardType.Password,
                    accionIme = ImeAction.Done,
                    alPresionarListo = { viewModel.iniciarSesion(onLoginExitoso) },
                    esContrasena = true
                )

                if (estado.errorGeneral != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                estado.errorGeneral.orEmpty(),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.iniciarSesion(onLoginExitoso) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Iniciar sesión")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = onRegistroClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Crear cuenta")
        }
    }
}
