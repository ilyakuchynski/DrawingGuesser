package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import static com.guessdraw.app.handlers.SceneChanger.currentUser;

public class MenuScreenController {

    @FXML
    private Button drawButton;

    @FXML
    private Button galleryButton;

    @FXML
    private Text loggedAs;

    public void initialize() {
        currentLogin();
    }

    @FXML
    protected void openDrawingScreen() {
        SceneChanger.changeScene(SceneChanger.SceneType.DRAWING);
    }

    @FXML
    protected void openGalleryScreen() {
        SceneChanger.changeScene(SceneChanger.SceneType.ALL_DRAWINGS);
    }

    @FXML
    protected void openLoginScreen() {
        SceneChanger.changeScene(SceneChanger.SceneType.LOGIN);
    }

    private void currentLogin(){
        if(currentUser.user_id == 1){
            loggedAs.setText("You are anonymous author.");
        }else{
            loggedAs.setText("Logged as: " + currentUser.login);
        }
    }
}
