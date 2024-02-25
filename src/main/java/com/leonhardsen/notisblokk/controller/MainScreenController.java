package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.view.LoginView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    @FXML public AnchorPane rootPane;
    @FXML public TextField txtPesquisa;
    @FXML public Button btnTag;
    @FXML public Button btnNote;
    @FXML public ListView<Tags> listTag;
    @FXML public TableView<Notes> tblNote;
    @FXML public TableColumn<Notes, Integer> colID;
    @FXML public TableColumn<Notes, String> colData;
    @FXML public TableColumn<Notes, String> colTitulo;
    @FXML public TableColumn<Notes, String> colStatus;
    @FXML public TableColumn<Notes, Integer> colEditar;
    @FXML public ImageView imgPlus;
    @FXML public ComboBox<String> cmbFiltro;

    public LoginView loginView;
    public Tags tagItem;
    public Notes noteItem;
    public String statusItem;
    public static MainScreenController instance;

    @Setter
    private Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
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
                loginView = new LoginView();
                loginView.openTagView(null);
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
                    loginView = new LoginView();
                    loginView.openNoteView(tagItem, null, null);
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
                    loginView = new LoginView();
                    loginView.openTagView(tagItem);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        populaLista();

        statusColor();

        cmbFiltro.setItems(listaFiltro());
        cmbFiltro.getSelectionModel().selectFirst();
        cmbFiltro.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> populaTabela());

    }

    public ObservableList<String> listaFiltro(){
        List<String> lista = new ArrayList<>();
        lista.add("MOSTRAR TODOS");
        lista.add("A RESOLVER");
        lista.add("ATRASADO");
        lista.add("BLOQUEADO");
        lista.add("CANCELADO");
        lista.add("EM ANDAMENTO");
        lista.add("EM REVISÃO");
        lista.add("PENDENTE DE APROVAÇÃO");
        lista.add("PRIORIDADE");
        lista.add("RESOLVIDO");
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
        botaoEditar();
        colEditar.setStyle("-fx-alignment: CENTER");
        if (tagItem != null) {
            NotesDAO notesDAO = new NotesDAO();
            tblNote.setItems(notesDAO.getAll(tagItem.getId(),
                    cmbFiltro.getSelectionModel().getSelectedItem()));
        }
    }

    private void botaoEditar() {
        Callback<TableColumn<Notes, Integer>, TableCell<Notes, Integer>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Notes, Integer> call(final TableColumn<Notes, Integer> param) {
                return new TableCell<>() {

                    private final Button btnEditar = new Button("");
                    private final ImageView imgEditar = new ImageView();
                    private final File logoFile = new File("src/main/resources/com/leonhardsen/notisblokk/image/pen.png");
                    private final Image editarImg = new Image(logoFile.toURI().toString());

                    {
                        btnEditar.setOnMouseClicked(event -> {
                            noteItem = getTableView().getItems().get(getIndex());
                            TagsDAO tagsDAO = new TagsDAO();
                            tagItem = tagsDAO.findID(noteItem.getId_tag());
                            statusItem = noteItem.getStatus();
                            loginView = new LoginView();
                            try {
                                loginView.openNoteView(tagItem, noteItem, statusItem);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            imgEditar.setImage(editarImg);
                            imgEditar.setFitHeight(15);
                            imgEditar.setFitWidth(15);
                            btnEditar.setGraphic(imgEditar);
                            btnEditar.setCursor(Cursor.HAND);
                            btnEditar.setStyle("-fx-background-color: #f89128; -fx-text-fill: #FFFFFF; -fx-background-radius: 10");
                            setGraphic(btnEditar);
                        }
                    }
                };
            }
        };
        colEditar.setCellFactory(cellFactory);
    }

    private void statusColor(){
        colStatus.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Notes, String> call(TableColumn<Notes, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            switch (item) {
                                case "A RESOLVER":
                                    setStyle("-fx-background-color: #e2d970; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "ATRASADO":
                                    setStyle("-fx-background-color: #f73d28; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "BLOQUEADO":
                                    setStyle("-fx-background-color: #ff9d02; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "CANCELADO":
                                    setStyle("-fx-background-color: gray; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "EM ANDAMENTO":
                                    setStyle("-fx-background-color: #6696e1; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "EM REVISÃO":
                                    setStyle("-fx-background-color: #a74c9d; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "PENDENTE DE APROVAÇÃO":
                                    setStyle("-fx-background-color: #eb99b9; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                case "PRIORIDADE":
                                    setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
                                    break;
                                case "RESOLVIDO":
                                    setStyle("-fx-background-color: #00ce7e; -fx-text-fill: black; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("");
                                    break;
                            }
                        }
                    }
                };
            }
        });
    }

}
