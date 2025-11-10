package com.poo.gestorbiblioteca.core;

import com.poo.gestorbiblioteca.exception.LibroNoPrestadoException;
import com.poo.gestorbiblioteca.model.*;
import com.poo.gestorbiblioteca.persistence.DatosPersistidos;
import com.poo.gestorbiblioteca.persistence.ObjectStreamPersistenceService;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

public class Biblioteca {
    private String nombre;
    private ArrayList<Socio> socios;
    private ArrayList<Libro> libros;
    private ObjectStreamPersistenceService persistenceService;
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String ARCHIVO_DATOS = USER_HOME + File.separator + "biblioteca.dat";


    /**
     * Constructor de Biblioteca.
     *
     * @param p_nombre nombre de la biblioteca.
     */
    public Biblioteca(String p_nombre) {
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<>());
        this.setLibros(new ArrayList<>());

        this.persistenceService = new ObjectStreamPersistenceService(ARCHIVO_DATOS);

        this.cargarDatosDesdeServicio(this);

    }

    /**
     * Constructor de Biblioteca.
     *
     * @param p_nombre nombre de la biblioteca.
     * @param p_socios socios de la biblioteca.
     * @param p_libros libros de la biblioteca.
     */
    public Biblioteca(String p_nombre, ArrayList<Socio> p_socios, ArrayList<Libro> p_libros){
        this.setNombre(p_nombre);
        this.setSocios(p_socios);
        this.setLibros(p_libros);
    }

    //Getters y Setters
    public void setNombre(String p_nombre) {
        this.nombre = p_nombre;
    }

    private void setSocios(ArrayList<Socio> p_socios) {
        this.socios = p_socios;
    }

    private void setLibros(ArrayList<Libro> p_libros) {
        this.libros = p_libros;
    }

    public String getNombre() {
        return this.nombre;
    }

    public ArrayList<Socio> getSocios() {
        return this.socios;
    }

    public ArrayList<Libro> getLibros() {
        return this.libros;
    }


    //Metodos de Libro
    public boolean agregarLibro(Libro p_libro) {
        return this.getLibros().add(p_libro);
    }

    public boolean eliminarLibro(Libro p_libro) {
        if (p_libro == null) {
            return false;
        }
        if (p_libro.getPrestamos() != null && !p_libro.getPrestamos().isEmpty()) {

            // Creamos una copia para evitar un ConcurrentModificationException
            ArrayList<Prestamo> prestamosAEliminar = new ArrayList<>(p_libro.getPrestamos());

            for (Prestamo prestamo : prestamosAEliminar) {
                Socio socioDelPrestamo = prestamo.getSocio();
                if (socioDelPrestamo != null) {
                    socioDelPrestamo.quitarPrestamo(prestamo);
                }
            }

        }
        return this.getLibros().remove(p_libro);
    }
