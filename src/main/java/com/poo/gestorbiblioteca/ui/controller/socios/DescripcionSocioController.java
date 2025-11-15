package com.poo.gestorbiblioteca.ui.controller.socios;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Docente;
import com.poo.gestorbiblioteca.model.Estudiante;
import com.poo.gestorbiblioteca.model.Prestamo;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.utils.Alerta;
import com.poo.gestorbiblioteca.ui.controller.prestamos.DescripcionPrestamoController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static com.poo.gestorbiblioteca.utils.Alerta.mostrarAlerta;

public class DescripcionSocioController {

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

    /**
     * Recibe la lógica de negocio.
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
    }

    /**
     * Recibe el Stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Recibe el socio a mostrar
     * Puebla los campos de la descripcion.
     */
    public void setSocio(Socio socio) {
        this.socioSeleccionado = socio;
        this.poblarCamposSocio(socio);
        this.cargarHistorial(socio);
    }

    /**
     * Puebla los campos de la descripcion con los datos del libro seleccionado.
     */
    private void poblarCamposSocio(Socio socio){
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

    /**
     * Define el data binding
     */
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

    /**
     * Carga el historal de prestamos del socio
     */
    private void cargarHistorial(Socio socio){
        if (socio.getPrestamos() != null) {
            tablaHistorial.setItems(FXCollections.observableArrayList(socio.getPrestamos()));
        }
    }

    /**
     * Registra el manejador de eventos
     */
    private void setListenerTablaHistorial(){
        tablaHistorial.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaHistorial.getSelectionModel().isEmpty()) {
                this.handleVerDescripcionPrestamo();
            }
        });
    }

    /**
     * Obtiene el Prestamo seleccionado y llama al metodo abrirVentanaDescripcion
     */
    private void handleVerDescripcionPrestamo(){
        Prestamo prestamoSeleccionado = tablaHistorial.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            return;
        }
        this.abrirVentanaDescripcion(prestamoSeleccionado);
    }

    /**
     * Orquesta la apertura del popup
     */
    private void abrirVentanaDescripcion(Prestamo prestamo) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/DescripcionPrestamo.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Expediente del Préstamo");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(this.stage);
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/prestamo-icon.png"));
            popupStage.getIcons().add(appIcon);

            DescripcionPrestamoController controller = loader.getController();

            controller.setStage(popupStage);
            controller.setBiblioteca(this.biblioteca);
            controller.setPrestamo(prestamo); // ¡Este es el paso clave!

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de descripción: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja el clic en el botón "Eliminar".
     */
    @FXML
    private void handleEliminarSocio() {
        //Confirmación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de que desea eliminar este socio?");
        confirmAlert.setContentText("Socio: " + socioSeleccionado.getNombre() + "\nEsta acción no se puede deshacer.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        // Elimina
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

    /**
     * Maneja el clic en el botón "Cerrar".
     */
    @FXML
    private void handleCerrar() {
        stage.close();
    }

}
