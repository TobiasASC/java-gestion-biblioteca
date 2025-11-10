package com.poo.gestorbiblioteca.ui.controller.reporte;

import com.poo.gestorbiblioteca.ui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReporteController extends Controller {

    @FXML
    private Label tituloReporteLabel;
    @FXML
    private TextArea reporteTextArea;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Inyector de datos. Rellena el popup con el
     * título y el String del reporte generados por el backend.
     */
    public void setDatos(String titulo, String contenido) {
        tituloReporteLabel.setText(titulo);
        reporteTextArea.setText(contenido);
    }

    @FXML
    private void handleCerrar() {
        this.stage.close();
    }


}
