package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Tags;
import java.nio.charset.StandardCharsets;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class NoteController implements Initializable {

    public TextField txtTitulo;
    public Button btnSave;
    public ImageView imgSave;
    public Button btnDelete;
    public ImageView imgDelete;
    public HTMLEditor htmlEditor;
    public ComboBox<String> cmbStatus;
    public Notes noteItem;
    public Tags tagItem;
    public String statusItem;

    private Stage currentStage;

    public void setData(Tags tagItem, Notes noteItem, String statusItem){
        this.tagItem = tagItem;
        this.noteItem = noteItem;
        this.statusItem = statusItem;

        if (tagItem != null && noteItem != null){
            txtTitulo.setText(noteItem.getTitulo());
            if (noteItem.getRelato() != null) {
                String htmlContent = new String(noteItem.getRelato(), StandardCharsets.UTF_8);
                htmlEditor.setHtmlText(htmlContent);
            }
            txtTitulo.requestFocus();
            cmbStatus.getSelectionModel().select(statusItem);
        } else {
            txtTitulo.requestFocus();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmbStatus.setItems(NotisblokkController.instance.listaStatus());
        cmbStatus.getSelectionModel().selectFirst();

        btnDelete.setOnMouseClicked(e -> deletaNota());
        btnSave.setOnMouseClicked(e -> salvaNota());

    }

    private void deletaNota() {
        if (noteItem == null){
            fecharJanela();
        } else {
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
    }

    private void salvaNota() {
        if (txtTitulo.getText().isEmpty() || txtTitulo.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome da nota para salvar.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    txtTitulo.requestFocus();
                }
            });
        } else if (Objects.equals(cmbStatus.getSelectionModel().getSelectedItem(), "MOSTRAR TODOS")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Selecione um status para sua nota.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    cmbStatus.requestFocus();
                }
            });
        } else {
            NotesDAO notesDAO =  new NotesDAO();
            if (noteItem == null) {
                Notes note = new Notes();
                note.setId_tag(tagItem.getId());
                note.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                note.setTitulo(txtTitulo.getText());
                String htmlContent = htmlEditor.getHtmlText();
                byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
                note.setRelato(htmlBytes);
                note.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
                notesDAO.save(note);
                atualizar();
            } else {
                noteItem.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                noteItem.setTitulo(txtTitulo.getText());
                String htmlContent = htmlEditor.getHtmlText();
                byte[] htmlBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
                noteItem.setRelato(htmlBytes);
                noteItem.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
                notesDAO.update(noteItem);
                atualizar();
            }
        }
    }

    public void atualizar(){
        NotisblokkController.instance.populaLista();
        NotisblokkController.instance.populaTabela();
        NotisblokkController.instance.listaStatus();
        NotisblokkController.instance.noteItem = null;
    }

    public void fecharJanela() {
        atualizar();
        currentStage.close();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

}
