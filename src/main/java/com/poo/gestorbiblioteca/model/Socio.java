package com.poo.gestorbiblioteca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Clase abstracta socio
 * @author Fernandez Pablo
 * @version 07/11/25
 */
public abstract class Socio implements Serializable {
    private int dniSocio;
    private String nombre;
    private int diasPrestamo;
    private ArrayList<Prestamo> prestamos;

    /**
     * Constructor de la clase Socio
     * Recibe tres parametros(int, String, int)
     */
    public Socio(int p_dniSocio, String p_nombre, int p_diasPrestamo) {
        this.setDniSocio(p_dniSocio);
        this.setNombre(p_nombre);
        this.setDiasPrestamo(p_diasPrestamo);
        this.setPrestamos(new ArrayList<Prestamo>());
    }

    public void setDniSocio(int p_dniSocio) {
        this.dniSocio = p_dniSocio;
    }

    public void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }

    public void setDiasPrestamo(int p_diasPrestamo) {
        this.diasPrestamo = p_diasPrestamo;
    }

    public void setPrestamos(ArrayList<Prestamo> p_prestamos) {
        this.prestamos = p_prestamos;
    }

    public int getDniSocio() {
        return this.dniSocio;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getDiasPrestamo() {
        return this.diasPrestamo;
    }

    public ArrayList<Prestamo> getPrestamos() {
        return this.prestamos;
    }

    public void agregarPrestamo(Prestamo p_prestamo) {
        this.getPrestamos().add(p_prestamo);
    }

    public void quitarPrestamo(Prestamo p_prestamo) {
        Iterator<Prestamo> iterador = this.prestamos.iterator();

        while (iterador.hasNext()) {
            Prestamo miPrestamo = iterador.next();

            Libro miLibro = miPrestamo.getLibro();
            Libro libroTarget = p_prestamo.getLibro();

            boolean tituloMatches = miLibro.getTitulo().equals(libroTarget.getTitulo());
            boolean edicionMatches = miLibro.getEdicion() == libroTarget.getEdicion();
            boolean anioMatches = miLibro.getAnio() == libroTarget.getAnio();

            if (tituloMatches && edicionMatches && anioMatches) {
                iterador.remove();
                break;
            }
        }
    }

    /**
     * Metodo cantLibrosPrestados
     * @return  Cantidad de libros que fueron prestados
     */
    public int cantLibrosPrestados() {
        int libros = 0;

        for(Prestamo prestamo : this.getPrestamos()) {
            if (prestamo.getFechaDevolucion() == null) {
                ++libros;
            }
        }

        return libros;
    }

    /**
     * @return Devuelve el nombre y dni.
     */
    public String toString() {

        return String.valueOf(this.getDniSocio()) + " - " + this.getNombre();
    }

    /**
     * @return true si puede hacer un prestamo.
     */
    public boolean puedePedir() {
        Calendar hoy = new GregorianCalendar();
        boolean puede = true;

        for(int i = 0; i < this.getPrestamos().size(); ++i) {
            puede = puede && ((Prestamo)this.getPrestamos().get(i)).vencido(hoy);
        }

        return puede;
    }

    public abstract String soyDeLaClase();

}
