package com.poo.gestorbiblioteca.ui.controller.libros;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Optional;

public class DescripcionLibroController extends Controller {

    // --- Atributos FXML ---
    @FXML private Label tituloLabel;
    @FXML private Label edicionLabel;
    @FXML private Label editorialLabel;
    @FXML private Label anioLabel;
    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    // --- Atributos de Lógica ---
    private Biblioteca biblioteca;
    private Stage stage;
    private Libro libroSeleccionado; // El libro cuyos detalles estamos mostrando

    /**
     * initialize() se deja vacío ya que los datos se inyectarán después.
     */
    @FXML
    private void initialize() {
        // No se necesita lógica de inicialización aquí
    }

    /**
     * Inyector para la lógica de negocio.
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    /**
     * Inyector para el Stage (ventana).
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Método para INYECTAR el libro a mostrar.
     * Este es el método más importante de este controlador.
     */
    public void setLibro(Libro libro) {
        this.libroSeleccionado = libro;
        // Rellenar los Labels con los datos del libro
        if (libro != null) {
            tituloLabel.setText(libro.getTitulo());
            edicionLabel.setText(String.valueOf(libro.getEdicion()));
            editorialLabel.setText(libro.getEditorial());
            anioLabel.setText(String.valueOf(libro.getAnio()));
        } else {
            // Manejar caso de libro nulo si fuera posible
            tituloLabel.setText("N/A");
            edicionLabel.setText("N/A");
            editorialLabel.setText("N/A");
            anioLabel.setText("N/A");
        }
    }

    /**
     * Maneja el clic en el botón "Eliminar".
     */
    @FXML
    private void handleEliminarLibro() {
        if (libroSeleccionado == null) {
            mostrarAlerta("Error", "No hay un libro seleccionado para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        // 1. Pedir confirmación al usuario (muy importante para eliminaciones)
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de que desea eliminar este libro?");
        confirmAlert.setContentText("Libro: " + libroSeleccionado.getTitulo() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 2. Si el usuario confirma, llamar al método de Biblioteca para eliminar
            try {
                biblioteca.eliminarLibro(libroSeleccionado);
                mostrarAlerta("Éxito", "Libro eliminado correctamente.", Alert.AlertType.INFORMATION);
                stage.close(); // Cerrar el popup después de eliminar
            } catch (Exception e) {
                mostrarAlerta("Error al Eliminar", "No se pudo eliminar el libro: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Maneja el clic en el botón "Cerrar".
     */
    @FXML
    private void handleCerrar() {
        stage.close(); // Simplemente cierra la ventana
    }

    /**
     * Método helper para mostrar alertas al usuario.
     */

}
