package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class TagController implements Initializable {

    @FXML public TextField txtTag;
    @FXML public Button btnSave;
    @FXML public ImageView imgSave;
    @FXML public Button btnDelete;
    @FXML public ImageView imgDelete;

    @Setter
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
        btnDelete.setOnMouseClicked(event -> deletaTag(tagItem));
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
            if (tagItem == null) {
                TagsDAO tagsDAO = new TagsDAO();
                Tags tag = new Tags();
                tag.setTag(txtTag.getText());
                tagsDAO.save(tag);
            } else {
                TagsDAO tagsDAO = new TagsDAO();
                Tags tag = new Tags();
                tag.setId(tagItem.getId());
                tag.setTag(txtTag.getText());
                tagsDAO.update(tag);
            }
        }
        fecharJanela();
    }

    private void deletaTag(Tags tag) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exclusão");
        alert.setHeaderText("Tem certeza que deseja excluir a tag selecionada?");
        alert.setContentText("Tag: " + tag.getTag());
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                TagsDAO tagsDAO = new TagsDAO();
                tagsDAO.delete(tag);
                fecharJanela();
            }
        });
    }

    public void fecharJanela() {
        MainScreenController.instance.populaLista();
        MainScreenController.instance.populaTabela();
        MainScreenController.instance.tagItem = null;
        MainScreenController.instance.noteItem = null;
        currentStage.close();
    }

}
