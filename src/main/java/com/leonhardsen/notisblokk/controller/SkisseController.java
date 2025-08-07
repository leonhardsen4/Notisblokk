package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.SketchDao;
import com.leonhardsen.notisblokk.model.Sketch;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class SkisseController implements Initializable {

    public AnchorPane rootPane;
    public HTMLEditor htmlEditor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            byte[] data = setData();
            String htmlContent = new String(data, StandardCharsets.UTF_8);
            htmlEditor.setHtmlText(htmlContent);
        });

        htmlEditor.setOnKeyReleased(event -> {
            SketchDao sketchDao = new SketchDao();
            Sketch sketch = new Sketch();
            String htmlContent = htmlEditor.getHtmlText();
            byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
            sketch.setRascunho(htmlBytes);
            sketchDao.update(sketch);
        });

        htmlEditor.setOnMouseClicked(event -> {
            SketchDao sketchDao = new SketchDao();
            Sketch sketch = new Sketch();
            String htmlContent = htmlEditor.getHtmlText();
            byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
            sketch.setRascunho(htmlBytes);
            sketchDao.update(sketch);
        });

    }

    public byte[] setData(){
        SketchDao sketchDao = new SketchDao();
        return sketchDao.setSketch().getRascunho();
    }

}
