package com.leonhardsen.notisblokk.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Crypthograph {

    public static String SHA256(String senha) {
        StringBuilder criptografada = new StringBuilder();
        try {
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = algoritmo.digest(senha.getBytes(StandardCharsets.UTF_8));
            for (byte b : messageDigest) {
                criptografada.append(String.format("%02X", 0xFF & b));
            }
            return criptografada.toString();
        } catch (Exception e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e);
        }
    }

}
