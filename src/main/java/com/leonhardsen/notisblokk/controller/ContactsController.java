package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.ContactDao;
import com.leonhardsen.notisblokk.model.Contact;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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

    public Stage currentStage;
    public Contact contactItem;
    public ContactDao contactDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDelete.setOnMouseClicked(event -> apagaContato());
        btnSave.setOnMouseClicked(event -> salvaContato());
    }

    private void salvaContato() {
        contactDao = new ContactDao();
        if (contactDao.findContact(txtNome.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Contato Já Cadastrado");
            alert.setHeaderText("Este contato já foi cadastrado. Faça uma busca para encontrá-lo, ou cadastre outro contato.");
            alert.showAndWait();
            txtNome.setText("");
            txtNome.requestFocus();
        } else if (txtNome.getText().isEmpty() || txtNome.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo vazio");
            alert.setHeaderText("Escreva o nome do contato para salvar.");
            alert.showAndWait();
            txtNome.requestFocus();
        } else {
            contactDao = new ContactDao();
            Contact contact = new Contact();
            if (contactItem == null) {
                contact.setNome(txtNome.getText());
                contact.setTelefone(txtTelefone.getText());
                contact.setEmail(txtEmail.getText());
                contact.setEndereco(txtEndereco.getText());
                contact.setObservacoes(txtObservacoes.getText());
                contactDao.save(contact);
                atualizar();
            } else {
                contact.setId(contactItem.getId());
                contact.setNome(txtNome.getText());
                contact.setTelefone(txtTelefone.getText());
                contact.setEmail(txtEmail.getText());
                contact.setEndereco(txtEndereco.getText());
                contact.setObservacoes(txtObservacoes.getText());
                contactDao.update(contact);
                atualizar();
            }
        }
    }

    private void apagaContato() {
        if (contactItem == null) {
            fecharJanela();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o contato selecionado?");
            alert.setContentText("Contato: " + contactItem.getNome());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    contactDao = new ContactDao();
                    contactDao.delete(contactItem);
                    fecharJanela();
                }
            });
        }
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setContactItem(Contact contactItem) {
        this.contactItem = contactItem;
        if (contactItem != null){
            txtNome.setText(contactItem.getNome());
            txtTelefone.setText(contactItem.getTelefone());
            txtEmail.setText(contactItem.getEmail());
            txtEndereco.setText(contactItem.getEndereco());
            txtObservacoes.setText(contactItem.getObservacoes());
        } else {
            txtNome.requestFocus();
        }
    }

    public void atualizar(){
        KontakterController.instance.populaTabela();
        KontakterController.instance.contactItem = null;
    }

    public void fecharJanela() {
        atualizar();
        currentStage.close();
    }
}
