package com.poo.gestorbiblioteca.ui.controller.libros;

import com.poo.exception.LibroNoPrestadoException;
import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Libro;
import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
     * Configura los pipes para la TABLA DE LIBROS.
     */
    @FXML
    private void initialize() {
        this.configurarColumnas();
        this.configurarFiltroBusqueda();
        this.tablaLibros.setOnMouseClicked( event -> this.onTablaLibrosDobleClic(event));
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaLibros();
    }

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
                } catch (LibroNoPrestadoException e) { /* Ignorar */ }
            }
            return new SimpleStringProperty(nombre);
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<Libro> listaFiltrada = new FilteredList<>(listaMaestraLibros, p -> true);

        campoBusquedaLibros.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(libro -> {

                // Si el campo de búsqueda está vacío, muestra todo.
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

        this.tablaLibros.setItems(listaFiltrada);
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
     * Recibe el objeto MouseEvent del del metodo setOnMouseClicked y valida:
     * si es doble click y si es click derecho selecciona el objeto Libro y lo pasa al metodo
     * handleDobleClicLibro.
     * @param event
     */
    private void onTablaLibrosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Libro libroSeleccionado = tablaLibros.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                this.handleDobleClicLibro(libroSeleccionado);
            }
        }
    }

    @FXML
    private void handleNuevoLibro() {
        try {
            // carga el FXML del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/libros/FormularioLibro.fxml"));
            Parent root = loader.load();

            //Obtiene el controlador del formulario
            FormularioLibroController formController = loader.getController();

            //Crea el nuevo Stage (la ventana popup)
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Nuevo Libro");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/libro-icon.png"));
            popupStage.getIcons().add(appIcon);

            Scene popupScene = new Scene(root);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );
            popupStage.setScene(popupScene);

            //Inyecta
            formController.setBiblioteca(this.biblioteca);
            formController.setStage(popupStage);

            // 5. Mostrar la ventana y esperar
            popupStage.showAndWait();

            // 6. Refrescar la tabla después de que el popup se cierre
            this.refrescarTablaLibros();

        } catch (IOException e) {
            e.printStackTrace();
            // (Mostrar alerta de error al usuario)
        }
    }

    private void handleDobleClicLibro(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/libros/DescripcionLibro.fxml"));
            Parent root = loader.load();

            DescripcionLibroController descController = loader.getController();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Detalles del Libro");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/libro-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            // ¡INYECTAR! Pasa la biblioteca, el stage y el libro seleccionado al controlador del popup
            descController.setBiblioteca(this.biblioteca);
            descController.setStage(popupStage);
            descController.setLibro(libro); // <-- ¡Importante! Pasa el libro aquí

            popupStage.showAndWait();
            this.refrescarTablaLibros(); // Refrescar la tabla al cerrar el popup (por si se eliminó)

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la descripción del libro.", Alert.AlertType.ERROR);
        }
    }

}

