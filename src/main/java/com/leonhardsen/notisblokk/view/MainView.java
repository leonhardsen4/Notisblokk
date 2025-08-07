package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.MainScreenController;
import com.leonhardsen.notisblokk.model.Users;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainView {

    public static void openView(Users user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        MainScreenController mainScreenController = fxmlLoader.getController();
        Stage mainStage = new Stage();
        mainScreenController.setCurrentStage(mainStage);
        mainStage.getIcons().add(new Image(Objects.requireNonNull(MainView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        mainScreenController.setUser(user);
        mainStage.setTitle("Notisblokk :: Anotações para tudo");
        mainStage.setMaximized(true);
        mainStage.setMinWidth(850);
        mainStage.setMinHeight(600);
        mainStage.setScene(scene);
        mainStage.show();
        //mainStage.setAlwaysOnTop(true);
    }

}
