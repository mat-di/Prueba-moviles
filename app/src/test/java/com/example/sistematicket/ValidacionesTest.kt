package com.example.sistematicket

import com.example.sistematicket.data.TiposArchivo
import com.example.sistematicket.util.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correo_valido_institucional() {
        assertNull(Validaciones.correo("ana.perez@duoc.cl"))
        assertNull(Validaciones.correo("  Ana.Perez@DUOC.cl  "))
    }

    @Test
    fun correo_invalido() {
        assertEquals("Ingresa tu correo", Validaciones.correo(""))
        assertEquals("El formato del correo no es válido", Validaciones.correo("ana@"))
        assertEquals("Debe terminar en @duoc.cl", Validaciones.correo("ana@gmail.com"))
    }

    @Test
    fun contrasena_nueva_exige_largo_mayuscula_y_numero() {
        assertNotNull(Validaciones.contrasenaNueva("Abc12"))
        assertNotNull(Validaciones.contrasenaNueva("abcdefg1"))
        assertNotNull(Validaciones.contrasenaNueva("Abcdefgh"))
        assertNull(Validaciones.contrasenaNueva("Abcdefg1"))
    }

    @Test
    fun confirmacion_debe_coincidir() {
        assertEquals("Las contraseñas no coinciden", Validaciones.confirmacion("Abcdefg1", "Abcdefg2"))
        assertNull(Validaciones.confirmacion("Abcdefg1", "Abcdefg1"))
    }

    @Test
    fun nombre_solo_letras() {
        assertNull(Validaciones.nombre("José Muñoz"))
        assertNotNull(Validaciones.nombre("Jo"))
        assertNotNull(Validaciones.nombre("Juan123"))
    }

    @Test
    fun ticket_titulo_y_descripcion() {
        assertNotNull(Validaciones.titulo("Hola"))
        assertNull(Validaciones.titulo("No funciona el wifi"))
        assertNotNull(Validaciones.descripcion("corto"))
        assertNull(Validaciones.descripcion("El equipo no enciende desde ayer"))
    }

    @Test
    fun archivo_solo_pdf_o_word_hasta_10mb() {
        assertNull(Validaciones.archivo(TiposArchivo.PDF, 1024))
        assertNull(Validaciones.archivo(TiposArchivo.DOCX, 1024))
        assertNotNull(Validaciones.archivo("image/png", 1024))
        assertNotNull(Validaciones.archivo(TiposArchivo.PDF, TiposArchivo.TAMANO_MAXIMO + 1))
    }
}
