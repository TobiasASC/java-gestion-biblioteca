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
import java.util.GregorianCalendar;


public class FormularioPrestamoController extends Controller {

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
     * Inyecta la lógica de negocio.
     * Llama al metodo poblador de combobox
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        poblarComboBoxPrestamo();
    }

    /**
     * Puebla los combo box
     */
    private void poblarComboBoxPrestamo(){
        if (biblioteca.getSocios() != null) {
            socioComboBox.setItems(FXCollections.observableArrayList(biblioteca.sociosHabilitados()));
        }
            libroComboBox.setItems(FXCollections.observableArrayList(biblioteca.librosDispobibles()));
    }
    /**
     * Inyecta el stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Manejador del botón "Crear"
     */
    @FXML
    private void handleCrearPrestamo() {
        // Obtiene los datos de los campos
        Socio socioSeleccionado = socioComboBox.getSelectionModel().getSelectedItem();
        Libro libroSeleccionado = libroComboBox.getSelectionModel().getSelectedItem();
        LocalDate fechaSeleccionada = fechaDatePicker.getValue();

        // Validaciones
        if (socioSeleccionado == null || libroSeleccionado == null) {
            mostrarAlerta("Error de Validación", "Debe seleccionar un socio y un libro.", Alert.AlertType.INFORMATION);
            return;
        }

        // Convierte fechaSeleccionada a tipo Calendar
        GregorianCalendar fechaCalendar = GregorianCalendar.from(
                fechaSeleccionada.atStartOfDay(ZoneId.systemDefault())
        );

        // Realiza el prestamo
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
