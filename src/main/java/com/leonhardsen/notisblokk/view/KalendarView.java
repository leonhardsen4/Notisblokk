package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

public class KalendarView extends ViewManager{

    public static void openView(AnchorPane mainPane) {
        setScreen("Kalendar.fxml", "#rootPane", mainPane);
    }

}
