package com.poo.gestorbiblioteca.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringNormalizador {

    private static final Pattern DIACRITICS_PATTERN =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String minusculas = texto.toLowerCase();

        // Descompone
        String normalizado = Normalizer.normalize(minusculas, Normalizer.Form.NFD);

        // Limpia
        return DIACRITICS_PATTERN.matcher(normalizado).replaceAll("");
    }
}
