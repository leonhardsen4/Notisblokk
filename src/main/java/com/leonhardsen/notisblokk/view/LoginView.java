package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginView extends Application{

    @Override
    public void start(Stage loginStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 350);
        LoginController loginController = fxmlLoader.getController();
        loginController.setLoginView(this);
        loginController.setCurrentStage(loginStage);
        loginStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        loginStage.setTitle("Login");
        loginStage.setResizable(false);
        loginStage.setScene(scene);
        loginStage.show();
    }

    public static void main(String[] args) {
        LoginView.launch();
    }

}

