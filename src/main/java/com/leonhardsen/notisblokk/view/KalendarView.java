package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class KalendarView extends ViewManager{

    public static void openView(AnchorPane mainPane) throws IOException {
        setScreen("Kalendar.fxml", "#rootPane", mainPane);
    }

}
