package com.example.sistematicket

import com.example.sistematicket.data.Seguridad
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun la_contrasena_no_se_guarda_en_texto_plano() {
        val guardada = Seguridad.protegerContrasena("Secreta123")
        assertFalse(guardada.contains("Secreta123"))
    }

    @Test
    fun verifica_la_contrasena_correcta() {
        val guardada = Seguridad.protegerContrasena("Secreta123")
        assertTrue(Seguridad.verificarContrasena("Secreta123", guardada))
        assertFalse(Seguridad.verificarContrasena("secreta123", guardada))
    }

    @Test
    fun misma_contrasena_produce_hash_distinto_por_la_sal() {
        assertNotEquals(
            Seguridad.protegerContrasena("Secreta123"),
            Seguridad.protegerContrasena("Secreta123")
        )
    }
}
