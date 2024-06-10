package com.guessdraw.app;

import com.guessdraw.app.handlers.Database;
import com.guessdraw.app.handlers.QueryGPT;
import com.guessdraw.app.handlers.TopicGenerator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static com.guessdraw.app.Constants.SCREEN_HEIGHT;
import static com.guessdraw.app.Constants.SCREEN_WIDTH;
import static com.guessdraw.app.handlers.SceneChanger.initializeStage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
//        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue)
//                stage.setMaximized(false);
//        });
        stage.setFullScreen(true);

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