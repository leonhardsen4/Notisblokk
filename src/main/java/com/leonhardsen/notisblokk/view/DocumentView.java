package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class DocumentView extends ViewManager{

    public static void openView(AnchorPane mainPane) {
        setScreen("DigitalFolder.fxml", "#rootPane", mainPane);
    }
}
