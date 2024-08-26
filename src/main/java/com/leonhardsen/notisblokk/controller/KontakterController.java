package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.ContactDao;
import com.leonhardsen.notisblokk.model.Contact;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
    public ContactDao contactDao;
    public ObservableList<Contact> contactList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        instance = this;

        txtPesquisa.textProperty().addListener((a, b, c) -> populaTabela());

        btnNovoContato.setOnMouseClicked(e -> novoContato());

    }

    private void novoContato() {
        //TODO
    }

    private void populaTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colObservacoes.setCellValueFactory(new PropertyValueFactory<>("observacoes"));
        tblContatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        String filtroPesquisa = txtPesquisa.getText();
        contactDao = new ContactDao();
        contactList.setAll(contactDao.getAll(filtroPesquisa));
        tblContatos.setItems(contactList);
    }
}