//    public boolean removeLibro(Libro p_libro) {
//        return this.getLibros().remove(p_libro);
//    }
    /**
     * Permite agregar un nuevo libro al ArrayList de libros de la biblioteca
     *
     * @param p_titulo Titulo del libro.
     * @param p_edicion N° de edición del libro.
     * @param p_editorial Editorial del libro.
     * @param p_anio Año del libro.
     *
     */
    public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio) {
        this.agregarLibro(new Libro(p_titulo, p_edicion, p_editorial, p_anio));
    }

    /**
     * Presta un libro a un socio
     *
     * @param p_fechaRetiro dia que se realizo el prestamo del libro
     * @param p_socio socio a quien se le presta el libro
     * @param p_libro libro prestado
     *
     * @return (true) registra el prestamo del libro / (false) retorna false
     */
    public boolean prestarLibro(Calendar p_fechaRetiro, Socio p_socio, Libro p_libro) {
        if (p_socio != null) {
            if (p_socio.puedePedir() && !p_libro.prestado()) {
                Prestamo nuevoPrestamo = new Prestamo(p_fechaRetiro, p_socio, p_libro);

                p_socio.agregarPrestamo(nuevoPrestamo);
                p_libro.agregarPrestamo(nuevoPrestamo);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Asigna la fecha actual como fecha de devolucion
     * (true) asigna la fecha de devolucion
     * (false) el libro se encuentra en la bibliote
     *
     * @param p_libro libro devuelto
     */
    public void devolverLibro(Libro p_libro) throws LibroNoPrestadoException {
        if (p_libro.prestado()) {
            p_libro.getPrestamo().registrarFechaDevolucion(new GregorianCalendar());
        } else {
            throw new LibroNoPrestadoException("El libro '" + p_libro.getTitulo() + "' no se puede devolver ya que se encuentra en la biblioteca\n");
        }
    }

    /**
     * Devuelve el nombre del socio que posee un libro en especifico
     *
     * @param p_libro libro que se desea conocer su ubicacion
     * @return (true) nombre del socio que posee el libro / (false) el libro esta en la biblioteca
     */
    public String quienTieneElLibro(Libro p_libro) throws LibroNoPrestadoException {
        if (p_libro.prestado()) {
            return p_libro.getPrestamo().getSocio().getNombre();
        } else {
            throw new LibroNoPrestadoException("El libro '" + p_libro.getTitulo() + "' se encuentra en la biblioteca\n");
        }
    }

    /**
     * Retorna un ArrayList de los libros que no se encuentran prestados.
     */
    public ArrayList<Libro> librosDispobibles(){
        ArrayList<Libro> librosDisponibles = new ArrayList<>();
        if (this.getLibros() != null) {
            for(Libro libro : this.getLibros()){
                if(!libro.prestado()){
                librosDisponibles.add(libro);
                }
            }
        }
        return librosDisponibles;
    }


    //Metodos de Socio
    public boolean agregarSocio(Socio p_socio) {
        return this.getSocios().add(p_socio);
    }

    public boolean eliminarSocio(Socio p_socio){
        return this.getSocios().remove(p_socio);
    }

    public void nuevoSocioEstudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        this.agregarSocio(new Estudiante(p_dniSocio, p_nombre, p_carrera));
    }

    public void nuevoSocioDocente(int p_dniSocio, String p_nombre, String p_area) {
        this.agregarSocio(new Docente(p_dniSocio, p_nombre, p_area));
    }

    /**
     * Devuelve la cantidad de tipos de socios.
     *
     * @param p_objeto Tipo de socio.
     * @return Cantidad de socios por tipo.
     */
    public int cantidadSociosPorTipo(String p_objeto) {
        int contadorSocios = 0;

        for(Socio unSocio : this.getSocios()) {
            if (unSocio.soyDeLaClase().equalsIgnoreCase(p_objeto)) {
                ++contadorSocios;
            }
        }

        return contadorSocios;
    }

    public ArrayList<Socio> sociosHabilitados(){
        ArrayList<Socio> sociosHabilitados = new ArrayList<>();
        if (this.getSocios() != null) {
            for(Socio socio : this.getSocios()){
                if(socio.puedePedir()){
                    sociosHabilitados.add(socio);
                }
            }
        }
        return sociosHabilitados;
    }

    /**
     * Busca el socio según el dni pasado como parametro.
     *
     * @param p_dni Dni del socio.
     * @return Socio buscado.
     */
    public Socio buscarSocio(int p_dni) {
        Socio socio = null;

        for(Socio socios : this.getSocios()) {
            if (socios.getDniSocio() == p_dni) {
                socio = socios;
            }
        }

        return socio;
    }

    public ArrayList<Docente> docentesResponsables() {
        ArrayList<Docente> docResponsables = new ArrayList<>();

        for(Socio socios : this.getSocios()) {
            if (socios.soyDeLaClase().equalsIgnoreCase("Docente")) {
                Docente unDocente = (Docente)socios;
                if (unDocente.esResponsable()) {
                    docResponsables.add(unDocente);
                }
            }
        }

        return docResponsables;
    }

    //Metodos de Prestamo
    /**
     * Devuelve una lista de prestamos vencidos hasta el dia de la fecha.
     *
     * @return Lista de prestamos vencidos
     */
    public ArrayList<Prestamo> prestamosVencidos() {
        ArrayList<Prestamo> prestamosVencidos = new ArrayList<>();
        Calendar fechaHoy = new GregorianCalendar();

        for(Libro unLibro : this.getLibros()) {
            for(Prestamo unPrestamo : unLibro.getPrestamos()) {
                if (unPrestamo.vencido(fechaHoy)) {
                    prestamosVencidos.add(unPrestamo);
                }
            }
        }

        return prestamosVencidos;
    }

    /**
     * Elimina un préstamo de forma permanente del sistema, rompiendo la
     * referencia tanto en el Socio como en el Libro.
     */
    public void eliminarPrestamo(Prestamo p_prestamo) {
        if (p_prestamo == null) return;

        if (p_prestamo.getSocio() != null) {
            p_prestamo.getSocio().quitarPrestamo(p_prestamo);
        }

        if (p_prestamo.getLibro() != null) {
            p_prestamo.getLibro().removePrestamo(p_prestamo);
        }
    }


    //Metodos de Listas
    /**
     +    Lista los titulos de libros con los que cuenta la biblioteca, lo hace sin repetir elementos
     +    @return un conjunto de titulos en una cadena, separada por saltos de linea
     +    */
    public String listaDeTitulos() {
        int i = 0;
        HashSet<String> titulos = new HashSet<>();

        for(Libro unLibro : this.getLibros()) {
            titulos.add(unLibro.getTitulo());
        }

        String listaTitulos = this.getNombre() + "\n\nLista de Títulos:\n\n";

        for(String unLibro : titulos) {
            ++i;
            listaTitulos = listaTitulos + i + ") " + unLibro + "\n";
        }

        return listaTitulos;
    }

    /**
     +    Regresa un String con la lista de socios de la Biblioteca
     +    @param no recibe parametros
     +    @return regresa un valor de tipo String
     +    @trows no dispara ninguna excepcion
     +    */
    public String listaDeSocios() {
        String lista = this.getNombre() + "\n\nLista de Socios:\n\n";
        int i = 0;
        int docentes = 0;
        int estudiantes = 0;

        for(Socio socios : this.getSocios()) {
            ++i;
            if (socios.soyDeLaClase().equalsIgnoreCase("Docente")) {
                ++docentes;
            } else if (socios.soyDeLaClase().equalsIgnoreCase("Estudiante")) {
                ++estudiantes;
            }

            lista = lista + i + ")" + socios.toString() + "\n";
        }

        return lista +
                "\n******************************************\nCantidad de Socios de tipo Estudiante: " + estudiantes +
                "\nCantidad de Socios de tipo Docente: " + docentes +
                "\n******************************************\n";
    }

    /**
     +    Regresa un String con la lista de libros de la biblioteca
     +    @param no recibe parametros
     +    @return regresa un valor de tipo String
     +    @trows no dispara ninguna excepcion
     +    */
    public String listaDeLibros() {
        int i = 0;
        String listaLibros = this.getNombre() + "\n\nLista de Libros:\n\n";

        for(Libro libros : this.getLibros()) {
            ++i;
            if (!libros.prestado()) {
                listaLibros = listaLibros + i + ") " + libros.toString() + "||Prestado:(NO)\n";
            } else {
                listaLibros = listaLibros + i + ") " + libros.toString() + "||Prestado:(SI)\n";
            }
        }

        return listaLibros;
    }

    /**
     +    Regresa un String con la lista de docentes que hallan sido responsables con la fecha de devolucion
     +    @param no recibe parametros
     +    @return regresa un valor de tipo String
     +    @trows no dispara ninguna excepcion
     */
    public String listaDeDocentesResponsables() {
        String listaResponsables = this.getNombre() + "\n\nLista de Docentes Responsables:\n\n";

        for(Docente docentes : this.docentesResponsables()) {
            listaResponsables = listaResponsables + "*" + docentes.toString() + "\n";
        }

        return listaResponsables;
    }


    //Metodos de Persistencia
    private void cargarDatosDesdeServicio(Biblioteca biblioteca) {
        try {
            // Le pide al servicio que cargue los datos
            DatosPersistidos datos = persistenceService.cargarDatos(biblioteca);
            this.setSocios(datos.getSocios());
            this.setLibros(datos.getLibros());

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ERROR FATAL AL CARGAR DATOS. No se pudo leer " + ARCHIVO_DATOS);
            e.printStackTrace();
           // Si falla la carga se inicializan los atributos con listas vacías para evitar un crash.
            this.setSocios(new ArrayList<>());
            this.setLibros(new ArrayList<>());
        }
    }

    public void guardarDatosEnArchivo() {
        try {
            DatosPersistidos datosActuales = new DatosPersistidos(this.getSocios(), this.getLibros());
            persistenceService.guardarDatos(datosActuales);
            System.out.println("Datos guardados.");

        } catch (IOException e) {
            System.err.println("ERROR: No se pudieron guardar los datos en " + ARCHIVO_DATOS);
            e.printStackTrace();
        }
    }

}