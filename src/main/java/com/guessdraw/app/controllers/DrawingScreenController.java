package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.QueryGPT;
import com.guessdraw.app.handlers.SceneChanger;
import com.guessdraw.app.handlers.Database;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import static com.guessdraw.app.Constants.*;
import static com.guessdraw.app.handlers.Database.*;
import static com.guessdraw.app.handlers.SceneChanger.currentUser;

public class DrawingScreenController {

    private boolean debug_mode = true;

    @FXML
    private Label welcomeText;

    @FXML
    private Canvas canvas;

    @FXML
    private Pane HeaderPane;

    @FXML
    private Pane CanvasPane;

    @FXML
    private VBox MainBox;

    @FXML
    private Button DrawButton;

    @FXML
    private Text topic_text;

    @FXML
    private Text gpt_guess;

    @FXML
    private Text center_text;

    @FXML
    private Text tutorial;

    @FXML
    private Text loggedAs;

    @FXML
    protected void onDrawButtonClick() { // green start button clicked
//        DrawButton.styleProperty().set("-fx-background-color: #1d7829; -fx-text-fill: white; -fx-font-family: winkle; -fx-font-size: 17; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(29, 120, 41, 0.3), 20, 0.5, 0.0, 0.0);");
        // generate a random topic
        currentTopic = get_random_topic();
        topic_text.setText("Draw " + currentTopic.topic_name);
        gpt_guess.setText("");
        drawingWasGuessed = new AtomicBoolean(false);
        // start another thread, responsible for querying gpt about the drawing
        // querying may take a while, so we don't want to block the main thread
        Thread MainThread = Thread.currentThread();
        queryThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            while(MainThread.isAlive() && queryThread == Thread.currentThread()) {
                // concurrency conflict might occur dealing with drawing.png
            /*
                send drawing.png with current topic to gpt agent
                if gpt thinks that the drawing satisfies the topic, then stop
                else, get gpt's thought what the drawing is and display it
                basically if GPT_RESPONSE == currentTopic, then stop
            */
                copyDrawing();
                String GPT_RESPONSE = QueryGPT.queryGPT(currentTopic.topic_name);
                System.out.println("GPT Response: " + GPT_RESPONSE);
                if(GPT_RESPONSE.equals(currentTopic.topic_name)) {
                    drawingWasGuessed.set(true);
                    gpt_guess.setText("");
                    storeDrawing();
                    break;
                }else{
                    gpt_guess.setText(GPT_RESPONSE + "?");
                }
            }
        });
        queryThread.start();
    }

    @FXML
    protected void onDrawButtonRelease() {
//        DrawButton.styleProperty().set("-fx-background-color: #23ad35; -fx-text-fill: white; -fx-font-family: winkle; -fx-font-size: 26; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(29, 120, 41, 0.3), 20, 0.5, 0.0, 0.0);");
        removeButton.play();
        if(center_text.getOpacity() > 0)
            hideCenterText.play();
        closeHeader.play();
        showTopicText.play();
    }

    @FXML
    protected void openMenu(){
        queryThread = null;
        SceneChanger.changeScene(SceneChanger.SceneType.MENU);
    }

    public Topic currentTopic;

    public AtomicBoolean drawingWasGuessed;

    private volatile Thread queryThread;

    final Animation showButton = new Transition() {
        {
            setCycleDuration(Duration.millis(100));
        }
        protected void interpolate(double v) {
            DrawButton.setOpacity(v);
        }
    };
    final Animation removeButton = new Transition() { // remove the button after it is clicked
        {
            setCycleDuration(Duration.millis(100));
        }
        protected void interpolate(double v) {

            DrawButton.setOpacity(1 - v);
            tutorial.setOpacity(1 - v);
        }
    };


    final Animation closeHeader = new Transition() { // close header pane and open the canvas
        {
            setCycleDuration(Duration.millis(500));
        }
        protected void interpolate(double frac){
            final int n = Math.round(CANVAS_MAX_HEIGHT * (float) frac);
            HeaderPane.setPrefHeight(SCREEN_HEIGHT - n);
            CanvasPane.setPrefHeight(n);
            canvas.setHeight(n);
        }

    };

    final Animation showTopicText = new Transition() {
        {
            setCycleDuration(Duration.millis(200));
        }
        protected void interpolate(double frac){
            topic_text.setOpacity(frac);
            gpt_guess.setOpacity(frac);
        }
    };

    final Animation hideTopicText = new Transition() {
        {
            setCycleDuration(Duration.millis(200));
        }
        protected void interpolate(double frac){
            topic_text.setOpacity(1 - frac);
            gpt_guess.setOpacity(1 - frac);
        }
    };

    final Animation showCenterText = new Transition() {
        {
            setCycleDuration(Duration.millis(200));
        }
        protected void interpolate(double frac){
            center_text.setOpacity(frac);
        }
    };

    final Animation hideCenterText = new Transition() {
        {
            setCycleDuration(Duration.millis(200));
        }
        protected void interpolate(double frac){
            center_text.setOpacity(1 - frac);
        }
    };



    final Animation openHeader = new Transition() { // close canvas and show header
        {
            setCycleDuration(Duration.millis(500));
        }
        protected void interpolate(double frac){
            final int n = Math.round(CANVAS_MAX_HEIGHT * (float) (1 - frac));
            HeaderPane.setPrefHeight(SCREEN_HEIGHT - n);
            CanvasPane.setPrefHeight(n);
            canvas.setHeight(n);
        }

    };

    final Animation endRoundAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(3000));
        }
        @Override
        protected void interpolate(double v) {
//            if(v >= 0.75){
//                gpt_guess.setOpacity(4.0 * (1.0 - v));
//            }
        }
    };

    private void endDrawingRound() {
        // clear the canvas
        gpt_guess.setText("Oh, it's " + currentTopic.topic_name + "!");
        System.out.println("Drawing was guessed");

        endRoundAnimation.setOnFinished(e -> {
            gc = canvas.getGraphicsContext2D();
            hideTopicText.play();
            openHeader.play();
            showCenterText.play();
            showButton.play();
        });
        endRoundAnimation.play();
    }

    public double lastMouseX = -1, lastMouseY = -1;
    private MouseButton lastMouseButton = MouseButton.NONE;

    GraphicsContext gc;
    private int counter;

    public void initialize() { // executed when the controller is loaded
        currentLogin();
        File file = new File("drawing.png");
        if(file.exists()) {
            file.delete();
        }
        counter = 0;
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(THICKNESS);
        gc.setFill(Color.BLACK);
        canvas.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.SECONDARY){
                gc.clearRect(0, 0, SCREEN_WIDTH, CANVAS_MAX_HEIGHT);
            }
        });
        canvas.setOnMouseDragged(e -> {
            if(e.getButton() != lastMouseButton){
                lastMouseX = -1;
                lastMouseY = -1;
            }
            lastMouseButton = e.getButton();
            if(e.getButton() == MouseButton.PRIMARY){
                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
            }else if(e.getButton() == MouseButton.SECONDARY){
                gc.clearRect(0, 0, SCREEN_WIDTH, CANVAS_MAX_HEIGHT);
            }
            else return;

            if(lastMouseX == -1) {
                gc.fillOval(e.getX(), e.getY(), THICKNESS, THICKNESS);
            } else {
                gc.beginPath();
                gc.moveTo(lastMouseX, lastMouseY);
                gc.quadraticCurveTo((lastMouseX + e.getX()) / 2, (lastMouseY + e.getY()) / 2, e.getX(), e.getY());
                gc.stroke();
            }
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            ++ counter;
            if(counter >= TICKS_TO_SAVE){
                saveDrawing();
                counter = 0;
                System.out.println("saving");
            }
        });
        canvas.setOnMouseReleased(e -> {
            lastMouseX = -1;
            lastMouseY = -1;

            if(drawingWasGuessed.get()) {
                System.out.println("ROUND FINISHED");
                endDrawingRound();
                drawingWasGuessed.set(false);
            }
        });
        canvas.setWidth(SCREEN_WIDTH);
    }


    public void saveDrawing() {
        // remove file if it already exists
        File file = new File("drawing.png");
        if(file.exists()) {
            file.delete();
        }
        try {
            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("drawing.png"));
        } catch(Exception e) {
            System.err.println("Failed to save drawing");
        }
    }

    public void copyDrawing() {
        try{
            Files.copy(Paths.get("drawing.png"), Paths.get("copy.png"), StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception e){
            System.err.println("Failed to copy drawing");
        }
    }

    public void storeDrawing() {
        try{
            int drawing_id = add_drawing(currentTopic.topic_id, currentUser.user_id);
            if(drawing_id != -1) {
                System.out.println("Drawing stored with id: " + drawing_id);
            }
            Files.copy(Paths.get("copy.png"), Paths.get("AllDrawings/" + drawing_id + ".png"), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            System.err.println("Failed to store drawing");
        }
    }


    public void exitApplication() {
        Platform.exit();
    }

    private void currentLogin(){
        if(currentUser.user_id == 1){
            loggedAs.setText("You are anonymous author.");
        }else{
            loggedAs.setText("Logged as: " + currentUser.login);
        }
    }

}