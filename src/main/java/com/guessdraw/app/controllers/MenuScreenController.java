package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuScreenController {

    @FXML
    private Button drawButton;

    @FXML
    private Button galleryButton;

    @FXML
    protected void openDrawingScreen() {
        SceneChanger.changeScene(SceneChanger.SceneType.DRAWING);
    }

    @FXML
    protected void openGalleryScreen() {
        SceneChanger.changeScene(SceneChanger.SceneType.ALL_DRAWINGS);
    }
}
