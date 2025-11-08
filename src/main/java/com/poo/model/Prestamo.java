import java.util.Calendar;
/**
 * Clase Prestamo.
 * @author Fernandez Pablo 
 * @version 07/11/25
 */
public class Prestamo
{
    private Calendar fechaRetiro;
    private Calendar fechaDevolucion;
    private Socio socio;
    private Libro libro;

    /**
     * Constructor de la clase Prestamo
     */
    public Prestamo(Calendar p_fechaRetiro, Socio p_socio,Libro p_libro, Calendar p_fechaDevolucion){
        this.setFechaRetiro(p_fechaRetiro);
        this.setSocio(p_socio);
        this.setLibro(p_libro);
        this.setFechaDevolucion((Calendar)null);
    }

    private void setFechaRetiro(Calendar p_fecha){
        this.fechaRetiro = p_fecha;
    }
    private void setSocio(Socio p_socio){
        this.socio = p_socio;
    }
    private void setLibro(Libro p_libro){
        this.libro = p_libro;
    }
    private void setFechaDevolucion(Calendar p_fecha){
        this.fechaDevolucion = p_fecha;
    }
    
    public Calendar getFechaRetiro(){
        return this.fechaRetiro;
    }
    public Calendar getFechaDevolucion(){
        return this.fechaDevolucion;
    }
    public Socio getSocio(){
        return this.socio;
    }
    public Libro getLibro(){
        return this.libro;
    }
    
    /**
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void registrarFechaDevolucion(Calendar p_fecha){
        this.fechaDevolucion = p_fecha;
    }
    
    public boolean vencido(Calendar p_fecha){
        int dias = this.getSocio().getDiasPrestamo();
        Calendar retiro = this.getFechaRetiro();
        retiro.add(5, dias);
        return p_fecha.before(retiro) || p_fecha.equals(retiro);
    }
    
    
    
}