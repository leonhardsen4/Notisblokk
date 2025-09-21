package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.TemplateController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TemplateView {

    public static void openView(Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TemplateView.class.getResource("TemplateScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 660, 500);
        TemplateController templateController = fxmlLoader.getController();
        Stage noteStage = new Stage();
        templateController.setCurrentStage(noteStage);
        noteStage.getIcons().add(new Image(Objects.requireNonNull(NoteView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        noteStage.initOwner(parentStage);
        noteStage.initModality(Modality.APPLICATION_MODAL);
        noteStage.setMinWidth(660);
        noteStage.setMinHeight(500);
        noteStage.setTitle("Templates");
        noteStage.setScene(scene);
        noteStage.show();
    }

}
