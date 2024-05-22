package com.guessdraw.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static com.guessdraw.app.handlers.SceneChanger.initializeStage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("fxml/DrawingScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        System.out.println(this.getClass());
        URL url = this.getClass().getResource("styles/stylesheet.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        System.out.println(url.toExternalForm());
        String css = url.toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("GuessDraw");
        stage.setScene(scene);
        stage.show();
        initializeStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}