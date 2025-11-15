package com.poo.gestorbiblioteca.ui.controller.ajustes;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.utils.Alerta;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.poo.gestorbiblioteca.utils.Alerta.mostrarAlerta;

public class AjustesController {

    @FXML
    private TextField nombreTextField;

    private Biblioteca biblioteca;
    private Stage stage;

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        nombreTextField.setText(biblioteca.getNombre());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Se llama al presionar el botón "Guardar"
     */
    @FXML
    private void handleGuardarNombre() {
        String nuevoNombre = nombreTextField.getText();
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            mostrarAlerta("Error", "El nombre no puede estar vacío.", Alert.AlertType.ERROR);
            return;
        }

        biblioteca.setNombre(nuevoNombre);
        stage.close();
    }

}
