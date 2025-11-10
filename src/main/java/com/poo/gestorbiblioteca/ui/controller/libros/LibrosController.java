package com.poo.gestorbiblioteca.ui.controller.libros;

import com.poo.gestorbiblioteca.exception.LibroNoPrestadoException;
import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LibrosController extends Controller {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colLibroTitulo;
    @FXML private TableColumn<Libro, Integer> colLibroEdicion;
    @FXML private TableColumn<Libro, String> colLibroEditorial;
    @FXML private TableColumn<Libro, Integer> colLibroAnio;
    @FXML private TableColumn<Libro, String> colLibroPrestado;
    @FXML private TableColumn<Libro, String> colLibroPrestadoA;
    @FXML private TextField campoBusquedaLibros;

    private Biblioteca biblioteca;
    private ObservableList<Libro> listaMaestraLibros = FXCollections.observableArrayList();

    /**
     * Inicializa:
     *  - El data binding de las columnas
     *  - La configuración del filtro de búsqueda
     * Asigna:
     *  - El metodo observador a la tabla de libros.
     */
    @FXML
    private void initialize() {
        this.configurarColumnas();
        this.configurarFiltroBusqueda();
        this.tablaLibros.setOnMouseClicked( event -> this.onTablaLibrosDobleClic(event));
    }

    /**
     * Inyecta la lógica de negocio
     * Refresca la lista
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaLibros();
    }

    /**
     * Mapea los valores de las columnas con los atributos de la clase Libro,
     * mediante un control virtualizado
     */
    private void configurarColumnas(){
        colLibroTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colLibroEdicion.setCellValueFactory(new PropertyValueFactory<>("edicion"));
        colLibroEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colLibroAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colLibroPrestado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().prestado() ? "Sí" : "No")
        );
        colLibroPrestadoA.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue();
            String nombre = "-";
            if (this.biblioteca != null && libro.prestado()) {
                try {
                    nombre = this.biblioteca.quienTieneElLibro(libro);
                } catch (LibroNoPrestadoException e) {}
            }
            return new SimpleStringProperty(nombre);
        });
    }

    /**
     * Configura la busqueda, con una logica de filtrado y ordenamiento reactivo
     */
    private void configurarFiltroBusqueda(){

        // Capa de filtrado
        FilteredList<Libro> listaFiltrada = new FilteredList<>(listaMaestraLibros, p -> true);

        campoBusquedaLibros.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(libro -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String filtroMinusculas = newValue.toLowerCase();

                if (libro.getTitulo().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                } else if (libro.getEditorial().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                } else if (String.valueOf(libro.getAnio()).contains(filtroMinusculas)) {
                    return true;
                }

                return false;
            });
        });

        // Capa de ordenamiento
        SortedList<Libro> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaLibros.comparatorProperty());

        this.tablaLibros.setItems(listaOrdenada);
    }

    /**
     * Refresca la tabla de libros
     */
    private void refrescarTablaLibros() {
        if (this.biblioteca.getLibros() != null) {
            this.listaMaestraLibros.clear();
            this.listaMaestraLibros.addAll(this.biblioteca.getLibros());
        }
    }

    /**
     * Recibe el objeto MouseEvent del metodo setOnMouseClicked y valida:
     * si es doble click y si es click izquierdo selecciona el objeto Libro y lo pasa al metodo
     * handleDobleClicLibro.
     */
    private void onTablaLibrosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                this.handleDobleClicLibro(libroSeleccionado);
            }
        }
    }

    /**
     * Maneja la accion del boton "Nuevo Libro"
     */
    @FXML
    private void handleNuevoLibro() {
        try {
            //Se carga el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/libros/FormularioLibro.fxml"));
            Parent root = loader.load();

            //Se obtiene el controlador
            FormularioLibroController formController = loader.getController();

            //Se crea un nuevo stage en modo popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Nuevo Libro");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/libro-icon.png"));
            popupStage.getIcons().add(appIcon);

            //Crea un scene y lo asigna al stage
            Scene popupScene = new Scene(root);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm());
            popupStage.setScene(popupScene);

            //Inyecta la biblioteca y el stage al controlador
            formController.setBiblioteca(this.biblioteca);
            formController.setStage(popupStage);

            popupStage.showAndWait();

            this.refrescarTablaLibros();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Maneja la accion del doble click en la tabla
     */
    private void handleDobleClicLibro(Libro libro) {
        try {
            //Se carga el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/libros/DescripcionLibro.fxml"));
            Parent root = loader.load();

            //Se obtiene el controlador
            DescripcionLibroController descController = loader.getController();

            //Se crea un nuevo stage en modo popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Detalles del Libro");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/libro-icon.png"));
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
            descController.setLibroSeleccionado(libro);

            popupStage.showAndWait();
            this.refrescarTablaLibros();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la descripción del libro.", Alert.AlertType.ERROR);
        }
    }
}

