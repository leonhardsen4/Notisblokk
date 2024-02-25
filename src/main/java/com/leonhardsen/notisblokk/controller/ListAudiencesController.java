package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.AudiencesDAO;
import com.leonhardsen.notisblokk.model.Audiences;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ListAudiencesController implements Initializable {
    
    public TextField txtPesquisa;
    public Button btnNovaAudiencia;
    public TableView<Audiences> tblAudiencias;
    public TableColumn<Audiences, String> colData;
    public TableColumn<Audiences, String> colHorario;
    public TableColumn<Audiences, String> colProcesso;
    public TableColumn<Audiences, String> colJuiz;
    public TableColumn<Audiences, String> colCompetencia;
    public TableColumn<Audiences, String> colArtigo;
    public TableColumn<Audiences, String> colSituacao;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populaTabela();

        btnNovaAudiencia.setOnMouseClicked(e -> {});

        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            AudiencesDAO audiencesDAO = new AudiencesDAO();
            tblAudiencias.setItems(audiencesDAO.search(newValue));
        });

    }

    private void populaTabela() {
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colProcesso.setCellValueFactory(new PropertyValueFactory<>("processo"));
        colJuiz.setCellValueFactory(new PropertyValueFactory<>("juiz"));
        colCompetencia.setCellValueFactory(new PropertyValueFactory<>("competencia"));
        colArtigo.setCellValueFactory(new PropertyValueFactory<>("capitulacao"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        tblAudiencias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        AudiencesDAO audiencesDAO = new AudiencesDAO();
        audiencesDAO.getAll();
    }


}
