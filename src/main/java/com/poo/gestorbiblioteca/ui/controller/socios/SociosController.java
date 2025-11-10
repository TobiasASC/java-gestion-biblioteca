package com.poo.gestorbiblioteca.ui.controller.socios;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.model.Docente;
import com.poo.gestorbiblioteca.model.Estudiante;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class SociosController extends Controller {

    @FXML
    private TableView<Socio> tablaSocios;
    @FXML
    private TableColumn<Socio, Integer> colSocioDNI;
    @FXML
    private TableColumn<Socio, String> colSocioNombre;
    @FXML
    private TableColumn<Socio, String> colSocioTipo;
    @FXML
    private TableColumn<Socio, String> colSocioAreaCarrera;
    @FXML
    private TextField campoBusquedaSocios;

    @FXML
    private Button btnNuevoSocio;

    private Biblioteca biblioteca;
    private ObservableList<Socio> listaMaestraSocios = FXCollections.observableArrayList();

    /**
     * Configura las "tuberías" (Cell Value Factories) para la TABLA DE SOCIOS.
     * Se llama automáticamente cuando se carga el FXML.
     */
    @FXML
    private void initialize() {
        this.configurarColumnas();
        this.configurarFiltroBusqueda();
        tablaSocios.setOnMouseClicked(event -> onTablaSociosDobleClic(event));
    }

    private void configurarFiltroBusqueda() {
        FilteredList<Socio> listaFiltrada = new FilteredList<>(listaMaestraSocios, p -> true);

        campoBusquedaSocios.textProperty().addListener((observable, oldValue, newValue) -> {

            listaFiltrada.setPredicate(socio -> {

                // Si el campo de búsqueda está vacío, muestra todo.
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }

                String filtroMinusculas = newValue.toLowerCase();

                if (socio.getNombre().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                }

                else if (String.valueOf(socio.getDniSocio()).contains(filtroMinusculas)) {
                    return true;
                }

                else if (socio.soyDeLaClase().toLowerCase().contains(filtroMinusculas)) {
                    return true;
                }

                else if (socio instanceof Docente) {
                    if (((Docente) socio).getArea().toLowerCase().contains(filtroMinusculas)) {
                        return true;
                    }
                } else if (socio instanceof Estudiante) {
                    if (((Estudiante) socio).getCarrera().toLowerCase().contains(filtroMinusculas)) {
                        return true;
                    }
                }

                return false;
            });
        });
        tablaSocios.setItems(listaFiltrada);
    }

    private void configurarColumnas() {
        // Asocia la columna "DNI" con el metodo getDniSocio() de la clase Socio
        colSocioDNI.setCellValueFactory(new PropertyValueFactory<>("dniSocio"));

        colSocioNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colSocioTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().soyDeLaClase();
            return new SimpleStringProperty(tipo);
        });

        colSocioAreaCarrera.setCellValueFactory(cellData -> {

            Socio socio = cellData.getValue();
            String valor = "-";

            // Comprueba si el Socio es en realidad un Docente
            if (socio instanceof Docente) {
                valor = ((Docente) socio).getArea();
            }
            // Comprueba si el Socio es en realidad un Estudiante
            else if (socio instanceof Estudiante) {
                valor = ((Estudiante) socio).getCarrera();
            }

            return new SimpleStringProperty(valor);
        });
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.refrescarTablaSocios();
    }

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
     * Es llamado por el 'listener' de la tabla. Revisa si fue un doble clic.
     */
    private void onTablaSociosDobleClic(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            Socio socioSeleccionado = tablaSocios.getSelectionModel().getSelectedItem();
            if (socioSeleccionado != null) {
                handleAbrirDescripcionSocio(socioSeleccionado);
            }
        }
    }

    /**
     * Abre el popup 'DescripcionSocio.fxml' y le pasa el socio seleccionado.
     */
    private void handleAbrirDescripcionSocio(Socio socio) {
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
     * Configura y lanza un mensaje pop-up de alerta.
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
