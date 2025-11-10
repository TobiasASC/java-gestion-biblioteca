package com.poo.gestorbiblioteca.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Clase Prestamo.
 * @author Fernandez Pablo
 * @version 07/11/25
 */

public class Prestamo implements Serializable {
    private Calendar fechaRetiro;
    private Calendar fechaDevolucion;
    private Socio socio;
    private Libro libro;


    /**
     * Constructor de la clase Prestamo
     * Recibe 4 paramentros(Calendar, Socio, Libro, Calendar)
     */
    public Prestamo(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        this.setFechaRetiro(p_fechaRetiro);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
        this.setFechaDevolucion((Calendar)null);
    }

    public void setFechaRetiro(Calendar p_fecha) {
        this.fechaRetiro = p_fecha;
    }

    public void setFechaDevolucion(Calendar p_fecha) {
        this.fechaDevolucion = p_fecha;
    }

    public void setSocio(Socio p_socio) {
        this.socio = p_socio;
    }

    public void setLibro(Libro p_libro) {
        this.libro = p_libro;
    }

    public Calendar getFechaRetiro() {
        return this.fechaRetiro;
    }

    public Calendar getFechaDevolucion() {
        return this.fechaDevolucion;
    }

    public Socio getSocio() {
        return this.socio;
    }

    public Libro getLibro() {
        return this.libro;
    }

    /**
     * Metodo registarFechaDevolucion(Calendar): Registra la fecha de devolución
     * @return  No devuelve ningun valor
     */
    public void registrarFechaDevolucion(Calendar p_fecha) {
        this.fechaDevolucion = p_fecha;
    }

    /**
     * Método vencido(Calendar):Verifica si la fecha del prestamo no vencio.
     * @return Devuelve true o false dependiendo si la fecha esta vencida.
     */
    public boolean vencido(Calendar p_fecha) {
        int dias = this.getSocio().getDiasPrestamo();
        Calendar retiro = (Calendar) this.getFechaRetiro().clone();
        retiro.add(5, dias);
        return p_fecha.after(retiro);
    }

    /**
     * Devuelve un String con el estado del prestamo.
     */
    public String estado() {
        if(this.getFechaDevolucion() != null){
            if(this.vencido(this.getFechaDevolucion())){
                return "Finalizado - Vencido";
            }   return "Finalizado";
        } else {
            Calendar hoy = new GregorianCalendar();
            if(this.vencido(hoy)){
                return "Vencido";
            }   return "Pendiente";
        }
    }

}
