package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.ContactDao;
import com.leonhardsen.notisblokk.model.Contact;
import com.leonhardsen.notisblokk.view.ContactView;
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
import java.util.ResourceBundle;

public class KontakterController implements Initializable {

    public AnchorPane rootPane;
    public TextField txtPesquisa;
    public Button btnNovoContato;
    public ImageView imgPlus;
    public TableView<Contact> tblContatos;
    public TableColumn<Contact, Integer> colID;
    public TableColumn<Contact, String> colNome;
    public TableColumn<Contact, String> colTelefone;
    public TableColumn<Contact, String> colEmail;
    public TableColumn<Contact, String> colEndereco;
    public TableColumn<Contact, String> colObservacoes;
    
    public static KontakterController instance;
    public ObservableList<Contact> contactList;
    public Stage currentStage;
    public Contact contactItem;
    public Button btnAdicionar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        instance = this;

        contactList = FXCollections.observableArrayList();

        populaTabela();

        txtPesquisa.textProperty().addListener((a, b, c) -> populaTabela());

        btnNovoContato.setOnMouseClicked(e -> {
            try {
                novoContato();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        tblContatos.setRowFactory(e -> {
            TableRow<Contact> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    contactItem = row.getItem();
                    try {
                        ContactView.openView(contactItem, currentStage);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            return row;
        });

    }

    private void novoContato() throws IOException {
        ContactView.openView(null, currentStage);
    }

    public void populaTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colObservacoes.setCellValueFactory(new PropertyValueFactory<>("observacoes"));
        tblContatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        ContactDao contactDao = new ContactDao();
        contactList.setAll(contactDao.getAll(txtPesquisa.getText()));
        tblContatos.setItems(contactList);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

}
