package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.TagController;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TagView {

    public static void openView(Tags tag, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("TagScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 150);
        TagController tagController = fxmlLoader.getController();
        Stage tagStage = new Stage();
        tagController.setCurrentStage(tagStage);
        tagStage.getIcons().add(new Image(Objects.requireNonNull(TagView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        tagController.setTagItem(tag);
        tagStage.initOwner(parentStage);
        tagStage.initModality(Modality.APPLICATION_MODAL);
        tagStage.setResizable(false);
        tagStage.setTitle("Tag");
        tagStage.setScene(scene);
        tagStage.show();
    }

}
