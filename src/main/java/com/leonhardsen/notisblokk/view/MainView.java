package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.MainScreenController;
import com.leonhardsen.notisblokk.model.Users;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView {

    public static void openView(Users user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        MainScreenController mainScreenController = fxmlLoader.getController();
        Stage mainStage = new Stage();
        mainScreenController.setCurrentStage(mainStage);
        mainScreenController.setUser(user);
        mainStage.setTitle("JusNote :: Anotações seguras para seus processos");
        mainStage.setMaximized(true);
        mainStage.setMinWidth(600);
        mainStage.setMinHeight(400);
        mainStage.setScene(scene);
        mainStage.show();
    }

}
