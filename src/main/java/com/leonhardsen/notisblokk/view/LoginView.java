package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.*;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.model.Users;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginView extends Application {

    public LoginController loginController;
    public JusNoteController jusNoteController;
    public static MainScreenController mainScreenController;
    public static ListAudiencesController listAudiencesController;
    public TagController tagController;
    public NoteController noteController;
    public Stage jusNoteStage;
    public Stage mainStage;
    public Stage tagStage;
    public Stage noteStage;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        loginController = fxmlLoader.getController();
        loginController.setLoginView(this);
        loginController.setCurrentStage(stage);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void openJusNote(Users user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("JusNote.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        jusNoteController = fxmlLoader.getController();
        jusNoteStage = new Stage();
        jusNoteController.setCurrentStage(jusNoteStage);
        jusNoteController.setUser(user);
        jusNoteStage.setTitle("JusNote :: Anotações seguras para seus processos");
        jusNoteStage.setMaximized(true);
        jusNoteStage.setMinWidth(600);
        jusNoteStage.setMinHeight(400);
        jusNoteStage.setScene(scene);
        jusNoteStage.show();
    }

    public static void openMainScreen(){
        setPane("MainScreen.fxml", "mainScreenController","#rootPane");
    }

    public static void openListAudiences(){
        setPane("List_audiences.fxml", "listAudiencesController", "#anchorListAudiences");
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
        tagStage.show();
    }

    public void openNoteView(Tags tag, Notes note, String status) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource("NoteScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        noteController = fxmlLoader.getController();
        noteStage = new Stage();
        noteController.setCurrentStage(noteStage);
        noteController.setData(tag, note, status);
        noteStage.initOwner(mainStage);
        noteStage.initModality(Modality.APPLICATION_MODAL);
        noteStage.setMinWidth(400);
        noteStage.setMinHeight(400);
        noteStage.setTitle("Note");
        noteStage.setScene(scene);
        noteStage.show();
    }

    private static void setPane(String fxmlPath, String controllerName, String anchor) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoginView.class.getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Object controller = fxmlLoader.getController();
            AnchorPane anchorPane = (AnchorPane) root.lookup(anchor);
            anchorPane.setBackground(Background.fill(Color.WHITE));
            AnchorPane mainPane = JusNoteController.instance.mainPane;
            mainPane.getChildren().add(anchorPane);
            anchorPane.maxWidthProperty().bind(mainPane.widthProperty());
            anchorPane.maxHeightProperty().bind(mainPane.heightProperty());
            AnchorPane.setLeftAnchor(anchorPane, 0.0);
            AnchorPane.setRightAnchor(anchorPane, 0.0);
            AnchorPane.setTopAnchor(anchorPane, 0.0);
            AnchorPane.setBottomAnchor(anchorPane, 0.0);
            if (controllerName.equals("listAudiencesController")) {
                listAudiencesController = (ListAudiencesController) controller;
            } else if (controllerName.equals("mainScreenController")) {
                mainScreenController = (MainScreenController) controller;
            }
        } catch (IOException ex) {
            ex.fillInStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
