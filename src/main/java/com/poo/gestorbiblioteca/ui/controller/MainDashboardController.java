package com.poo.gestorbiblioteca.ui.controller;
import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.ui.controller.ajustes.AjustesController;
import com.poo.gestorbiblioteca.ui.controller.libros.LibrosController;
import com.poo.gestorbiblioteca.ui.controller.prestamos.PrestamosController;
import com.poo.gestorbiblioteca.ui.controller.socios.SociosController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class MainDashboardController {

    private Biblioteca biblioteca;

    @FXML
    private LibrosController librosViewController;
    @FXML
    private SociosController sociosViewController;
    @FXML
    private PrestamosController prestamosViewController;
    @FXML
    private Tab tabPrestamos;
    @FXML
    private Stage mainStage;

    @FXML
    private void initialize() {
        this.refrescarPrestamos();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        if (librosViewController != null) {
            librosViewController.setBiblioteca(biblioteca);
        }
        if (sociosViewController != null) {
            sociosViewController.setBiblioteca(biblioteca);
        }
        if (prestamosViewController != null) {
            prestamosViewController.setBiblioteca(biblioteca);
        }

    }

    private void refrescarPrestamos(){
        if (tabPrestamos != null) {
            tabPrestamos.setOnSelectionChanged(event -> {
                if (tabPrestamos.isSelected() && prestamosViewController != null) {
                    System.out.println("Pestaña Préstamos seleccionada. Refrescando tabla...");
                    prestamosViewController.refrescarTablaPrestamos();
                }
            });
        }
    }


    @FXML
    private void handleReporteSocios() {
        String titulo = "Reporte de Socios";
        // La lógica de generar el string está en el backend
        String contenido = biblioteca.listaDeSocios();
        abrirVentanaReporte(titulo, contenido);
    }

    @FXML
    private void handleReporteLibros() {
        String titulo = "Reporte de Libros";
        String contenido = biblioteca.listaDeLibros();
        abrirVentanaReporte(titulo, contenido);
    }

    @FXML
    private void handleReporteDocentes() {
        String titulo = "Reporte de Docentes Responsables";
        String contenido = biblioteca.listaDeDocentesResponsables();
        abrirVentanaReporte(titulo, contenido);
    }


    @FXML
    private void handleAjustesNombre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/ajustes/AjustesView.fxml"));
            Parent root = loader.load();

            AjustesController controller = loader.getController();

            Stage popupStage = new Stage();
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/biblioteca-icon.png"));
            popupStage.getIcons().add(appIcon);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Asignar Nombre a la Biblioteca");
            Scene popupScene = new Scene(root);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm());
            popupStage.setScene(popupScene);

            // Inyectamos la biblioteca y el stage
            controller.setBiblioteca(this.biblioteca);
            controller.setStage(popupStage);

            popupStage.showAndWait();

            if (this.mainStage != null) {
                this.mainStage.setTitle("Gestor de Biblioteca: " + biblioteca.getNombre());
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana de ajustes.");
        }
    }

    @FXML
    private void handleSalir() {
        Platform.exit();
    }

    @FXML
    private void handleAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Gestor de Biblioteca v1.0");
        alert.setContentText("""
            Proyecto final de Programación Orientada a Objetos.
            Desarrollado por:
            - Fernández, Pablo
            - Kruzolek, Lucas
            - Marquez, Marcos Abel
            - Rojas, Marcos Agustín
            - Sanchez Cueba, Tobías
            - Santoro Sandoval, Lionel
            """
                );
        alert.showAndWait();
    }

    /**
     * Método helper reutilizable para abrir cualquier popup de reporte.
     */
    private void abrirVentanaReporte(String titulo, String contenido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/reportes/ReporteView.fxml"));
            Parent root = loader.load();

            com.poo.gestorbiblioteca.ui.controller.reporte.ReporteController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(titulo);
            Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/reporte-icon.png"));
            popupStage.getIcons().add(appIcon);
            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupScene.getStylesheets().add(
                    getClass().getResource("/com/poo/gestorbiblioteca/ui/style.css").toExternalForm());
            // Inyectamos los datos y el stage
            controller.setStage(popupStage);
            controller.setDatos(titulo, contenido); // Método que crearemos en ReporteController

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el reporte.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}