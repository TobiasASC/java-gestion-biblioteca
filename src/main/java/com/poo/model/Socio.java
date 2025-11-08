import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * Clase abstracta socio
 * @author Fernandez Pablo 
 * @version 07/11/25
 */
public abstract class Socio
{
    private int dniSocio;
    private String nombre;
    private int diasPrestamo;
    private ArrayList<Prestamo> prestamos;
    
    /**
     * Constructor de la clase Socio
     */
    public Socio(int p_dniSocio, String p_nombre, int p_diasPrestamo){
      this.setDniSocio(p_dniSocio);
      this.setNombre(p_nombre);
      this.setDiasPrestamo(p_diasPrestamo);
      this.setPrestamos(new ArrayList());
    }

    
    private void setDniSocio(int p_dniSocio){
        this.dniSocio = p_dniSocio;
    }
    
    private void setNombre(String p_nombre){
        this.nombre = p_nombre;
    }
    
    private void setDiasPrestamo(int p_diasPrestamo){
        this.diasPrestamo = p_diasPrestamo;
    }
    
    private void setPrestamos(ArrayList p_prestamos){
        this.prestamos = p_prestamos;
    }
    
    public int getDniSocio(){
        return this.dniSocio;
    } 
    
    public String getNombre(){
        return this.nombre;
    } 
    
    public int getDiasPrestamo(){
        return this.diasPrestamo;
    }
    
    public ArrayList<Prestamo> getPrestamos(){
        return this.prestamos;
    }
    
    public void agregarPrestamo(Prestamo p_prestamo){
        this.getPrestamos().add(p_prestamo);
    }
    
    public void quitarPrestamo(Prestamo p_prestamo){
        this.getPrestamos().remove(p_prestamo);
    }
    
    /**
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public int cantLibrosPrestados(){
        int libros = 0;
        for(int i = 0; i < this.getPrestamos().size(); i++){
            if(((Prestamo)this.getPrestamos().get(i)).getFechaDevolucion()==null){
                ++libros;
            }
        }
        
        return libros;
    }
    
    public String toString(){
        return ("D.N.I. : " + this.getDniSocio() + "||" + this.getNombre() + "(" + soyDeLaClase() + ") || Libros Prestados: " + this.cantLibrosPrestados());
    }
    
    public boolean puedePedir(){
        Calendar hoy = new GregorianCalendar();
        boolean puede = true;
        
        for(int i = 0; i < this.getPrestamos().size(); i++){
            puede = puede &&((Prestamo)this.getPrestamos().get(i)).vencido(hoy);
        }
        return puede;
    }
    
    public abstract String soyDeLaClase();
    
}