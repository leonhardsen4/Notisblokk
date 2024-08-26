package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.UsersDAO;
import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.utils.Crypthograph;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    public PasswordField txtSenha;
    public Button btnSave;
    public ImageView imgSave;
    public Users user;

    public Stage currentStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnSave.setOnMouseClicked(e -> {
            if(verificaSenha(txtSenha.getText())){
                UsersDAO usersDAO = new UsersDAO();
                user = MainScreenController.instance.usr;
                user.setId(user.getId());
                user.setUser(user.getUser());
                user.setPassword(Crypthograph.SHA256(txtSenha.getText()));
                usersDAO.update(user);
                currentStage.close();
            }
        });
    }

    private boolean verificaSenha(String senha) {
        if(senha.length() < 4 || senha.length() > 16){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Senha não permitida");
            alert.setHeaderText(null);
            alert.setContentText("A senha deve conter no mínimo 4 caracteres e no máximo 16 caracteres.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
            txtSenha.setText("");
            txtSenha.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

}
