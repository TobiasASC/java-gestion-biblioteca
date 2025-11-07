import java.io.Serializable;

/**
 * Representa un estudiante, con sus atributos DNI, nombre, días de préstamo, préstamo y carrera.
 *
 * @author Lucas Kruzolek
 * @version 07.11.2025
 */
public class Estudiante extends Socio implements Serializable {
    private String carrera;

    public Estudiante(int p_dniSocio, String p_nombre, String p_carrera) {
        super(p_dniSocio, p_nombre, 20);
        this.setCarrera(p_carrera);
    }

    public void setCarrera(String p_carrera) {
        this.carrera = p_carrera;
    }

    public String getCarrera() {
        return this.carrera;
    }

    @Override
    public boolean puedePedir() {
        return super.puedePedir() && super.cantLibrosPrestados()<3;
    }

    @Override
    public String soyDeLaClase() {
        return "Estudiante";
    }
}
