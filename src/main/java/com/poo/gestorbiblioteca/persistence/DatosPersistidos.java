package com.poo.gestorbiblioteca.persistence;


import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.model.Socio;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase contenedora que agrupa todos los datos
 * del estado de la aplicación para que puedan ser guardados
 * en un solo archivo por el servicio de persistencia.
 */
public class DatosPersistidos implements Serializable {

    private static final long serialVersionUID = 1L;

    public ArrayList<Socio> socios;
    public ArrayList<Libro> libros;

    public DatosPersistidos() {
        this.setSocios(new ArrayList<>());
        this.setLibros(new ArrayList<>());
    }

    public DatosPersistidos(ArrayList<Socio> socios, ArrayList<Libro> libros) {
        this.setSocios(socios);
        this.setLibros(libros);
    }

    public ArrayList<Socio> getSocios() {
        return socios;
    }

    public void setSocios(ArrayList<Socio> socios) {
        this.socios = socios;
    }

    public ArrayList<Libro> getLibros() {
        return libros;
    }

    public void setLibros(ArrayList<Libro> libros) {
        this.libros = libros;
    }
}