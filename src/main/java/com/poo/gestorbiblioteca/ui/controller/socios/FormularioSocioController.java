package com.poo.gestorbiblioteca.ui.controller.socios;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormularioSocioController extends Controller {

    @FXML private TextField dniTextField;
    @FXML private TextField nombreTextField;
    @FXML private ComboBox<String> categoriaTextField;
    @FXML private Label labelDinamico;
    @FXML private TextField campoDinamico;
    @FXML private Button crearButton;

    private Biblioteca biblioteca;
    private Stage stage; // La ventana popup

    // Opciones para el ComboBox
    private final String TIPO_ESTUDIANTE = "Estudiante";
    private final String TIPO_DOCENTE = "Docente";

    /**
     * Se llama automáticamente después de cargar el FXML.
     * Aquí poblamos el ComboBox y configuramos el listener.
     */
    @FXML
    private void initialize() {

        categoriaTextField.getItems().addAll(TIPO_ESTUDIANTE, TIPO_DOCENTE);

        labelDinamico.setVisible(false);
        campoDinamico.setVisible(false);

        // 3. Añadir un "listener" para reaccionar a la selección del ComboBox
        categoriaTextField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                labelDinamico.setVisible(false);
                campoDinamico.setVisible(false);
            } else if (newVal.equals(TIPO_ESTUDIANTE)) {
                labelDinamico.setText("Carrera:");
                campoDinamico.setPromptText("Ingrese la carrera");
                labelDinamico.setVisible(true);
                campoDinamico.setVisible(true);
            } else if (newVal.equals(TIPO_DOCENTE)) {
                labelDinamico.setText("Área:");
                campoDinamico.setPromptText("Ingrese el área");
                labelDinamico.setVisible(true);
                campoDinamico.setVisible(true);
            }
        });
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    /**
     * Inyector para el Stage (ventana) (llamado desde SociosController)
     * Lo necesitamos para poder cerrar el popup.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Se llama al hacer clic en el botón "Crear"
     */
    @FXML
    private void handleCrearSocio() {

        String categoria = categoriaTextField.getValue();
        String nombre = nombreTextField.getText();
        String dniStr = dniTextField.getText();
        String dinamico = campoDinamico.getText();
        int dni;

        //Validaciones
        if (categoria == null || nombre.isBlank() || dniStr.isBlank() || dinamico.isBlank()) {
            mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            dni = Integer.parseInt(dniStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Validación", "El DNI debe ser un número.", Alert.AlertType.INFORMATION);
            return;
        }
        for(Socio socio : biblioteca.getSocios()){
            if(dni==socio.getDniSocio()){
                mostrarAlerta("Error de Valicación", "El DNI ya existe", Alert.AlertType.INFORMATION);
                return;
            }
        }

        //Cargar
        try {
            if (categoria.equals(TIPO_ESTUDIANTE)) {
                biblioteca.nuevoSocioEstudiante(dni, nombre, dinamico);
            } else if (categoria.equals(TIPO_DOCENTE)) {
                biblioteca.nuevoSocioDocente(dni, nombre, dinamico);
            }
        } catch (Exception e) {

            mostrarAlerta("Error al Crear", "No se pudo crear el socio: " + e.getMessage(), Alert.AlertType.INFORMATION);
            return;
        }


        mostrarAlerta("Éxito", "Socio creado correctamente.", Alert.AlertType.INFORMATION);
        stage.close();
    }

    /**
     * Muestra alertas al usuario
     */

}
