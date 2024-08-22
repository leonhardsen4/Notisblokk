package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Contact;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ListAudiencesController implements Initializable {

    @FXML public TextField txtPesquisa;
    @FXML public Button btnNovo;
    @FXML public ListView<String> listaProcessos;
    @FXML public TableView<Contact> tblPartes;
    @FXML public TableColumn<Contact, String> colParte;
    @FXML public TableColumn<Contact, String> colNome;
    @FXML public TableColumn<Contact, String> colTelefone;
    @FXML public TableColumn<Contact, String> colEmail;
    @FXML public TableColumn<Contact, String> colSituacao;
    @FXML public TableColumn<Contact, String> colObservacao;
    @FXML public TextField txtDenuncia;
    @FXML public TextField txtDefesa;
    @FXML public TextField txtLaudo;
    @FXML public TextField txtPubli;
    @FXML public TextField txtFa;
    @FXML public TextField txtDist;
    @FXML public TextField txtCota;
    @FXML public TextArea txtObs;
    @FXML public Button btnParte;
    @FXML public Button btnSalvar;
    @FXML public Button btnExcluir;
    @FXML public Label lblProcesso;
    @FXML public Label lblDataHora;
    @FXML public Label lblCompetencia;
    @FXML public Label lblTipo;
    @FXML public Label lblArtigo;
    @FXML public Label lblSituacao;
    @FXML public Label lblJuiz;
    @FXML public Label lblPromotor;

    @FXML public AnchorPane anchorListAudiences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populaTabela();

        btnNovo.setOnMouseClicked(e -> {});

        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {

        });

    }

    private void populaTabela() {
    }


}
