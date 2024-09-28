package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.StatusController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StatusView {

    public static void openView(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusView.class.getResource("Status.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 442);
        StatusController statusController = fxmlLoader.getController();
        Stage statusStage = new Stage();
        statusController.setCurrentStage(statusStage);
        statusStage.getIcons().add(new Image(Objects.requireNonNull(StatusView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        statusStage.initOwner(parentStage);
        statusStage.initModality(Modality.APPLICATION_MODAL);
        statusStage.setResizable(false);
        statusStage.setTitle("Status");
        statusStage.setScene(scene);
        statusStage.show();
    }

}
