package com.poo.gestorbiblioteca.utils;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.model.Prestamo;
import com.poo.gestorbiblioteca.model.Socio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Clase de utilidad para poblar la biblioteca. Para poblarla hay que descomentar el llamado
 * a esta clase en la clase Biblioteca, en su metodo cargarDatosDesdeServicio.
 */
public final class PobladoraDatos {

    // Generador de aleatoriedad
    private static final Random rand = new Random();

    // --- Fuentes de Datos (Nombres ficticios) ---
    private static final String[] NOMBRES = {
            "Juan", "María", "José", "Ana", "Luis", "Laura", "Carlos", "Sofía", "Miguel", "Elena",
            "Javier", "Carmen", "David", "Isabel", "Francisco", "Marta", "Daniel", "Paula", "Pedro", "Lucía",
            "Manuel", "Alejandra", "Sergio", "Verónica", "Jorge", "Patricia", "Fernando", "Claudia", "Rafael", "Andrea",
            "Ricardo", "Gabriela", "Eduardo", "Silvia", "Guillermo", "Rosa", "Arturo", "Teresa", "Héctor", "Natalia"
    };
    private static final String[] APELLIDOS = {
            "García", "Rodríguez", "Martínez", "Hernández", "López", "González", "Pérez", "Sánchez", "Romero", "Díaz",
            "Torres", "Álvarez", "Ruiz", "Ramírez", "Flores", "Benítez", "Acosta", "Medina", "Castillo", "Moreno",
            "Gómez", "Jiménez", "Vargas", "Castro", "Méndez", "Ortiz", "Silva", "Núñez", "Rojas", "Herrera",
            "Soto", "Aguirre", "Morales", "Bravo", "Vega", "Reyes", "Figueroa", "Paredes", "Molina", "Campos"
    };
    private static final String[] TITULOS =  {
            // Literatura Argentina
            "Ficciones",
            "El Aleph",
            "Rayuela",
            "El túnel",
            "Sobre héroes y tumbas",
            "Martín Fierro",
            "Boquitas pintadas",
            "La invención de Morel",
            "Respiración artificial",
            "Ferdydurke",

            // Literatura Internacional
            "Cien años de soledad",
            "Don Quijote de la Mancha",
            "1984",
            "Un mundo feliz",
            "El extranjero",
            "Ulises",
            "Crimen y castigo",
            "El proceso",
            "En busca del tiempo perdido",
            "Moby Dick",
            "Orgullo y prejuicio",
            "El gran Gatsby",
            "Lolita",
            "Anna Karénina",
            "La Odisea",

            // Filosofía
            "Así habló Zaratustra",
            "Crítica de la razón pura",
            "El ser y la nada",
            "Ética a Nicómaco",
            "La República",
            "El contrato social",
            "Leviatán",
            "Meditaciones metafísicas",
            "El mundo como voluntad y representación",
            "Tractatus logico-philosophicus",
            "El Anticristo",
            "Más allá del bien y del mal",
            "Ser y tiempo",
            "El mito de Sísifo",
            "Vigilar y castigar",

            // Sistemas / Programación
            "Clean Code (Código Limpio)",
            "Design Patterns (Patrones de Diseño)",
            "The Pragmatic Programmer",
            "Cracking the Coding Interview",
            "Introduction to Algorithms (Cormen)",
            "Sistemas Operativos Modernos (Tanenbaum)",
            "Compiladores: Principios, Técnicas y Herramientas",
            "Redes de Computadoras (Tanenbaum)",
            "Inteligencia Artificial: Un Enfoque Moderno",
            "Code Complete",
            "Java: Cómo programar (Deitel)",
            "Head First Design Patterns",
            "Bases de Datos (Elmasri)",
            "The Mythical Man-Month",
            "Arquitectura Limpia (Robert C. Martin)",

            // Matemáticas
            "Principia Mathematica",
            "Cálculo (Spivak)",
            "Álgebra (Baldor)",
            "Cálculo (Stewart)",
            "Una introducción a la teoría de números",
            "Matemática Discreta y sus Aplicaciones",
            "El hombre que calculaba",
            "Elementos de Euclides",
            "Introduction to Probability (Ross)",
            "Álgebra Lineal (Grossman)",

            // Derecho / Abogacía
            "Teoría general del Derecho (Kelsen)",
            "El Contrato Social (Rousseau)",
            "De los delitos y de las penas (Beccaria)",
            "Código Civil y Comercial Argentino (Comentado)",
            "Manual de Derecho Constitucional (Bidart Campos)",
            "Tratado de Derecho Penal (Zaffaroni)",
            "El espíritu de las leyes (Montesquieu)",
            "Derecho Romano (Di Pietro)",
            "Contratos (Lorenzetti)",
            "Introducción al Análisis del Derecho (Nino)",
            "Sobre la libertad (John Stuart Mill)",
            "Manual de Derecho de Familia",
            "Derecho Procesal Civil y Comercial",
            "Teoría Pura del Derecho",
            "El Federalista (Hamilton, Madison, Jay)"
    };
    private static final String[] EDITORIALES = {"Planeta", "Gredos", "Anaya", "Marcombo", "Prentice Hall", "O'Reilly"};

