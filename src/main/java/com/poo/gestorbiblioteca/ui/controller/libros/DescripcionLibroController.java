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

    @FXML private Label tituloLabel;
    @FXML private Label edicionLabel;
    @FXML private Label editorialLabel;
    @FXML private Label anioLabel;
    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    private Biblioteca biblioteca;
    private Stage stage;
    private Libro libroSeleccionado;

    /**
     * Inyecta la lógica de negocio.
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    /**
     * Inyecta el Stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Inyecta el libro a mostrar.
     */
    public void setLibroSeleccionado(Libro libro) {
        this.libroSeleccionado = libro;
        this.poblarCamposLibro();
    }

    public Libro getLibroSeleccionado(){
        return this.libroSeleccionado;
    }

    /**
     * Puebla los campos de la descripcion con los datos del libro seleccionado.
     */
    public void poblarCamposLibro(){
        if (this.getLibroSeleccionado() != null) {
            tituloLabel.setText(getLibroSeleccionado().getTitulo());
            edicionLabel.setText(String.valueOf(getLibroSeleccionado().getEdicion()));
            editorialLabel.setText(getLibroSeleccionado().getEditorial());
            anioLabel.setText(String.valueOf(getLibroSeleccionado().getAnio()));
        } else {
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

        // Confirmación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de que desea eliminar este libro?");
        confirmAlert.setContentText("Libro: " + libroSeleccionado.getTitulo() + "\nEsta acción no se puede deshacer.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        // Eliminación
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
        stage.close();
    }
}
