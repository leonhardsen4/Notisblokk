package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.UsersDAO;
import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.utils.Crypthograph;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    public TextField txtUsuario;
    public PasswordField txtSenha;
    public Button btnSave;
    public ImageView imgSave;

    public Stage currentStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnSave.setOnMouseClicked(e -> {
            if(verificaUsuario(txtUsuario.getText()) && verificaSenha(txtSenha.getText())){
                UsersDAO usersDAO = new UsersDAO();
                Users user = new Users();
                user.setUser(txtUsuario.getText());
                user.setPassword(Crypthograph.SHA256(txtSenha.getText()));
                usersDAO.save(user);
                currentStage.close();
            }
        });

    }

    private boolean verificaUsuario(String usuario) {
        UsersDAO usersDAO = new UsersDAO();
        if(usersDAO.findUsername(usuario)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Usuário já cadastrado");
            alert.setHeaderText(null);
            alert.setContentText("Este usuário já foi cadastrado. Cadastre outro usuário ou recupere a senha.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
            txtUsuario.setText("");
            txtUsuario.requestFocus();
            return false;
        } else if (usuario.length() < 4 || usuario.length() > 16){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Usuário não permitido");
            alert.setHeaderText(null);
            alert.setContentText("O usuário deve conter no mínimo 4 caracteres e no máximo 16 caracteres.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
            txtUsuario.setText("");
            txtUsuario.requestFocus();
            return false;
        } else {
            return true;
        }
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

