package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.dao.StatusDAO;
import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Status;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.view.LoginView;
import com.leonhardsen.notisblokk.view.NoteView;
import com.leonhardsen.notisblokk.view.StatusView;
import com.leonhardsen.notisblokk.view.TagView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NotisblokkController implements Initializable {

    public AnchorPane rootPane;
    public TextField txtPesquisa;
    public Button btnTag;
    public Button btnNote;
    public ListView<Tags> listTag;
    public TableView<Notes> tblNote;
    public TableColumn<Notes, Integer> colID;
    public TableColumn<Notes, String> colData;
    public TableColumn<Notes, String> colTitulo;
    public TableColumn<Notes, String> colStatus;
    public ImageView imgPlus;
    public TextField txtPesquisaNotas;
    public ComboBox<String> cmbFiltro;

    public LoginView loginView;
    public Tags tagItem;
    public Notes noteItem;
    public String statusItem;
    public static NotisblokkController instance;
    public ObservableList<Notes> notesList;

    public Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = this;

        notesList = FXCollections.observableArrayList();

        txtPesquisa.textProperty().addListener((value, oldValue, newValue) -> {
            try {
                TagsDAO tagsDAO = new TagsDAO();
                listTag.setItems(tagsDAO.search(newValue));
            } catch (Exception e) {
                e.fillInStackTrace();
                e.getCause();
                throw new RuntimeException(e.getMessage());
            }
        });


        btnTag.setOnMouseClicked(e -> {
            try {
                TagView.openView(null, currentStage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        if (tagItem == null) {
            btnNote.setDisable(true);
        }

        btnNote.setOnMouseClicked(e -> {
            try {
                if (tagItem != null) {
                    NoteView.openView(tagItem, null, null, currentStage);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Tag não selecionada");
                    alert.setHeaderText("Selecione uma Tag antes de criar uma nova nota.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            txtPesquisa.requestFocus();
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        listTag.getSelectionModel().selectFirst();

        listTag.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                tagItem = listTag.getSelectionModel().getSelectedItem();
                btnNote.setDisable(false);
                populaTabela();
            } else if (event.getClickCount() == 2) {
                try {
                    tagItem = listTag.getSelectionModel().getSelectedItem();
                    TagView.openView(tagItem, currentStage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        populaLista();

        statusColor();

        cmbFiltro.setItems(listaStatus());
        cmbFiltro.getSelectionModel().selectFirst();
        cmbFiltro.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> populaTabela());
        txtPesquisaNotas.textProperty().addListener((a, b, c) -> populaTabela());

        cmbFiltro.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    StatusView.openView(currentStage);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        tblNote.setRowFactory(e -> {
            TableRow<Notes> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    noteItem = row.getItem();
                    TagsDAO tagsDAO = new TagsDAO();
                    tagItem = tagsDAO.findID(noteItem.getId_tag());
                    statusItem = noteItem.getStatus();
                    loginView = new LoginView();
                    try {
                        NoteView.openView(tagItem, noteItem, statusItem, currentStage);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            return row;
        });

    }

    public ObservableList<String> listaStatus(){
        StatusDAO statusDAO = new StatusDAO();
        ObservableList<Status> listaStatus = statusDAO.getAll();
        List<String> lista = new ArrayList<>();
        lista.add("MOSTRAR TODOS");
        for (Status status : listaStatus) {
            lista.add(status.getStatus());
        }
        return FXCollections.observableArrayList(lista);
    }

    public void populaLista() {
        TagsDAO tagsDAO =  new TagsDAO();
        listTag.setItems(tagsDAO.getAll());
    }

    public void populaTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblNote.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        if (tagItem != null) {
            NotesDAO notesDAO = new NotesDAO();
            String filtroStatus = cmbFiltro.getSelectionModel().getSelectedItem();
            String filtroTitulo = txtPesquisaNotas.getText();
            notesList.setAll(notesDAO.getAll(tagItem.getId(), filtroStatus, filtroTitulo));
            tblNote.setItems(notesList);
        }
    }

    private void statusColor() {
        colStatus.setCellFactory(e -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    StatusDAO statusDAO = new StatusDAO();
                    Status status = statusDAO.findByStatus(item);

                    if (status != null) {
                        Rectangle colorSquare = new Rectangle(20, 20);
                        colorSquare.setFill(Color.web(status.getCor()));
                        HBox hbox = new HBox(5, colorSquare, new Label(item));
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                        setText(item);
                    }
                }
            }
        });
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
