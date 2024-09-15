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
        if (txtTag.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome da tag para salvar.");
            alert.showAndWait();
            txtTag.requestFocus();
        } else {
            TagsDAO tagsDAO;
            if (tagItem == null) {
                 tagsDAO = new TagsDAO();
                if (tagsDAO.findTag(txtTag.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Tag Já Cadastrada");
                    alert.setHeaderText("Esta tag já foi cadastrada. Faça uma busca para encontrá-la, ou cadastre outra tag.");
                    alert.showAndWait();
                    txtTag.setText("");
                    txtTag.requestFocus();
                } else {
                    tagsDAO = new TagsDAO();
                    Tags tag = new Tags();
                    tag.setTag(txtTag.getText());
                    tagsDAO.save(tag);
                    fecharJanela();
                }
            } else {
                tagsDAO = new TagsDAO();
                tagItem.setTag(txtTag.getText());
                tagsDAO.update(tagItem);
                fecharJanela();
            }
        }
    }

    private void deletaTag() {
        if (tagItem == null) {
            fecharJanela();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir a tag selecionada?");
            alert.setContentText("Esta operação irá apagar também todas as notas relacionadas à tag " + tagItem.getTag() + ".");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    TagsDAO tagsDAO = new TagsDAO();
                    tagsDAO.delete(tagItem);
                    fecharJanela();
                }
            });
        }
    }

    public void atualizar(){
        NotisblokkController.instance.populaLista();
        NotisblokkController.instance.populaTabela();
        NotisblokkController.instance.tagItem = null;
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
