package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class SkecthView extends ViewManager {

    public static void openView(AnchorPane mainPane) {
        setScreen("Skisse.fxml", "#rootPane", mainPane);
    }

}
