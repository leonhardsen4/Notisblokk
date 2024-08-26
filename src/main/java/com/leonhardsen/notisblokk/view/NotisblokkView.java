package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class NotisblokkView extends ViewManager {

    public static void openView(AnchorPane mainPane) {
        setScreen("Notisblokk.fxml", "#rootPane", mainPane);
    }

}
