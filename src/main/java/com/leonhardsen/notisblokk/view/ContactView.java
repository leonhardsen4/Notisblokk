package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.ContactsController;
import com.leonhardsen.notisblokk.model.Contact;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ContactView {

    public static void openView(Contact contact, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ContactView.class.getResource("Contact.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 540, 450);
        ContactsController contactsController = fxmlLoader.getController();
        Stage contactStage = new Stage();
        contactsController.setCurrentStage(contactStage);
        contactStage.getIcons().add(new Image(Objects.requireNonNull(ContactView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        contactsController.setContactItem(contact);
        contactStage.initOwner(parentStage);
        contactStage.initModality(Modality.APPLICATION_MODAL);
        contactStage.setResizable(false);
        contactStage.setTitle("Contato");
        contactStage.setScene(scene);
        contactStage.show();
    }

}
