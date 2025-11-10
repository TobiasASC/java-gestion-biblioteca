package com.poo.gestorbiblioteca.ui.controller.socios;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Docente;
import com.poo.gestorbiblioteca.model.Estudiante;
import com.poo.gestorbiblioteca.model.Prestamo;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import com.poo.gestorbiblioteca.ui.controller.prestamos.DescripcionPrestamoController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DescripcionSocioController extends Controller {

    @FXML private Label dniLabel;
    @FXML private Label nombreLabel;
    @FXML private Label tipoLabel;
    @FXML private Label labelDinamico;
    @FXML private Label areaCarreraLabel;

    @FXML private TableView<Prestamo> tablaHistorial;
    @FXML private TableColumn<Prestamo, String> colHistorialLibro;
    @FXML private TableColumn<Prestamo, String> colHistorialEstado;

    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    private Biblioteca biblioteca;
    private Stage stage;
    private Socio socioSeleccionado;

    /**
     * Se llama automáticamente al cargar el FXML.
     * Configura la mini-tabla de historial de préstamos.
     */
    @FXML
    private void initialize() {

        this.configurarColumnas();
        this.setListenerTablaHistorial();
    }

    private void configurarColumnas() {
        colHistorialLibro.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new SimpleStringProperty(cellData.getValue().getLibro().getTitulo());
            }
            return new SimpleStringProperty("Libro no disponible");
        });

        colHistorialEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().estado())
        );
    }

    private void setListenerTablaHistorial(){
        tablaHistorial.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaHistorial.getSelectionModel().isEmpty()) {
                this.handleVerDescripcionPrestamo();
            }
        });
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Recibe el Socio seleccionado y puebla todos los campos de la vista de detalles.
     */
    public void setSocio(Socio socio) {
        this.socioSeleccionado = socio;
        this.cargarDescripcion(socio);
        this.cargarHistorial(socio);
    }

    private void cargarDescripcion(Socio socio){
        dniLabel.setText(String.valueOf(socio.getDniSocio()));
        nombreLabel.setText(socio.getNombre());
        tipoLabel.setText(socio.soyDeLaClase());

        if (socio instanceof Docente) {
            labelDinamico.setText("Área:");
            areaCarreraLabel.setText(((Docente) socio).getArea());
        } else if (socio instanceof Estudiante) {
            labelDinamico.setText("Carrera:");
            areaCarreraLabel.setText(((Estudiante) socio).getCarrera());
        } else {
            labelDinamico.setVisible(false);
            areaCarreraLabel.setVisible(false);
        }
    }
    private void cargarHistorial(Socio socio){
        if (socio.getPrestamos() != null) {
            tablaHistorial.setItems(FXCollections.observableArrayList(socio.getPrestamos()));
        }
    }
    /**
     * Maneja el clic en el botón "Eliminar".
     */
    @FXML
    private void handleEliminarSocio() {
        // 1. Pide confirmación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de que desea eliminar este socio?");
        confirmAlert.setContentText("Socio: " + socioSeleccionado.getNombre() + "\nEsta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                biblioteca.eliminarSocio(socioSeleccionado);
                mostrarAlerta("Éxito", "Socio eliminado correctamente.", Alert.AlertType.INFORMATION);
                stage.close();
            } catch (Exception e) {
                mostrarAlerta("Error al Eliminar", "No se pudo eliminar el socio: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }

    private void handleVerDescripcionPrestamo(){
        Prestamo prestamoSeleccionado = tablaHistorial.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            return;
        }
        this.abrirVentanaDescripcion(prestamoSeleccionado);
    }

    private void abrirVentanaDescripcion(Prestamo prestamo) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/DescripcionPrestamo.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Descripción del Préstamo");
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(this.stage);

            DescripcionPrestamoController controller = loader.getController();

            controller.setStage(newStage);
            controller.setBiblioteca(this.biblioteca);
            controller.setPrestamo(prestamo); // ¡Este es el paso clave!

            newStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de descripción: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}
