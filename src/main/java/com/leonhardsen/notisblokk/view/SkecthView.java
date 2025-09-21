package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.SkisseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SkecthView extends ViewManager {

    public static void openView(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StatusView.class.getResource("Skisse.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        SkisseController skisseController= fxmlLoader.getController();
        Stage skisseStage = new Stage();
        skisseController.setCurrentStage(skisseStage);
        skisseStage.getIcons().add(new Image(Objects.requireNonNull(StatusView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        skisseStage.initOwner(parentStage);
        skisseStage.setResizable(true);
        skisseStage.setMinHeight(500);
        skisseStage.setMinWidth(500);
        skisseStage.setTitle("Skisse");
        skisseStage.setScene(scene);
        skisseStage.show();
    }

}
