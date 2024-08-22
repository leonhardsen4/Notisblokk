package com.leonhardsen.notisblokk.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ContactsController implements Initializable {

    public TextField txtNome;
    public TextField txtTelefone;
    public TextField txtEmail;
    public TextField txtEndereco;
    public TextArea txtObservacoes;
    public Button btnSave;
    public Button btnDelete;
    public ImageView imgSave;
    public ImageView imgDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
