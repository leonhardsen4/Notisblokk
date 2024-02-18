package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.LoginController;
import com.leonhardsen.notisblokk.controller.MainScreenController;
import com.leonhardsen.notisblokk.controller.NoteController;
import com.leonhardsen.notisblokk.controller.TagController;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.model.Users;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {

    public LoginController loginController;
    public MainScreenController mainScreenController;
    public TagController tagController;
    public NoteController noteController;
    public Stage mainStage;
    public Stage tagStage;
    public Stage noteStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        loginController = fxmlLoader.getController();
        loginController.setLoginView(this);
        loginController.setCurrentStage(stage);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void openMainScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("MainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        mainScreenController = fxmlLoader.getController();
        mainStage = new Stage();
        mainScreenController.setCurrentStage(mainStage);
        mainStage.setTitle("MainScreen");
        mainStage.setMinWidth(600);
        mainStage.setMinHeight(400);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public void openTagView(Tags tag) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("TagScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 150);
        tagController = fxmlLoader.getController();
        tagStage = new Stage();
        tagController.setCurrentStage(tagStage);
        tagController.setTagItem(tag);
        tagStage.initOwner(mainStage);
        tagStage.initModality(Modality.APPLICATION_MODAL);
        tagStage.setResizable(false);
        tagStage.setTitle("Tag");
        tagStage.setScene(scene);
        tagStage.setOnCloseRequest(event -> {});
        tagStage.show();
    }

    public void openNoteView(Tags tag, Notes note) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("NoteScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        noteController = fxmlLoader.getController();
        noteStage = new Stage();
        noteController.setCurrentStage(noteStage);
        noteController.setData(tag, note);
        noteStage.initOwner(mainStage);
        noteStage.initModality(Modality.APPLICATION_MODAL);
        noteStage.setMinWidth(400);
        noteStage.setMinHeight(400);
        noteStage.setTitle("Note");
        noteStage.setScene(scene);
        noteStage.setOnCloseRequest(event -> {});
        noteStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
