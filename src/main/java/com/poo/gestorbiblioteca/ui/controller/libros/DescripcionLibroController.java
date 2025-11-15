package com.poo.gestorbiblioteca.ui.controller.libros;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
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
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static com.poo.gestorbiblioteca.utils.Alerta.mostrarAlerta;

public class DescripcionLibroController {

    @FXML private Label tituloLabel;
    @FXML private Label edicionLabel;
    @FXML private Label editorialLabel;
    @FXML private Label anioLabel;
    @FXML private Label estadoLabel;

    @FXML private TableView<Prestamo> tablaHistorial;
    @FXML private TableColumn<Prestamo, String> colHistorialSocio;
    @FXML private TableColumn<Prestamo, String> colHistorialEstado;

    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    private Biblioteca biblioteca;
    private Stage stage;
    private Libro libroSeleccionado;

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
     * Recibe el libro a mostrar
     * Puebla los campos de la descripcion y del historial.
     */
    public void setLibroSeleccionado(Libro libro) {
        this.libroSeleccionado = libro;
        this.poblarCamposLibro(libro);
        this.cargarHistorial(libro);
    }

    /**
     * Puebla los campos de la descripcion con los datos del libro seleccionado.
     */
    public void poblarCamposLibro(Libro libro){
        if (libro != null) {
            tituloLabel.setText(libro.getTitulo());
            edicionLabel.setText(String.valueOf(libro.getEdicion()));
            editorialLabel.setText(libro.getEditorial());
            anioLabel.setText(String.valueOf(libro.getAnio()));
            if (this.libroSeleccionado.prestado()) {
                estadoLabel.setText("Prestado");
                estadoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #dc3545;");
            } else {
                estadoLabel.setText("Disponible");
                estadoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");
            }
        } else {
            tituloLabel.setText("N/A");
            edicionLabel.setText("N/A");
            editorialLabel.setText("N/A");
            anioLabel.setText("N/A");
            estadoLabel.setText("N/A");
            estadoLabel.setStyle("-fx-font-weight: normal; -fx-text-fill: black;");
        }
    }

    /**
     * Define el data binding para la tabla de historial.
     */
    private void configurarColumnas() {
        colHistorialSocio.setCellValueFactory(cellData -> {
            Socio socio = cellData.getValue().getSocio();
            if (socio != null) {
                return new SimpleStringProperty(socio.getNombre());
            }
            return new SimpleStringProperty("Socio no disponible");
        });

        colHistorialEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().estado())
        );
    }

    /**
     * Carga el historial de préstamos del libro
     */
    private void cargarHistorial(Libro libro) {
        if (libro != null && libro.getPrestamos() != null) {
            tablaHistorial.setItems(FXCollections.observableArrayList(libro.getPrestamos()));
        }
    }

    /**
     * Registra el manejador de eventos de doble clic en la tabla de historial.
     */
    private void setListenerTablaHistorial() {
        tablaHistorial.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) &&
                    event.getClickCount() == 2 &&
                    !tablaHistorial.getSelectionModel().isEmpty()) {

                this.handleVerDescripcionPrestamo();
            }
        });
    }

    /**
     * Obtiene el Prestamo seleccionado de la tabla de historial
     * y llama al metodo abrirVentanaDescripcion.
     */
    private void handleVerDescripcionPrestamo() {
        Prestamo prestamoSeleccionado = tablaHistorial.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            return;
        }
        this.abrirVentanaDescripcion(prestamoSeleccionado);
    }

    /**
     * Orquesta la apertura del popup de Descripción de Préstamo (tercer nivel).
     */
    private void abrirVentanaDescripcion(Prestamo prestamo) {
        try {
            // Carga el FXML del popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/DescripcionPrestamo.fxml"));
            Parent root = loader.load();

            // Crea el nuevo Stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Expediente del Préstamo");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(this.stage);
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/prestamo-icon.png"));
            popupStage.getIcons().add(appIcon);

            // Crear y asignar una Scene al Stage
            Scene popupScene = new Scene(root);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            popupStage.setScene(popupScene);

            // Obtener el controlador del popup
            DescripcionPrestamoController controller = loader.getController();

            // Inyectar Biblioteca, Stage y Préstamo
            controller.setStage(popupStage);
            controller.setBiblioteca(this.biblioteca);
            controller.setPrestamo(prestamo);

            popupStage.showAndWait();

            this.cargarHistorial(this.libroSeleccionado);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de descripción: " + e.getMessage(), Alert.AlertType.ERROR);
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
