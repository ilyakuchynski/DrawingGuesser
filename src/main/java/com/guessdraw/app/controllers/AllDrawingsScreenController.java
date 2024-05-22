package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AllDrawingsScreenController {

    @FXML
    private Button MenuButton;


    @FXML
    protected void openMenu(){
        SceneChanger.changeScene(SceneChanger.SceneType.MENU);
    }
}
