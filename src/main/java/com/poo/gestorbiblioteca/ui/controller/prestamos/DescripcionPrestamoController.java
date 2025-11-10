package com.poo.gestorbiblioteca.ui.controller.prestamos;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.exception.LibroNoPrestadoException;
import com.poo.gestorbiblioteca.model.*;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

public class DescripcionPrestamoController extends Controller {

    @FXML private Label estadoLabel;
    @FXML private Label fechaRetiroLabel;
    @FXML private Label fechaLimiteLabel;
    @FXML private Label fechaDevolucionLabel;

    @FXML private Label socioNombreLabel;
    @FXML private Label socioDniLabel;
    @FXML private Label socioTipoLabel;
    @FXML private Label socioContactoLabel;

    @FXML private Label libroTituloLabel;
    @FXML private Label libroEditorialLabel;
    @FXML private Label libroEdicionLabel;

    @FXML private Button finalizarButton;
    @FXML private Button eliminarButton;

    private Biblioteca biblioteca;
    private Stage stage;
    private Prestamo prestamoSeleccionado;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Inyecta la logica de negocio
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    /**
     * Inyecta el stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Inyector principal: Recibe el Préstamo seleccionado y
     * puebla todos los campos de la vista de detalles.
     */
    public void setPrestamo(Prestamo prestamo) {
        this.prestamoSeleccionado = prestamo;
        this.poblarCamposPrestamo();
    }

    public Prestamo getPrestamoSeleccionado(){
        return this.prestamoSeleccionado;
    }

    /**
     * Puebla los campos de la descripcion con los datos del prestamo seleccionado.
     */
    public void poblarCamposPrestamo(){
        if (getPrestamoSeleccionado() == null) return;

        // Puebla Sección Prestamo
        estadoLabel.setText(getPrestamoSeleccionado().estado());
        fechaRetiroLabel.setText(dateFormat.format(getPrestamoSeleccionado().getFechaRetiro().getTime()));

        if (getPrestamoSeleccionado().getSocio() != null) {
            Calendar fechaLimite = (Calendar) getPrestamoSeleccionado().getFechaRetiro().clone();
            fechaLimite.add(Calendar.DAY_OF_MONTH, getPrestamoSeleccionado().getSocio().getDiasPrestamo());
            fechaLimiteLabel.setText(dateFormat.format(fechaLimite.getTime()));
        } else {
            fechaLimiteLabel.setText("N/A");
        }

        if (getPrestamoSeleccionado().getFechaDevolucion() != null) {
            fechaDevolucionLabel.setText(dateFormat.format(getPrestamoSeleccionado().getFechaDevolucion().getTime()));
            finalizarButton.setDisable(true);
        } else {
            fechaDevolucionLabel.setText("Pendiente");
            finalizarButton.setDisable(false);
        }

        // Puebla Sección Socio
        Socio socio = getPrestamoSeleccionado().getSocio();
        if (socio != null) {
            socioNombreLabel.setText(socio.getNombre());
            socioDniLabel.setText(String.valueOf(socio.getDniSocio()));
            socioTipoLabel.setText(socio.soyDeLaClase());

            if (socio instanceof Docente) {
                socioContactoLabel.setText("Área: " + ((Docente) socio).getArea());
            } else if (socio instanceof Estudiante) {
                socioContactoLabel.setText("Carrera: " + ((Estudiante) socio).getCarrera());
            }
        } else {
            // Manejo de datos corruptos (socio nulo)
            socioNombreLabel.setText("Socio no encontrado");
        }

        // Puebla Sección Libro
        Libro libro = getPrestamoSeleccionado().getLibro();
        if (libro != null) {
            libroTituloLabel.setText(libro.getTitulo());
            libroEditorialLabel.setText(libro.getEditorial());
            libroEdicionLabel.setText(String.valueOf(libro.getEdicion()));
        } else {
            libroTituloLabel.setText("Libro no encontrado");
        }
    }

    /**
     * Maneja el clic en el botón "Finalizar Préstamo".
     */
    @FXML
    private void handleFinalizarPrestamo() {
        try {
            biblioteca.devolverLibro(prestamoSeleccionado.getLibro());
        } catch (LibroNoPrestadoException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        mostrarAlerta("Éxito", "Préstamo finalizado correctamente.", Alert.AlertType.INFORMATION);
        stage.close();
    }

    /**
     * Maneja el clic en el botón "Eliminar Préstamo".
     */
    @FXML
    private void handleEliminarPrestamo() {
        // Confirmacion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de que desea eliminar este registro de préstamo?");
        confirmAlert.setContentText("Esto eliminará el préstamo del historial del socio y del libro.\nEsta acción no se puede deshacer.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        //Elimina
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                biblioteca.eliminarPrestamo(prestamoSeleccionado);
                mostrarAlerta("Éxito", "Préstamo eliminado correctamente.", Alert.AlertType.INFORMATION);
                stage.close(); // Cierra el popup
            } catch (Exception e) {
                mostrarAlerta("Error al Eliminar", "No se pudo eliminar el préstamo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}