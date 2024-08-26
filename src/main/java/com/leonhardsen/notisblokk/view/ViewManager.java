package com.leonhardsen.notisblokk.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class ViewManager {

    public static void setScreen(String path, String anchor, AnchorPane mainPane) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(ViewManager.class.getResource(path));
            Parent root = fxmlLoader.load();
            fxmlLoader.getController();
            AnchorPane childPane = (AnchorPane) root.lookup(anchor);
            childPane.setBackground(Background.fill(Color.WHITE));
            mainPane.getChildren().add(childPane);
            childPane.maxWidthProperty().bind(mainPane.widthProperty());
            childPane.maxHeightProperty().bind(mainPane.heightProperty());
            AnchorPane.setLeftAnchor(childPane, 0.0);
            AnchorPane.setRightAnchor(childPane, 0.0);
            AnchorPane.setTopAnchor(childPane, 0.0);
            AnchorPane.setBottomAnchor(childPane, 0.0);
        } catch (Exception e){
            e.fillInStackTrace();
        }
    }

}
