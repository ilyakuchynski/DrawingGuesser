package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.Database;
import com.guessdraw.app.handlers.Database.Drawing;
import com.guessdraw.app.handlers.SceneChanger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;

import static com.guessdraw.app.handlers.SceneChanger.currentUser;

public class AllDrawingsScreenController {

    @FXML
    private Button MenuButton;

    @FXML
    private Text topic1;
    @FXML
    private Text topic2;
    @FXML
    private Text topic3;
    @FXML
    private Text topic4;
    @FXML
    private Text topic5;
    @FXML
    private Text topic6;

    @FXML
    private Text author1;
    @FXML
    private Text author2;
    @FXML
    private Text author3;
    @FXML
    private Text author4;
    @FXML
    private Text author5;
    @FXML
    private Text author6;

    @FXML
    private Rectangle clip1;
    @FXML
    private Rectangle clip2;
    @FXML
    private Rectangle clip3;
    @FXML
    private Rectangle clip4;
    @FXML
    private Rectangle clip5;
    @FXML
    private Rectangle clip6;

    @FXML
    private Text loggedAs;

    private ImageView drawings[] = new ImageView[6];

    private Text topics[];

    private Rectangle clips[];

    private Text authors[];

    public void initialize(){
        currentLogin();
        topics = new Text[]{topic1, topic2, topic3, topic4, topic5, topic6};
        authors = new Text[]{author1, author2, author3, author4, author5, author6};
        clips = new Rectangle[]{clip1, clip2, clip3, clip4, clip5, clip6};
        nextDrawings();
    }

    public static Drawing getDrawing(){
        Drawing drawing = Database.get_random_drawing();
        System.out.println("Drawing id: " + drawing.drawing_id + " Topic: " + drawing.topic.topic_name + " User: " + Database.getUsername(drawing.user_id));
        return drawing;
    }

    @FXML
    protected void openMenu(){
        SceneChanger.changeScene(SceneChanger.SceneType.MENU);
    }

    @FXML
    protected void nextDrawings(){
        for(int i = 0; i < 6; i++){
            try {
                Drawing drawing = getDrawing();
                Image image = new Image("file:AllDrawings/" + drawing.drawing_id + ".png");
//            drawings[i].setImage(image);
                clips[i].setFill(new ImagePattern(image));
                topics[i].setText(drawing.topic.topic_name);
                authors[i].setText("Author: " + Database.getUsername(drawing.user_id));
            }catch(Exception e) {
                System.out.println("Couldn't load image: " + e.getMessage() + " " + e.getClass());
            }
        }
    }
    @FXML
    protected void prevDrawings(){
        for(int i = 5; i >= 0; i--){
            try {
                Drawing drawing = getDrawing();
                Image image = new Image("file:AllDrawings/" + drawing.drawing_id + ".png");
//            drawings[i].setImage(image);
                clips[i].setFill(new ImagePattern(image));
                topics[i].setText(drawing.topic.topic_name);
                authors[i].setText("Author: " + Database.getUsername(drawing.user_id));
            }catch(Exception e) {
                System.out.println("Couldn't load image: " + e.getMessage() + " " + e.getClass());
            }
        }
    }

    private void currentLogin(){
        if(currentUser.user_id == 1){
            loggedAs.setText("You are anonymous author.");
        }else{
            loggedAs.setText("Logged as: " + currentUser.login);
        }
    }
}
