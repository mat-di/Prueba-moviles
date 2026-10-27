package com.example.sistematicket.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sistematicket.data.Almacen
import com.example.sistematicket.data.Seguridad
import com.example.sistematicket.data.Usuario
import com.example.sistematicket.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null
)

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null
)

class AuthViewModel : ViewModel() {

    private val _usuario = MutableStateFlow(Almacen.usuarioActual)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    fun onCorreoLogin(valor: String) =
        _login.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    fun onContrasenaLogin(valor: String) =
        _login.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _login.value
        val errorCorreo = Validaciones.correo(estado.correo)
        val errorContrasena = Validaciones.contrasenaLogin(estado.contrasena)
        if (errorCorreo != null || errorContrasena != null) {
            _login.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena
                )
            }
            return
        }

        val correo = estado.correo.trim().lowercase()
        val usuario = Almacen.usuarios.find {
            it.correo == correo && Seguridad.verificarContrasena(estado.contrasena, it.contrasena)
        }
        if (usuario == null) {
            _login.update { it.copy(errorGeneral = "Correo o contraseña incorrectos") }
            return
        }

        Almacen.usuarioActual = usuario
        _usuario.value = usuario
        _login.value = LoginUiState()
        onExito()
    }

    fun onNombre(valor: String) = _registro.update { it.copy(nombre = valor, errorNombre = null) }

    fun onCorreoRegistro(valor: String) =
        _registro.update { it.copy(correo = valor, errorCorreo = null) }

    fun onContrasenaRegistro(valor: String) =
        _registro.update { it.copy(contrasena = valor, errorContrasena = null) }

    fun onConfirmacion(valor: String) =
        _registro.update { it.copy(confirmacion = valor, errorConfirmacion = null) }

    fun registrar(onExito: () -> Unit) {
        val estado = _registro.value
        val correo = estado.correo.trim().lowercase()
        val errorCorreo = Validaciones.correo(estado.correo)
            ?: if (Almacen.usuarios.any { it.correo == correo }) "Este correo ya está registrado" else null
        val conErrores = estado.copy(
            errorNombre = Validaciones.nombre(estado.nombre),
            errorCorreo = errorCorreo,
            errorContrasena = Validaciones.contrasenaNueva(estado.contrasena),
            errorConfirmacion = Validaciones.confirmacion(estado.contrasena, estado.confirmacion)
        )
        val hayErrores = listOf(
            conErrores.errorNombre, conErrores.errorCorreo,
            conErrores.errorContrasena, conErrores.errorConfirmacion
        ).any { it != null }
        if (hayErrores) {
            _registro.value = conErrores
            return
        }

        Almacen.usuarios.add(
            Usuario(
                id = Almacen.nuevoIdUsuario(),
                nombre = estado.nombre.trim(),
                correo = correo,
                contrasena = Seguridad.protegerContrasena(estado.contrasena)
            )
        )
        _registro.value = RegistroUiState()
        _login.value = LoginUiState(correo = correo)
        onExito()
    }

    fun limpiarRegistro() {
        _registro.value = RegistroUiState()
    }

    fun cerrarSesion() {
        Almacen.usuarioActual = null
        _usuario.value = null
        _login.value = LoginUiState()
    }
}
