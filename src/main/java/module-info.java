module com.guessdraw.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;

    opens com.guessdraw.app to javafx.fxml;
    exports com.guessdraw.app;
    exports com.guessdraw.app.controllers;
    opens com.guessdraw.app.controllers to javafx.fxml;
}