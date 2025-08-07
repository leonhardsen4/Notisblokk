package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Scheduling;
import com.leonhardsen.notisblokk.view.ScheduleView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class KalenderController implements Initializable {

    public AnchorPane rootPane;
    public TextField txtPesquisa;
    public DatePicker datePicker;
    public Button btnNovoAgendamento;
    public ImageView imgPlus;
    public TableView<Scheduling> tblAgenda;
    public TableColumn<Scheduling, Integer> colID;
    public TableColumn<Scheduling, String> colData;
    public TableColumn<Scheduling, String> colInicio;
    public TableColumn<Scheduling, String> colTermino;
    public TableColumn<Scheduling, String> colDuracao;
    public TableColumn<Scheduling, String> colAssunto;

    public static KalenderController instance;
    public ObservableList<Scheduling> scheduleList;
    public Stage currentStage;
    public DatePicker datePickerItem;
    public Scheduling scheduleItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        instance = this;

        scheduleList = FXCollections.observableArrayList();

        populaTabela();

        txtPesquisa.textProperty().addListener((a, b, c) -> populaTabela());

        btnNovoAgendamento.setOnMouseClicked(e -> {
            try {
                novoAgendamento();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        tblAgenda.setRowFactory(e -> {
            TableRow<Scheduling> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    scheduleItem = row.getItem();
                    try {
                        ScheduleView.openView(scheduleItem, currentStage);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            return row;
        });
    }

    private void novoAgendamento() throws IOException {
        ScheduleView.openView(null, currentStage);
    }

    private void populaTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("inicio"));
        colTermino.setCellValueFactory(new PropertyValueFactory<>("termino"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
        colAssunto.setCellValueFactory(new PropertyValueFactory<>("assunto"));
        tblAgenda.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        //TODO

    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }


//import com.leonhardsen.notisblokk.model.Scheduling;
//import java.time.LocalDateTime;
//
//    public void generateFreeTimeEvent() {
//        LocalDateTime now = LocalDateTime.now();
//        Scheduling event1 = new Scheduling(now.withHour(9).withMinute(0), now.withHour(10).withMinute(0));
//        Scheduling event2 = new Scheduling(now.withHour(11).withMinute(0), now.withHour(12).withMinute(0));
//
//        LocalDateTime freeStart = LocalDateTime.from(event1.getEndTime().plusMinutes(1));
//        LocalDateTime freeEnd = LocalDateTime.from(event2.getStartTime().minusMinutes(1));
//
//        Scheduling freeTimeEvent = new Scheduling(freeStart, freeEnd);
//        System.out.println("Free time event generated from " + freeStart + " to " + freeEnd);
//    }

}