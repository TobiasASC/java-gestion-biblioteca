import java.util.*;

/**
 * 
 */
public class Biblioteca {
    private String nombre;
    private ArrayList<Socio> socios;
    private ArrayList<Libro> libros;
    
    /**
     * 
     */
    public Biblioteca(String p_nombre){
        this.setNombre(p_nombre);
        this.setSocios(new ArrayList<>());
        this.setLibros(new ArrayList<>());
    }
    
    /**
     * 
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
    
    
}
