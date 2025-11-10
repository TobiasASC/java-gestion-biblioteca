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
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, String> colPrestamoLibro;
    @FXML private TableColumn<Prestamo, String> colPrestamoSocio;
    @FXML private TableColumn<Prestamo, String> colPrestamoFechaRetiro;
    @FXML private TableColumn<Prestamo, String> colPrestamoFechaDev;
    @FXML private TableColumn<Prestamo, String> colEstado;
    @FXML private TableColumn<Prestamo, String> colPrestamoDNI;
    @FXML private TextField campoBusquedaPrestamos;

    private Biblioteca biblioteca;
    private ObservableList<Prestamo> listaMaestraPrestamos = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Inicializa:
     *  - El data binding de las columnas
     *  - La configuración del filtro de búsqueda
     * Asigna:
     *  - El metodo observador a la tabla de prestamos
     */
    @FXML
    private void initialize() {
        this.configurarColumnas();
        this.configurarFiltroBusqueda();
        tablaPrestamos.setOnMouseClicked(event -> onTablaPrestamosDobleClic(event));
    }

    /**
     * Inyecta la lógica de negocio
     * Refresca la lista
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaPrestamos();
    }

    /**
     * Mapea los valores de las columnas con los atributos de la clase Libro,
     * mediante un control virtualizado
     */
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

    /**
     * Configura la busqueda, con una logica de filtrado y ordenamiento reactivo
     */
    private void configurarFiltroBusqueda() {
        // Capa de filtrado
        FilteredList<Prestamo> listaFiltrada = new FilteredList<>(listaMaestraPrestamos, p -> true);
        campoBusquedaPrestamos.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(prestamo -> {

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

        // Capa de ordenamiento
        SortedList<Prestamo> listaOrdenada = new SortedList<>(listaFiltrada);

        listaOrdenada.comparatorProperty().bind(tablaPrestamos.comparatorProperty());

        this.tablaPrestamos.setItems(listaOrdenada);
    }

    /**
     * Refresca la tabla de prestamos
     */
    public void refrescarTablaPrestamos() {
        if (this.biblioteca == null || this.biblioteca.getSocios() == null) {
            this.listaMaestraPrestamos.clear();
            return;
        }

        ArrayList<Prestamo> todosLosPrestamos = new ArrayList<>();
        for (Socio socio : this.biblioteca.getSocios()) {
            if (socio.getPrestamos() != null) {
                todosLosPrestamos.addAll(socio.getPrestamos());
            }
        }

        this.listaMaestraPrestamos.clear();
        this.listaMaestraPrestamos.addAll(todosLosPrestamos);
    }

    /**
     * Recibe el objeto MouseEvent del metodo setOnMouseClicked y valida:
     * si es doble click y si es click izquierdo selecciona el objeto Prestamo y lo pasa al metodo
     * handleDobleClicPrestamo.
     */
    private void onTablaPrestamosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Prestamo prestamoSeleccionado = tablaPrestamos.getSelectionModel().getSelectedItem();
            if (prestamoSeleccionado != null) {
                handleDobleClicPrestamo(prestamoSeleccionado);
            }
        }
    }

    /**
     * Abre el popup 'DescripcionPrestamo.fxml' y le pasa el préstamo seleccionado.
     */
    private void handleDobleClicPrestamo(Prestamo prestamo) {
        try {
            //Se carga el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/DescripcionPrestamo.fxml"));
            Parent root = loader.load();

            //Se obtiene el controlador
            DescripcionPrestamoController descController = loader.getController();

            //Se crea un nuevo stage en modo popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Expediente del Préstamo");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/prestamo-icon.png"));
            popupStage.getIcons().add(appIcon);

            //Se crea un nuevo scene y lo asigna al stage
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            //Inyecta la biblioteca, el stage y el libro al controlador
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

    /**
     * Maneja la accion del boton "Nuevo Prestamo"
     */
    @FXML
    private void handleNuevoPrestamo() {
        try {
            //Se carga el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/prestamos/FormularioPrestamo.fxml"));
            Parent root = loader.load();

            //Se obtiene el controlador
            FormularioPrestamoController formController = loader.getController();

            //Se crea un nuevo stage en modo popup
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

            // Se inyecta la biblioteca y el stage al controlador del popup
            formController.setBiblioteca(this.biblioteca);
            formController.setStage(popupStage);

            // Muestra la ventana y espera
            popupStage.showAndWait();

            this.refrescarTablaPrestamos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
