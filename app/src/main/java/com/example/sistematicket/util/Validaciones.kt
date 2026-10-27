package com.example.sistematicket.util

import com.example.sistematicket.data.TiposArchivo

object Validaciones {

    const val DOMINIO_CORREO = "@duoc.cl"
    const val MAX_TITULO = 60
    const val MAX_DESCRIPCION = 500
    const val MAX_NOMBRE_DOCUMENTO = 60

    private val FORMATO_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val SOLO_LETRAS = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]+$")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        !SOLO_LETRAS.matches(valor.trim()) -> "Solo se permiten letras y espacios"
        else -> null
    }

    fun correo(valor: String): String? {
        val correo = valor.trim().lowercase()
        return when {
            correo.isEmpty() -> "Ingresa tu correo"
            !FORMATO_CORREO.matches(correo) -> "El formato del correo no es válido"
            !correo.endsWith(DOMINIO_CORREO) -> "Debe terminar en $DOMINIO_CORREO"
            else -> null
        }
    }

    fun contrasenaLogin(valor: String): String? =
        if (valor.isEmpty()) "Ingresa tu contraseña" else null

    fun contrasenaNueva(valor: String): String? = when {
        valor.isEmpty() -> "Ingresa una contraseña"
        valor.length < 8 -> "Debe tener al menos 8 caracteres"
        valor.none { it.isUpperCase() } -> "Debe incluir al menos una mayúscula"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun confirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Repite la contraseña"
        confirmacion != contrasena -> "Las contraseñas no coinciden"
        else -> null
    }

    fun titulo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa un título"
        valor.trim().length < 5 -> "Debe tener al menos 5 caracteres"
        valor.trim().length > MAX_TITULO -> "Máximo $MAX_TITULO caracteres"
        else -> null
    }

    fun descripcion(valor: String): String? = when {
        valor.isBlank() -> "Describe el problema"
        valor.trim().length < 10 -> "Debe tener al menos 10 caracteres"
        valor.trim().length > MAX_DESCRIPCION -> "Máximo $MAX_DESCRIPCION caracteres"
        else -> null
    }

    fun seleccion(valor: String, campo: String): String? =
        if (valor.isBlank()) "Selecciona $campo" else null

    fun nombreDocumento(valor: String): String? = when {
        valor.isBlank() -> "Ingresa un nombre para el documento"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        valor.trim().length > MAX_NOMBRE_DOCUMENTO -> "Máximo $MAX_NOMBRE_DOCUMENTO caracteres"
        else -> null
    }

    fun archivo(tipoMime: String?, tamano: Long): String? = when {
        tipoMime == null -> "Selecciona un archivo PDF o Word"
        tipoMime !in TiposArchivo.PERMITIDOS -> "Solo se permiten archivos PDF o Word (.doc, .docx)"
        tamano > TiposArchivo.TAMANO_MAXIMO -> "El archivo supera el máximo de 10 MB"
        else -> null
    }
}
