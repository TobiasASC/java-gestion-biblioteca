package com.poo.gestorbiblioteca.ui.controller.socios;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Docente;
import com.poo.gestorbiblioteca.model.Estudiante;
import com.poo.gestorbiblioteca.model.Socio;
import com.poo.gestorbiblioteca.utils.Alerta;
import com.poo.gestorbiblioteca.utils.StringNormalizador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static com.poo.gestorbiblioteca.utils.Alerta.mostrarAlerta;

public class SociosController {

    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, Integer> colSocioDNI;
    @FXML private TableColumn<Socio, String> colSocioNombre;
    @FXML private TableColumn<Socio, String> colSocioTipo;
    @FXML private TableColumn<Socio, String> colSocioAreaCarrera;
    @FXML private TextField campoBusquedaSocios;
    @FXML private Button btnNuevoSocio;

    private Biblioteca biblioteca;
    private ObservableList<Socio> listaMaestraSocios = FXCollections.observableArrayList();

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
        tablaSocios.setOnMouseClicked(event -> onTablaSociosDobleClic(event));
    }

    /**
     * Inyecta la lógica de negocio
     * Refresca la lista
     */
    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaSocios();
    }

    /**
     * Mapea los valores de las columnas con los atributos de la clase Socio,
     * mediante un control virtualizado
     */
    private void configurarColumnas() {
        colSocioDNI.setCellValueFactory(new PropertyValueFactory<>("dniSocio"));
        colSocioNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSocioTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().soyDeLaClase();
            return new SimpleStringProperty(tipo);
        });
        colSocioAreaCarrera.setCellValueFactory(cellData -> {

            Socio socio = cellData.getValue();
            String valor = "-";

            // Comprueba si el Socio es un Docente
            if (socio instanceof Docente) {
                valor = ((Docente) socio).getArea();
            }
            // Comprueba si el Socio es un Estudiante
            else if (socio instanceof Estudiante) {
                valor = ((Estudiante) socio).getCarrera();
            }
            return new SimpleStringProperty(valor);
        });
    }

    /**
     * Configura la busqueda, con una logica de filtrado y ordenamiento reactivo
     */
    private void configurarFiltroBusqueda() {
        // Capa de filtrado
        FilteredList<Socio> listaFiltrada = new FilteredList<>(listaMaestraSocios, p -> true);
        campoBusquedaSocios.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(socio -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String filtroNormalizado = StringNormalizador.normalizarTexto(newValue);

                if (StringNormalizador.normalizarTexto(socio.getNombre())
                        .contains(filtroNormalizado)) {
                    return true;
                }

                else if (String.valueOf(socio.getDniSocio()).contains(filtroNormalizado)) {
                    return true;
                }

                else if (StringNormalizador.normalizarTexto(socio.soyDeLaClase())
                        .contains(filtroNormalizado)) {
                    return true;
                }

                else if (socio instanceof Docente) {
                    if (StringNormalizador.normalizarTexto(((Docente) socio).getArea())
                            .contains(filtroNormalizado)) {
                        return true;
                    }
                } else if (socio instanceof Estudiante) {
                    if (StringNormalizador.normalizarTexto(((Estudiante) socio).getCarrera())
                            .contains(filtroNormalizado)) {
                        return true;
                    }
                }
                return false;
            });
        });

        // Capa de ordenamiento
        SortedList<Socio> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaSocios.comparatorProperty());

        this.tablaSocios.setItems(listaOrdenada);
    }

    /**
     * Refresca la tabla de libros
     */
    public void refrescarTablaSocios(){

        if (this.biblioteca == null || this.biblioteca.getSocios() == null) {
            this.listaMaestraSocios.clear(); // Limpia la tabla si no hay nada
            return;
        }

        ArrayList<Socio> sociosActualizados = this.biblioteca.getSocios();

        this.listaMaestraSocios.clear();
        this.listaMaestraSocios.addAll(sociosActualizados);
    }

    /**
     * Recibe el objeto MouseEvent del metodo setOnMouseClicked y valida:
     * si es doble click y si es click izquierdo selecciona el objeto Socio y lo pasa al metodo
     * handleDobleClicLibro.
     */
    private void onTablaSociosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Socio socioSeleccionado = tablaSocios.getSelectionModel().getSelectedItem();
            if (socioSeleccionado != null) {
                handleDobleClicSocio(socioSeleccionado);
            }
        }
    }

    /**
     * Maneja la accion del doble click en la tabla
     */
    private void handleDobleClicSocio(Socio socio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/socios/DescripcionSocio.fxml"));
            Parent root = loader.load();

            DescripcionSocioController descController = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Detalles del Socio");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/socio-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            descController.setBiblioteca(this.biblioteca);
            descController.setStage(popupStage);
            descController.setSocio(socio);

            popupStage.showAndWait();

            this.refrescarTablaSocios();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista de detalles.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja la accion del boton "Nuevo Libro"
     */
    @FXML
    private void handleNuevoSocio() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/socios/FormularioSocio.fxml"));
            Parent root = loader.load();

            FormularioSocioController formController = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            popupStage.setTitle("Nuevo Socio");
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/socio-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm()
            );

            formController.setBiblioteca(this.biblioteca);
            formController.setStage(popupStage);

            popupStage.showAndWait();

            refrescarTablaSocios();

        } catch (IOException e) {
            e.printStackTrace();
        }

}
}
