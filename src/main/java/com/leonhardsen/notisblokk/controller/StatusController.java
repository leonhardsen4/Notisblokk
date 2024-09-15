package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.StatusDAO;
import com.leonhardsen.notisblokk.model.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusController implements Initializable {

    public TextField txtStatus;
    public Button btnSave;
    public Button btnDelete;
    public ImageView imgSave;
    public ImageView imgDelete;
    public ColorPicker colorPicker;
    public TableView<Status> tblStatus;
    public TableColumn<Status, String> colStatus;
    public TableColumn<Status, String> colCor;

    public Status statusItem;
    public ObservableList<Status> statusList;
    public Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        statusList = FXCollections.observableArrayList();
        popularTabela();
        statusColor();

        btnSave.setOnMouseClicked(e -> salvaStatus());

        btnDelete.setOnMouseClicked(e -> excluirStatus());

        tblStatus.setRowFactory(e -> {
            TableRow<Status> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    statusItem = row.getItem();
                    editarStatus();
                } else if (event.getClickCount() == 2 && !row.isEmpty()) {
                    statusItem = null;
                    limparCampos();
                }
            });
            return row;
        });

    }

    private void salvaStatus() {
        if (txtStatus.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome do status para salvar.");
            alert.showAndWait();
            txtStatus.requestFocus();
        } else {
            StatusDAO statusDAO;
            if (statusItem == null) {
                statusDAO = new StatusDAO();
                if (statusDAO.findStatus(txtStatus.getText(), colorPicker.getValue().toString())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Satstus ou Cor Já Cadastrado");
                    alert.setHeaderText("Esta status ou cor já foi cadastrado. Faça uma busca para encontrá-lo, ou cadastre outro status.");
                    alert.showAndWait();
                    txtStatus.setText("");
                    txtStatus.requestFocus();
                    colorPicker.setValue(null);
                } else {
                    statusDAO = new StatusDAO();
                    Status status = new Status();
                    status.setStatus(txtStatus.getText().toUpperCase());
                    status.setCor(colorPicker.getValue().toString());
                    statusDAO.save(status);
                    atualizar();
                }
            } else {
                statusDAO = new StatusDAO();
                statusItem.setStatus(txtStatus.getText().toUpperCase());
                statusItem.setCor(colorPicker.getValue().toString());
                statusDAO.update(statusItem);
                atualizar();
            }
        }
    }

    private void excluirStatus() {
        if (statusItem != null) {
            confirmarExclusao();
        }
    }

    private void editarStatus() {
        if (statusItem != null) {
            txtStatus.setText(statusItem.getStatus());
            colorPicker.setValue(Color.web(statusItem.getCor()));
            txtStatus.requestFocus();
        }
    }

    private void confirmarExclusao() {
        if (statusItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o status?");
            alert.setContentText("Status: " + statusItem.getStatus());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    StatusDAO statusDAO = new StatusDAO();
                    statusDAO.delete(statusItem);
                    statusItem = null;
                    atualizar();
                }
            });
        }
    }

    private void limparCampos() {
        txtStatus.clear();
        colorPicker.setValue(null);
        txtStatus.requestFocus();
        statusItem = null;
    }

    private void statusColor() {
        colCor.setCellFactory(e -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(20, 20, Color.web(item));
                    setGraphic(rect);
                }
            }
        });
    }

    private void popularTabela() {
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cor"));
        colCor.setStyle("-fx-alignment: CENTER");
        StatusDAO statusDAO = new StatusDAO();
        statusList.setAll(statusDAO.getAll());
        tblStatus.setItems(statusList);
    }

    private void atualizar(){
        limparCampos();
        popularTabela();
        NotisblokkController.instance.populaTabela();
        NotisblokkController.instance.cmbFiltro.setItems(NotisblokkController.instance.listaStatus());
        currentStage.close();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
