package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.RegisterController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegisterView {

    public static void openWindow(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RegisterView.class.getResource("Register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 220);
        RegisterController registerController = fxmlLoader.getController();
        Stage registerStage = new Stage();
        registerController.setCurrentStage(registerStage);
        registerStage.getIcons().add(new Image(Objects.requireNonNull(RegisterView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        registerStage.initOwner(parentStage);
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.setResizable(false);
        registerStage.setTitle("Cadastro");
        registerStage.setScene(scene);
        registerStage.show();
    }
}
