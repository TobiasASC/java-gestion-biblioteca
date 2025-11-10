package com.poo.gestorbiblioteca.ui.controller.prestamos;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.controlsfx.control.SearchableComboBox;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class FormularioPrestamoController extends Controller {

    // --- Atributos FXML (¡Tipos cambiados!) ---
    @FXML private SearchableComboBox<Socio> socioComboBox;
    @FXML private SearchableComboBox<Libro> libroComboBox;
    @FXML private Button crearButton;
    @FXML private DatePicker fechaDatePicker;


    private Biblioteca biblioteca;
    private Stage stage;

    @FXML
    private void initialize() {
        fechaDatePicker.setValue(LocalDate.now());
    }

    /**
     * Inyector para la lógica de negocio.
     * Aquí poblamos los ComboBox.
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        poblarComboBoxPrestamo();

    }

    private void poblarComboBoxPrestamo(){
        if (biblioteca.getSocios() != null) {
            socioComboBox.setItems(FXCollections.observableArrayList(biblioteca.sociosHabilitados()));
        }
            libroComboBox.setItems(FXCollections.observableArrayList(biblioteca.librosDispobibles()));
    }
    /**
     * Inyector para el Stage (ventana)
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Se llama al hacer clic en el botón "Crear"
     */
    @FXML
    private void handleCrearPrestamo() {

        Socio socioSeleccionado = socioComboBox.getSelectionModel().getSelectedItem();
        Libro libroSeleccionado = libroComboBox.getSelectionModel().getSelectedItem();
        LocalDate fechaSeleccionada = fechaDatePicker.getValue();

        if (socioSeleccionado == null || libroSeleccionado == null) {
            mostrarAlerta("Error de Validación", "Debe seleccionar un socio y un libro.", Alert.AlertType.INFORMATION);
            return;
        }

        GregorianCalendar fechaCalendar = GregorianCalendar.from(
                fechaSeleccionada.atStartOfDay(ZoneId.systemDefault())
        );

        boolean exito = biblioteca.prestarLibro(
                fechaCalendar,
                socioSeleccionado,
                libroSeleccionado
        );

        if (exito) {
            mostrarAlerta("Éxito", "Préstamo registrado correctamente.", Alert.AlertType.INFORMATION);
            stage.close();
        } else {
            mostrarAlerta("Error al Prestar", "No se pudo registrar el préstamo. Verifique que el libro esté disponible y que el socio esté habilitado.", Alert.AlertType.INFORMATION);
        }
    }

}
