package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NoteController implements Initializable {

    @FXML public TextField txtTitulo;
    @FXML public Button btnSave;
    @FXML public ImageView imgSave;
    @FXML public Button btnDelete;
    @FXML public ImageView imgDelete;
    @FXML public TextArea txtAnotacao;
    @FXML public ComboBox<String> cmbStatus;
    public Notes noteItem;
    public Tags tagItem;
    public String statusItem;

    @Setter
    private Stage currentStage;

    public void setData(Tags tagItem, Notes noteItem, String statusItem){
        this.tagItem = tagItem;
        this.noteItem = noteItem;
        this.statusItem = statusItem;

        if (tagItem != null && noteItem != null){
            txtTitulo.setText(noteItem.getTitulo());
            txtAnotacao.setText(noteItem.getRelato());
            txtTitulo.requestFocus();
            cmbStatus.getSelectionModel().select(statusItem);
        } else {
            txtTitulo.requestFocus();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmbStatus.setItems(listaStatus());
        cmbStatus.getSelectionModel().selectFirst();

        btnDelete.setOnMouseClicked(event -> deletaNota());
        btnSave.setOnMouseClicked(event -> salvaNota());

    }

    private void deletaNota() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exclusão");
        alert.setHeaderText("Tem certeza que deseja excluir a nota selecionada?");
        alert.setContentText("Nota: " + noteItem.getTitulo());
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                NotesDAO notesDAO = new NotesDAO();
                notesDAO.delete(noteItem);
                fecharJanela();
            }
        });
    }

    private void salvaNota() {
        if (txtTitulo.getText().isEmpty() || txtTitulo.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome da nota para salvar.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    txtTitulo.requestFocus();
                }
            });
        } else {
            NotesDAO notesDAO =  new NotesDAO();
            if (noteItem == null) {
                Notes note = new Notes();
                note.setId_tag(tagItem.getId());
                note.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                note.setTitulo(txtTitulo.getText());
                note.setRelato(txtAnotacao.getText());
                note.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
                notesDAO.save(note);
            } else {
                noteItem.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                noteItem.setTitulo(txtTitulo.getText());
                noteItem.setRelato(txtAnotacao.getText());
                noteItem.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
                notesDAO.update(noteItem);
            }
        }
        fecharJanela();
    }

    public void fecharJanela() {
        MainScreenController.instance.populaLista();
        MainScreenController.instance.populaTabela();
        //MainScreenController.instance.tagItem = null;
        MainScreenController.instance.noteItem = null;
        currentStage.close();
    }

    public ObservableList<String> listaStatus(){
        List<String> lista = new ArrayList<>();
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

}
