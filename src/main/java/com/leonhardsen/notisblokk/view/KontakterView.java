package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class KontakterView extends ViewManager {

    public static void openView(AnchorPane mainPane) throws IOException {
        setScreen("ContactScreen.fxml", "#rootPane", mainPane);
    }
    
}
