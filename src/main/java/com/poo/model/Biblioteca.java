import java.util.*;

/**
 * Representa una biblioteca con sus respectivos libros y socios
 * 
 * @author Tobias Cueva, Marcos Marquez, Marcos Rojas
 * @version 1
 */
public class Biblioteca {

    private String nombre;
    private ArrayList<Socio> socios;
    private ArrayList<Libro> libros;
    
    /**
     * Constructor de Biblioteca.
     * 
     * @param p_nombre nombre de la biblioteca.
     */
    public Biblioteca(String p_nombre){
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<>());
        this.setLibros(new ArrayList<>());
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
    
    private void setNombre(String p_nombre){
        this.nombre=p_nombre;
    }
    
    private void setSocios(ArrayList<Socio> p_socios){
        this.socios= p_socios;
    }
    
    private void setLibros(ArrayList<Libro> p_libros){
        this.libros= p_libros;
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
    
    public boolean agregarLibro(Libro p_libro){
        return this.getLibros().add(p_libro);
    }
    
    public boolean agregarSocio(Socio p_socio){
        return this.getSocios().add(p_socio);
    }
    
    public boolean eliminarLibro(Libro p_libro){
        return this.getLibros().remove(p_libro);
    }
    
    public boolean eliminarSocio(Socio p_socio) {
        return this.getSocios().remove(p_socio);
    }
    
    /**
     * Permite agregar un nuevo libro al ArrayList de libros de la biblioteca
     * 
     * @param p_titulo Titulo del libro.
     * @param p_edicion N° de edición del libro.
     * @param p_editorial Editorial del libro.
     * @param p_anio Año del libro.
     * 
     */
    public void nuevoLibro(String p_titulo, int p_edicion, String p_editorial, int p_anio){
        this.agregarLibro(new Libro(p_titulo, p_edicion, p_editorial, p_anio));
    }
    
    /**
     * Busca el socio según el dni pasado como parametro.
     * 
     * @param p_dni Dni del socio.
     * @return Socio buscado.
     */
    public Socio buscarSocio(int p_dni){
        Socio socio=null;
        
        for(Socio socios: this.getSocios()){
            if(socio.getDniSocio()== p_dni){
                socio= socios;
            }
        }
        return socio;
    }
    
    
    /**
     * Devuelve una lista de docentes que son responsables.
     * 
     * @return Lista de docentes responsables
     */
    public ArrayList<Docente> docentesResponsables(){
        ArrayList<Docente> docentesResponsables= new ArrayList<>();
        for(Socio unSocio: this.getSocios()){
            if(unSocio.soyDeLaClase().equalsIgnoreCase("Docente")){
              Docente unDocente= (Docente)unSocio;
            }
            
            if(unDocente.esResponsable()){
                docentesResponsables.add(unDocente);
            }
        }
        
        return docentesResponsables;
        
    }
    
    
    /**
     * Devuelve la cantidad de tipos de socios.
     * 
     * @param p_objeto Tipo de socio.
     * @return Cantidad de socios por tipo.
     */
    public int cantidadDeSociosPorTipo(String p_objeto){
        int contadorSocios = 0;

        for(Socio unSocio : this.getSocios()) {
            if (unSocio.soyDeLaClase().equalsIgnoreCase(p_objeto)) {
                ++contadorSocios;
            }
        }

        return contadorSocios;
    }
    
    /**
     * Devuelve una lista de prestamos vencidos hasta el dia de la fecha.
     * 
     * @return Lista de prestamos vencidos
     */
    public ArrayList<Prestamo> prestamosVencidos(){
        ArrayList<Prestamo> prestamosVencidos= new ArrayList<>();
        Calendar fechaHoy= new GregorianCalendar();
        
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

                p_socio.addPrestamo(new Prestamo(p_fechaRetiro, p_socio, p_libro));
                p_libro.addPrestamo(new Prestamo(p_fechaRetiro, p_socio, p_libro));
                return true;

            } else {

                return false;

            }

        } else {

            return false;

        }

    }

    /**
     * Calcula la cantidad de socios de un tipo especifico existen 
     * 
     * @param p_tipo es el tipo de socio que deseo contar
     * 
     * @return cantidad de socios del tipo solicitado
     */
    public int cantidadSociosPorTipo(String p_tipo){

        int cantTipoSocio = 0;

        for(Socio socios : this.getSocios()) {

            if (socios.soyDeLaClase().equalsIgnoreCase(p_tipo)) {

                cantTipoSocio++;

            }

        }

        return cantTipoSocio;

    }
    
}
