package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TagController implements Initializable {

    public TextField txtTag;
    public Button btnSave;
    public ImageView imgSave;
    public Button btnDelete;
    public ImageView imgDelete;

    private Stage currentStage;

    private Tags tagItem;

    public void setTagItem(Tags tagItem) {
        this.tagItem = tagItem;
       if (tagItem != null){
           txtTag.setText(tagItem.getTag());
       } else {
           txtTag.requestFocus();
       }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDelete.setOnMouseClicked(event -> deletaTag());
        btnSave.setOnMouseClicked(event -> salvaTag());
    }

    private void salvaTag() {
        if (txtTag.getText().isEmpty() || txtTag.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome da tag para salvar.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    txtTag.requestFocus();
                }
            });
        } else {
            TagsDAO tagsDAO = new TagsDAO();
            Tags tag = new Tags();
            if (tagItem == null) {
                tag.setTag(txtTag.getText());
                tagsDAO.save(tag);
            } else {
                tag.setId(tagItem.getId());
                tag.setTag(txtTag.getText());
                tagsDAO.update(tag);
            }
        }
        fecharJanela();
    }

    private void deletaTag() {
        if (tagItem == null) {
            fecharJanela();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir a tag selecionada?");
            alert.setContentText("Tag: " + tagItem.getTag());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    TagsDAO tagsDAO = new TagsDAO();
                    tagsDAO.delete(tagItem);
                    fecharJanela();
                }
            });
        }
    }

    public void fecharJanela() {
        NotisblokkController.instance.populaLista();
        NotisblokkController.instance.populaTabela();
        NotisblokkController.instance.tagItem = null;
        NotisblokkController.instance.noteItem = null;
        currentStage.close();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
