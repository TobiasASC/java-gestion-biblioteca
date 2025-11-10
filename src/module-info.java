module com.poo.gestorbiblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;


    opens com.poo.gestorbiblioteca.model to javafx.base;

    exports com.poo.gestorbiblioteca.ui;
    opens com.poo.gestorbiblioteca.ui to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller;
    opens com.poo.gestorbiblioteca.ui.controller to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller.libros;
    opens com.poo.gestorbiblioteca.ui.controller.libros to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller.socios;
    opens com.poo.gestorbiblioteca.ui.controller.socios to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller.prestamos;
    opens com.poo.gestorbiblioteca.ui.controller.prestamos to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller.reporte;
    opens com.poo.gestorbiblioteca.ui.controller.reporte to javafx.fxml;

    exports com.poo.gestorbiblioteca.ui.controller.ajustes;
    opens com.poo.gestorbiblioteca.ui.controller.ajustes to javafx.fxml;

}