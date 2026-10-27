package com.example.sistematicket.data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class Seguridad {

    private Seguridad() {
    }

    public static String protegerContrasena(String contrasena) {
        String sal = generarSal();
        return sal + ":" + sha256(sal + contrasena);
    }

    public static boolean verificarContrasena(String contrasena, String guardada) {
        if (guardada == null || !guardada.contains(":")) {
            return false;
        }
        String[] partes = guardada.split(":", 2);
        String calculado = sha256(partes[0] + contrasena);
        return MessageDigest.isEqual(
                calculado.getBytes(StandardCharsets.UTF_8),
                partes[1].getBytes(StandardCharsets.UTF_8));
    }

    private static String generarSal() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return aHexadecimal(bytes);
    }

    private static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return aHexadecimal(digest.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private static String aHexadecimal(byte[] bytes) {
        StringBuilder resultado = new StringBuilder();
        for (byte b : bytes) {
            resultado.append(String.format("%02x", b));
        }
        return resultado.toString();
    }
}
