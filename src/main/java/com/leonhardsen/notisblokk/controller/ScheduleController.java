package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Scheduling;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    public TextField txtAssunto;
    public DatePicker datePicker;
    public ComboBox<String> cmbInicio;
    public ComboBox<String> cmbTermino;
    public TextField txtDuracao;
    public HTMLEditor txtDescricao;
    public Button btnSave;
    public ImageView imgSave;
    public Button btnDelete;
    public ImageView imgDelete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setScheduleItem(Scheduling schedule) {
    }

    public void setCurrentStage(Stage scheduleStage) {
    }
}
