package com.guessdraw.app.controllers;

import com.guessdraw.app.handlers.Database;
import com.guessdraw.app.handlers.SceneChanger;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static com.guessdraw.app.handlers.SceneChanger.currentUser;

public class LoginController {

    @FXML
    private PasswordField loginPassword;
    @FXML
    private PasswordField registerPassword1;
    @FXML
    private PasswordField registerPassword2;
    @FXML
    private TextField registerUsername;
    @FXML
    private TextField loginUsername;
    @FXML
    private Text userStatus;
    @FXML
    private Text successStatus;
    @FXML
    private Text loggedAs;

    public void initialize() {
        currentLogin();
    }

    final Animation errorAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(3000));
        }
        @Override
        protected void interpolate(double v) {
            if(v < 0.5){
                userStatus.setOpacity(1);
            }
            if(v >= 0.5){
                userStatus.setOpacity(2.0 * (1.0 - v));
            }
        }
    };

    final Animation successAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(3000));
        }
        @Override
        protected void interpolate(double v) {
            if(v < 0.5){
                successStatus.setOpacity(1);
            }
            if(v >= 0.5){
                successStatus.setOpacity(2.0 * (1.0 - v));
            }
        }
    };

    private void showError(String message){
        userStatus.setText(message);
        errorAnimation.play();
    }

    @FXML
    protected void login() {
        if(loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()){
            showError("Please fill in all fields.");
            return;
        }
        String login = new String(loginUsername.getText());
        String password = getHashedPassword(new String(loginPassword.getText()));
        currentUser = Database.login(login, password);
        if(currentUser.user_id == 1){
//            userStatus.setText("Couldn't log in. Please check your credentials.");
            showError("Couldn't log in. Please check your credentials.");
        }else{
            successAnimation.play();
        }
        currentLogin();
    }


    @FXML
    protected void register() {
        if(registerUsername.getText().isEmpty() || registerPassword1.getText().isEmpty() || registerPassword2.getText().isEmpty()){
            showError("Please fill in all fields.");
//            userStatus.setText("Please fill in all fields.");
            return;
        }
        String login = new String(registerUsername.getText());
        String password1 = getHashedPassword(new String(registerPassword1.getText()));
        String password2 = getHashedPassword(new String(registerPassword2.getText()));
        if(!password1.equals(password2)){
//            userStatus.setText("Passwords don't match.");
            showError("Passwords don't match.");
            return;
        }
        currentUser = Database.register(login, password1);
        if(currentUser.user_id == 1){
            showError("Couldn't register. Please try again.");
//            userStatus.setText("Couldn't register. Please try again.");
        }else{
            successAnimation.play();
        }
        currentLogin();
    }

    private static String getHashedPassword(String password){
        String hashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            hashedPassword = bytesToHex(encodedhash);
        }catch (Exception e){
            e.printStackTrace();
        }
        return hashedPassword;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void currentLogin(){
        if(currentUser.user_id == 1){
            loggedAs.setText("You are anonymous author.");
        }else{
            loggedAs.setText("Logged as: " + currentUser.login);
        }
    }

    @FXML
    protected void openMenu(){
        SceneChanger.changeScene(SceneChanger.SceneType.MENU);
    }

}
