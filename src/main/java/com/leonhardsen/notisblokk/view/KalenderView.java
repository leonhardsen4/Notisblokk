package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class KalenderView extends ViewManager{

    public static void openView(AnchorPane mainPane) {
        setScreen("Calendar.fxml", "#rootPane", mainPane);
    }

}
