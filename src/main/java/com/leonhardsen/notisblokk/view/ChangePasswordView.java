package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.ChangePasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChangePasswordView {

    public static void ChangePasswordWindow(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChangePasswordView.class.getResource("ChangePassword.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 160);
        ChangePasswordController changePasswordController = fxmlLoader.getController();
        Stage changePasswordStage = new Stage();
        changePasswordController.setCurrentStage(changePasswordStage);
        changePasswordStage.getIcons().add(new Image(Objects.requireNonNull(ChangePasswordView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        changePasswordStage.initOwner(parentStage);
        changePasswordStage.initModality(Modality.APPLICATION_MODAL);
        changePasswordStage.setResizable(false);
        changePasswordStage.setTitle("Alterar Senha");
        changePasswordStage.setScene(scene);
        changePasswordStage.show();
    }

}
