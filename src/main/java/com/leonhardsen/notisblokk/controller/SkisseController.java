package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.SketchDao;
import com.leonhardsen.notisblokk.model.Sketch;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SkisseController implements Initializable {

    public AnchorPane rootPane;
    public TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            String data = setData();
            textArea.setText(data);
        });

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            SketchDao sketchDao = new SketchDao();
            Sketch sketch = new Sketch();
            sketch.setRascunho(newValue);
            sketchDao.update(sketch);
        });

    }

    public String setData(){
        SketchDao sketchDao = new SketchDao();
        return sketchDao.setSketch().getRascunho();
    }

}
