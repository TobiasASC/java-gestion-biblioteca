package com.poo.model;

import java.io.Serializable;

/**
 * Representa un docente, con sus atributos DNI, nombre, días de préstamo, préstamo y carrera.
 *
 * @version 04.11.2025
 */
public class Docente extends Socio implements Serializable {
    private String area;

    public Docente(int p_dniSocio, String p_nombre, String p_area) {
        super(p_dniSocio, p_nombre, 5);
        this.setArea(p_area);
    }

    public void setArea(String p_area) {
        this.area = p_area;
    }

    public String getArea() {
        return this.area;
    }

    /**
     * Retorna true si el docente no tiene ni tuvo préstamos vencidos. Caso contrario retorna false.
     */
    public boolean esResponsable() {
        boolean resultado = super.puedePedir();

        //Evalua entre los prestamos ya finalizados si fueron devueltos a tiempo, es decir
        // si vencido(prestamo.fechaDevolucion)
        for(Prestamo prestamo : super.getPrestamos()){
            if(prestamo.getFechaDevolucion() != null){
                resultado = resultado && prestamo.vencido(prestamo.getFechaDevolucion());
            }
        }
        return resultado;
    }

    /**
     * Evalúa si el docente es responsable. En caso de serlo, incrementa la cantidad de días de préstamo disponible
     * en la cantidad pasada por parámetro.
     * @param p_dias Representa la cantidad de días a adicionar.
     */
    public void agregarDiasDePrestamo(int p_dias) {
        if (this.esResponsable()) {
            super.setDiasPrestamo(super.getDiasPrestamo() + p_dias);
        }

    }

    /*
     * Retorna un String con el nombre de la clase.
     */
    @Override
    public String soyDeLaClase() {
        return "Docente";
    }
}
