package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.KalkulatorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class KalkulatorView extends ViewManager {

    public static void openView(AnchorPane mainPane) {

        setScreen("Kalkulator.fxml", "#rootPane", mainPane);

    }

    public static void openView(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusView.class.getResource("Kalkulator.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        KalkulatorController  kalkulatorController= fxmlLoader.getController();
        Stage kalkulatorStage = new Stage();
        kalkulatorController.setCurrentStage();
        kalkulatorStage.getIcons().add(new Image(Objects.requireNonNull(StatusView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        kalkulatorStage.initOwner(parentStage);
        //kalkulatorStage.initModality(Modality.APPLICATION_MODAL);
        kalkulatorStage.setResizable(false);
        kalkulatorStage.setTitle("Kalkulator");
        kalkulatorStage.setScene(scene);
        kalkulatorStage.show();
    }

}
