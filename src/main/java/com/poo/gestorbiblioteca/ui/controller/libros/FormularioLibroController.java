package com.poo.gestorbiblioteca.ui.controller.libros;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioLibroController extends Controller {

    @FXML private TextField tituloTextField;
    @FXML private TextField edicionTextField;
    @FXML private TextField editorialTextField;
    @FXML private TextField anioTextField;
    @FXML private Button crearButton;

    private Biblioteca biblioteca;
    private Stage stage; // La ventana popup

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Se llama al hacer clic en el botón "Crear"
     */
    @FXML
    private void handleCrearLibro() {
        String titulo = tituloTextField.getText();
        String edicionStr = edicionTextField.getText();
        String editorial = editorialTextField.getText();
        String anioStr = anioTextField.getText();

        //Validaciones
        if (titulo.isBlank() || edicionStr.isBlank() || editorial.isBlank() || anioStr.isBlank()) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.", Alert.AlertType.INFORMATION);
            return;
        }

        int edicion;
        int anio;
        try {
            edicion = Integer.parseInt(edicionStr);
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Validación", "Edición y Año deben ser números.", Alert.AlertType.INFORMATION);
            return;
        }


        try {

            biblioteca.nuevoLibro(titulo, edicion, editorial, anio);

        } catch (Exception e) {
            mostrarAlerta("Error al Crear", "No se pudo crear el libro: " + e.getMessage(), Alert.AlertType.INFORMATION);
            return;
        }

        mostrarAlerta("Éxito", "Libro creado correctamente.", Alert.AlertType.INFORMATION);
        stage.close();
    }

}