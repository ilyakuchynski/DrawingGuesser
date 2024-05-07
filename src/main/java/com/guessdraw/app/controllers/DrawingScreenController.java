package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.TopicGenerator;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import static com.guessdraw.app.Constants.*;

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
    private Text center_text;

    @FXML
    protected void onDrawButtonClick() { // green start button clicked
        DrawButton.styleProperty().set("-fx-background-color: #1d7829; -fx-text-fill: white; -fx-font-family: winkle; -fx-font-size: 17; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(29, 120, 41, 0.3), 20, 0.5, 0.0, 0.0);");

        // generate a random topic
        currentTopic = TopicGenerator.generateTopic();
        topic_text.setText("Draw " + currentTopic);
        drawingWasGuessed = new AtomicBoolean(false);

        // start another thread, responsible for querying gpt about the drawing
        // querying may take a while, so we don't want to block the main thread
        queryThread = new Thread(() -> {
            while(true) {
                // concurrency conflict might occur dealing with drawing.png
            /*
                send drawing.png with current topic to gpt agent
                if gpt thinks that the drawing satisfies the topic, then stop
                else, get gpt's thought what the drawing is and display it
                basically if GPT_RESPONSE == currentTopic, then stop
            */
                String GPT_RESPONSE = currentTopic; // = gpt.query("drawing.png", currentTopic); or smth like this
                if(GPT_RESPONSE.equals(currentTopic)) {
                    drawingWasGuessed.set(true);
                    break;
                }
            }
        });
        queryThread.start();
    }

    @FXML
    protected void onDrawButtonRelease() {
        DrawButton.styleProperty().set("-fx-background-color: #23ad35; -fx-text-fill: white; -fx-font-family: winkle; -fx-font-size: 26; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(29, 120, 41, 0.3), 20, 0.5, 0.0, 0.0);");
        removeButton.play();
        hideCenterText.play();
        closeHeader.play();
        showTopicText.play();
    }

    public String currentTopic = "";

    public AtomicBoolean drawingWasGuessed;

    private Thread queryThread;

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
        }
    };


    final Animation closeHeader = new Transition() { // close header pane and open the canvas
        {
            setCycleDuration(Duration.millis(500));
        }
        protected void interpolate(double frac){
            final int n = Math.round(CANVAS_MAX_HEIGHT * (float) frac);
            HeaderPane.setPrefHeight(600 - n);
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
        }
    };

    final Animation hideTopicText = new Transition() {
        {
            setCycleDuration(Duration.millis(200));
        }
        protected void interpolate(double frac){
            topic_text.setOpacity(1 - frac);
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
            HeaderPane.setPrefHeight(600 - n);
            CanvasPane.setPrefHeight(n);
            canvas.setHeight(n);
        }

    };

    private void endDrawingRound() {
        // clear the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, SCREEN_WIDTH, CANVAS_MAX_HEIGHT);
        hideTopicText.play();
        openHeader.play();
        showCenterText.play();
        showButton.play();
    }

    public double lastMouseX = -1, lastMouseY = -1;
    public void initialize() { // executed when the controller is loaded
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setLineWidth(THICKNESS);
        canvas.setOnMouseDragged(e -> {
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
            saveDrawing();
        });
        canvas.setOnMouseReleased(e -> {
            lastMouseX = -1;
            lastMouseY = -1;

            if(drawingWasGuessed.get()) {
                endDrawingRound();
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


}