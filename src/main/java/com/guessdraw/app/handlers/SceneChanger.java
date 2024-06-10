package com.guessdraw.app.handlers;

import com.guessdraw.app.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static com.guessdraw.app.Constants.SCREEN_HEIGHT;
import static com.guessdraw.app.Constants.SCREEN_WIDTH;

public class SceneChanger {
    private static Scene MenuScene;
    private static Scene AllDrawingsScene;
    private static Scene DrawingScene;
    private static Scene LoginScene;

    private static Stage stage;

    public static Database.User currentUser = new Database.User();

    public static void initializeStage(Stage stage){
        SceneChanger.stage = stage;
    }

//    private static void initializeScenes(){
//        try {
//            MenuScene = new Scene(new FXMLLoader(Application.class.getResource("fxml/MenuScreen.fxml")).load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//            AllDrawingsScene = new Scene(new FXMLLoader(Application.class.getResource("fxml/AllDrawingsScreen.fxml")).load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//            DrawingScene = new Scene(new FXMLLoader(Application.class.getResource("fxml/DrawingScreen.fxml")).load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//            LoginScene = new Scene(new FXMLLoader(Application.class.getResource("fxml/LoginScreen.fxml")).load(), SCREEN_WIDTH, SCREEN_HEIGHT);
//            URL url = Application.class.getResource("styles/stylesheet.css");
//            if (url == null) {
//                System.out.println("Resource not found. Aborting.");
//                System.exit(-1);
//            }
//            String css = url.toExternalForm();
//            MenuScene.getStylesheets().add(css);
//            AllDrawingsScene.getStylesheets().add(css);
//            DrawingScene.getStylesheets().add(css);
//            LoginScene.getStylesheets().add(css);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public enum SceneType {
        MENU,
        ALL_DRAWINGS,
        DRAWING,
        LOGIN
    }

    public static void changeScene(SceneType sceneType) {
//        initializeScenes();
        try {
            switch (sceneType) {
                case MENU ->
                        stage.getScene().setRoot(new FXMLLoader(Application.class.getResource("fxml/MenuScreen.fxml")).load());
                case ALL_DRAWINGS ->
                        stage.getScene().setRoot(new FXMLLoader(Application.class.getResource("fxml/AllDrawingsScreen.fxml")).load());
                case DRAWING ->
                        stage.getScene().setRoot(new FXMLLoader(Application.class.getResource("fxml/DrawingScreen.fxml")).load());
                case LOGIN ->
                        stage.getScene().setRoot(new FXMLLoader(Application.class.getResource("fxml/LoginScreen.fxml")).load());

            }
            stage.setFullScreen(true);
            stage.show();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
