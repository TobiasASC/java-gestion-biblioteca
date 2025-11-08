package com.poo.model;

import java.util.ArrayList;

/**
 * Representa un libro dentro del sistema de gestión de préstamos.
 * Cada libro posee título, edición, editorial, año y una lista de préstamos.
 *
 * @author Lionel
 * @version 1.0
 */
public class Libro {
    private String titulo;
    private int edicion;
    private String editorial;
    private int anio;
    private ArrayList<Prestamo> prestamos;

    /**
     * Constructor que inicializa un libro con sus datos principales.
     *
     * @param p_titulo     El título del libro.
     * @param p_edicion    El número de edición.
     * @param p_editorial  La editorial del libro.
     * @param p_anio       El año de publicación.
     */
    public Libro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        this.setTitulo(p_titulo);
        this.setEdicion(p_edicion);
        this.setEditorial(p_editorial);
        this.setAnio(p_anio);
        this.setPrestamos(new ArrayList<>());
    }

    private void setTitulo(String p_titulo) { this.titulo = p_titulo; }
    private void setEdicion(int p_edicion) { this.edicion = p_edicion; }
    private void setEditorial(String p_editorial) { this.editorial = p_editorial; }
    private void setAnio(int p_anio) { this.anio = p_anio; }
    private void setPrestamos(ArrayList<Prestamo> p_prestamos) { this.prestamos = p_prestamos; }

    public String getTitulo() { return this.titulo; }
    public int getEdicion() { return this.edicion; }
    public String getEditorial() { return this.editorial; }
    public int getAnio() { return this.anio; }
    public ArrayList<Prestamo> getPrestamos() { return this.prestamos; }

    /**
     * Devuelve el último préstamo realizado sobre este libro.
     *
     * @return El préstamo más reciente o null si no tiene ninguno.
     */
    public Prestamo getPrestamo() {
        Prestamo prestamo = null;
        for (Prestamo p : this.getPrestamos()) {
            prestamo = p;
        }
        return prestamo;
    }

    /**
     * Agrega un nuevo préstamo al libro.
     *
     * @param p_prestamo El préstamo a registrar.
     */
    public void addPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    /**
     * Elimina un préstamo de la lista de préstamos.
     *
     * @param p_prestamo El préstamo a eliminar.
     */
    public void removePrestamo(Prestamo p_prestamo) {
        this.getPrestamos().remove(p_prestamo);
    }

    /**
     * Indica si el libro se encuentra actualmente prestado.
     *
     * @return true si el libro está prestado; false en caso contrario.
     */
    public boolean prestado() {
        if (this.getPrestamo() != null) {
            return this.getPrestamo().getFechaDevolucion() == null;
        } else {
            return false;
        }
    }

    /**
     * Devuelve una representación textual básica del libro.
     *
     * @return Cadena con el título del libro.
     */
    public String toString() {
        return "Título: " + this.getTitulo();
    }
}