    private static final String[] CARRERAS = {"Ing. en Sistemas", "Medicina", "Derecho", "Arquitectura", "Biología", "Letras"};
    private static final String[] AREAS = {"Matemática", "Física", "Química", "Historia", "Lengua", "Computación"};

    /**
     * Método público principal. Llama a los métodos privados en orden.
     */
    public static void poblar(Biblioteca biblioteca) {
        // Importante: El orden importa.
        crearSocios(biblioteca, 40);
        crearLibros(biblioteca, 40);
        crearPrestamos(biblioteca, 30);
    }

    /**
     * Genera y añade 20 socios (10 Estudiantes, 10 Docentes).
     */
    private static void crearSocios(Biblioteca biblioteca, int cantidad) {
        System.out.println("Poblando " + cantidad + " socios...");

        // --- Rango de DNI ---
        int minDNI = 20000000;
        int maxDNI = 42000000;

        // El "límite" (bound) para nextInt es (max - min)
        int bound = maxDNI - minDNI;
        // ---

        for (int i = 0; i < cantidad; i++) {
            String nombre = NOMBRES[rand.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[rand.nextInt(APELLIDOS.length)];

            // --- ¡LÓGICA DE DNI ALEATORIO! ---
            // 1. Genera un número aleatorio entre 0 y (bound-1)
            int randomOffset = rand.nextInt(bound);
            // 2. Le suma el valor mínimo para ponerlo en el rango correcto
            int dni = minDNI + randomOffset;
            // ---

            // (Opcional: Comprobar si este DNI aleatorio ya existe)
            if (biblioteca.buscarSocio(dni) != null) {
                 i--; // Si ya existe, re-intenta el bucle
                 continue;
             }

            // 50% de probabilidad de ser Estudiante o Docente
            if (rand.nextBoolean()) {
                String carrera = CARRERAS[rand.nextInt(CARRERAS.length)];
                biblioteca.nuevoSocioEstudiante(dni, nombre + " " + apellido, carrera);
            } else {
                String area = AREAS[rand.nextInt(AREAS.length)];
                biblioteca.nuevoSocioDocente(dni, nombre + " " + apellido, area);
            }
        }
    }

    /**
     * Genera y añade 40 libros.
     */
    private static void crearLibros(Biblioteca biblioteca, int cantidad) {
        System.out.println("Poblando " + cantidad + " libros...");
        for (int i = 0; i < cantidad; i++) {
            String titulo = TITULOS[rand.nextInt(TITULOS.length)];
            String editorial = EDITORIALES[rand.nextInt(EDITORIALES.length)];
            int edicion = rand.nextInt(5) + 1; // Ediciones 1 a 5
            int anio = rand.nextInt(30) + 1990; // Años entre 1990 y 2020

            // Lógica para evitar duplicados (simplificada)
            String tituloFinal = titulo;
            biblioteca.nuevoLibro(tituloFinal, edicion, editorial, anio);
        }
    }

    /**
     * Genera y registra 30 préstamos aleatorios.
     */
    private static void crearPrestamos(Biblioteca biblioteca, int cantidad) {
        System.out.println("Poblando " + cantidad + " préstamos...");

        ArrayList<Socio> socios = biblioteca.getSocios();
        ArrayList<Libro> libros = biblioteca.getLibros();

        if (socios.isEmpty() || libros.isEmpty()) {
            System.err.println("No se pueden crear préstamos si no hay socios o libros.");
            return;
        }

        int prestamosCreados = 0;
        int intentos = 0;

        // Intentamos crear la cantidad de préstamos
        while (prestamosCreados < cantidad && intentos < 1000) {
            intentos++; // Evita un bucle infinito si todos los libros están prestados

            // 1. Elige un socio y un libro al azar
            Socio socioAleatorio = socios.get(rand.nextInt(socios.size()));
            Libro libroAleatorio = libros.get(rand.nextInt(libros.size()));

            // 2. Genera una fecha de retiro aleatoria (en los últimos 60 días)
            Calendar fechaRetiro = new GregorianCalendar();
            fechaRetiro.add(Calendar.DAY_OF_MONTH, -rand.nextInt(60));

            // 3. Intenta realizar el préstamo (esto fallará si el libro ya está prestado)
            boolean exito = biblioteca.prestarLibro(fechaRetiro, socioAleatorio, libroAleatorio);

            if (exito) {
                prestamosCreados++;

                // 50% de probabilidad de que el préstamo ya haya sido devuelto
                if (rand.nextBoolean()) {
                    // Busca el préstamo que acabamos de crear
                    Prestamo p = libroAleatorio.getPrestamo();
                    if (p != null) {
                        Calendar fechaDev = (Calendar) fechaRetiro.clone();
                        // Devuelto entre 1 y 30 días después
                        fechaDev.add(Calendar.DAY_OF_MONTH, rand.nextInt(30) + 1);
                        p.registrarFechaDevolucion(fechaDev);
                    }
                }
            }
        }
        System.out.println("Se crearon " + prestamosCreados + " préstamos.");
    }
}
