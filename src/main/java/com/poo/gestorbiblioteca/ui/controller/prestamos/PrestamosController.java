package com.poo.gestorbiblioteca.ui.controller.prestamos;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.model.Prestamo;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PrestamosController extends Controller {

    @FXML
    private TableView<Prestamo> tablaPrestamos;
    @FXML
    private TableColumn<Prestamo, String> colPrestamoLibro;
    @FXML
    private TableColumn<Prestamo, String> colPrestamoSocio;
    @FXML
    private TableColumn<Prestamo, String> colPrestamoFechaRetiro;
    @FXML
    private TableColumn<Prestamo, String> colPrestamoFechaDev;
    @FXML
    private TableColumn<Prestamo, String> colEstado;
    @FXML
    private TableColumn<Prestamo, String> colPrestamoDNI;

    @FXML
    private TextField campoBusquedaPrestamos;


    private Biblioteca biblioteca;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ObservableList<Prestamo> listaMaestraPrestamos = FXCollections.observableArrayList();

    /**
     * Configura las "tuberías" (Cell Value Factories) para la TABLA DE PRÉSTAMOS.
     * Se llama automáticamente cuando se carga el FXML.
     */
    @FXML
    private void initialize() {
        this.configurarColumnas();
        this.configurarFiltroBusqueda();
        tablaPrestamos.setOnMouseClicked(event -> onTablaPrestamosDobleClic(event));
    }

    private void configurarFiltroBusqueda() {
        FilteredList<Prestamo> listaFiltrada = new FilteredList<>(listaMaestraPrestamos, p -> true);

        campoBusquedaPrestamos.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(prestamo -> { // 'prestamo' es el objeto a probar

                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String filtroMinusculas = newValue.toLowerCase();

                if (prestamo.getLibro() != null &&
                        prestamo.getLibro().getTitulo().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                }

                else if (prestamo.getSocio() != null &&
                        prestamo.getSocio().getNombre().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                }

                else if (prestamo.estado().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                }

                else if (prestamo.getFechaRetiro() != null &&
                        dateFormat.format(prestamo.getFechaRetiro().getTime()).contains(filtroMinusculas)) {
                    return true;
                }

                else if (prestamo.getSocio() != null &&
                        String.valueOf(prestamo.getSocio().getDniSocio()).contains(filtroMinusculas)){
                    return true;
                }

                return false;
            });
        });

        tablaPrestamos.setItems(listaFiltrada);
    }

    private void configurarColumnas() {
        colPrestamoLibro.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue().getLibro();
            String titulo = (libro != null) ? libro.getTitulo() : "Libro no encontrado";
            return new SimpleStringProperty(titulo);
        });

        colPrestamoSocio.setCellValueFactory(cellData -> {
            Socio socio = cellData.getValue().getSocio();
            String nombre = (socio != null) ? socio.getNombre() : "Socio no encontrado";
            return new SimpleStringProperty(nombre);
        });

        colPrestamoDNI.setCellValueFactory(cellData -> {
            int dni = cellData.getValue().getSocio().getDniSocio();
            return new SimpleStringProperty(Integer.toString(dni));
        });

        colPrestamoFechaRetiro.setCellValueFactory(cellData -> {
            Calendar fecha = cellData.getValue().getFechaRetiro();
            String fechaFormateada = (fecha != null) ? dateFormat.format(fecha.getTime()) : "-";
            return new SimpleStringProperty(fechaFormateada);
        });

        colPrestamoFechaDev.setCellValueFactory(cellData -> {
            Calendar fecha = cellData.getValue().getFechaDevolucion();
            String fechaFormateada = (fecha != null) ? dateFormat.format(fecha.getTime()) : "-";
            return new SimpleStringProperty(fechaFormateada);
        });

        colEstado.setCellValueFactory( cellData -> {
            String valor = cellData.getValue().estado();
            return new SimpleStringProperty(valor);
        });
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaPrestamos();

    }

    public void refrescarTablaPrestamos() {
        if (this.biblioteca == null || this.biblioteca.getSocios() == null) {
            this.listaMaestraPrestamos.clear();
            return;
        }

        // 1. Obtiene la lista "plana" de todos los préstamos
        ArrayList<Prestamo> todosLosPrestamos = new ArrayList<>();
        for (Socio socio : this.biblioteca.getSocios()) {
            if (socio.getPrestamos() != null) {
                todosLosPrestamos.addAll(socio.getPrestamos());
            }
        }

        // 2. Vuelca los datos "frescos" a la lista maestra
        this.listaMaestraPrestamos.clear();
        this.listaMaestraPrestamos.addAll(todosLosPrestamos);
    }

    /**
     * Es llamado por el 'listener' de la tabla. Revisa si fue un doble clic.
     */
    private void onTablaPrestamosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
            if (prestamoSeleccionado != null) {
                handleAbrirDescripcionPrestamo(prestamoSeleccionado);
            }
        }
    }

    /**
     * Abre el popup 'DescripcionPrestamo.fxml' y le pasa el préstamo seleccionado.
     */
    private void handleAbrirDescripcionPrestamo(Prestamo prestamo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/DescripcionPrestamo.fxml"));
            Parent root = loader.load();

            DescripcionPrestamoController descController = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Expediente del Préstamo");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/prestamo-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            descController.setBiblioteca(this.biblioteca);
            descController.setStage(popupStage);
            descController.setPrestamo(prestamo);

            popupStage.showAndWait();

            this.refrescarTablaPrestamos();

        } catch (IOException e) {
            e.printStackTrace();
            super.mostrarAlerta("Error", "No se pudo cargar la vista de detalles.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleNuevoPrestamo() {
        try {
            // 1. Cargar el FXML del formulario popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/FormularioPrestamo.fxml"));
            Parent root = loader.load();

            // 2. Obtener el controlador del formulario
            FormularioPrestamoController formController = loader.getController();

            // 3. Crear el nuevo Stage (la ventana popup)
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            popupStage.setTitle("Registrar Nuevo Préstamo");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/prestamo-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            // 4. ¡INYECTAR! Pasa la biblioteca y el stage al controlador del popup
            formController.setBiblioteca(this.biblioteca);
            formController.setStage(popupStage);

            // 5. Mostrar la ventana Y ESPERAR a que se cierre
            popupStage.showAndWait();

            // 6. ¡REFRESCAR! Cuando el popup se cierra,
            // la ejecución continúa aquí. Refrescamos la tabla.
            this.refrescarTablaPrestamos();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }



}
