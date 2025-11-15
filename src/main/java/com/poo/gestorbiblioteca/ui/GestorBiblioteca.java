package com.poo.gestorbiblioteca.ui;

import com.poo.gestorbiblioteca.core.Biblioteca;
import com.poo.gestorbiblioteca.ui.controller.MainDashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX (MainApp).
 * Su trabajo es:
 * 1. Cargar el backend (Biblioteca) en init().
 * 2. Construir la UI (Stage/Scene) en start().
 * 3. Inyectar el backend en el controlador.
 * 4. Guardar el backend en stop().
 */
public class GestorBiblioteca extends Application {

    private Biblioteca biblioteca;

    @Override
    public void init() throws Exception {
        System.out.println("Iniciando aplicación y cargando datos...");
        this.biblioteca = new Biblioteca("Biblioteca Popular");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/poo/gestorbiblioteca/ui/MainDashboard.fxml"));

        Parent root = loader.load();

        MainDashboardController mainController = loader.getController();

        mainController.setBiblioteca(this.biblioteca);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Gestor de Biblioteca");
        primaryStage.setScene(scene);
        Image appIcon = new Image(getClass().getResourceAsStream("/com/poo/gestorbiblioteca/ui/images/biblioteca-icon.png"));
        primaryStage.getIcons().add(appIcon);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Cerrando aplicación y guardando datos...");
        if (this.biblioteca != null) {
            this.biblioteca.guardarDatosEnArchivo();
        }
    }
}