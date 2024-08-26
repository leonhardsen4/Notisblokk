package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class KontakterView extends ViewManager {

    public static void openView(AnchorPane mainPane) {
        setScreen("ContactScreen.fxml", "#rootPane", mainPane);
    }
    
}
