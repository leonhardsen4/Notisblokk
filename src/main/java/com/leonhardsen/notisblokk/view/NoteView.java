package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.NoteController;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NoteView {

    public static void openView(Tags tag, Notes note, String status, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NoteView.class.getResource("NoteScreenHTMLEditor.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 660, 500);
        NoteController noteController = fxmlLoader.getController();
        Stage noteStage = new Stage();
        noteController.setCurrentStage(noteStage);
        noteController.setData(tag, note, status);
        noteStage.initOwner(parentStage);
        noteStage.initModality(Modality.APPLICATION_MODAL);
        noteStage.setMinWidth(660);
        noteStage.setMinHeight(500);
        noteStage.setTitle("Note");
        noteStage.setScene(scene);
        noteStage.show();
    }

}
