package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.UsersDAO;
import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.utils.Crypthograph;
import com.leonhardsen.notisblokk.utils.Database;
import com.leonhardsen.notisblokk.view.LoginView;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @Getter
    @Setter
    private LoginView loginView;

    public TextField txtUsuario;
    public PasswordField txtSenha;
    public Button btnEntrar;

    @Setter
    public Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtUsuario.requestFocus();
        btnEntrar.setOnMouseClicked(e -> {
            try {
                efetuarLogin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void efetuarLogin() throws Exception {
        String usuario = txtUsuario.getText();
        String senha = Crypthograph.SHA256(txtSenha.getText());
        UsersDAO usersDAO = new UsersDAO();
        Users usr = usersDAO.find(usuario, senha);
        if (usr != null){
            Database.createTables();
            loginView.openMainScreen();
            fecharLogin();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Oh-oh!");
            alert.setContentText("Login não efetuado!");
            alert.showAndWait();
            txtUsuario.setText("");
            txtSenha.setText("");
            txtUsuario.requestFocus();
        }
    }

    public void fecharLogin() {
        txtUsuario.setText("");
        txtSenha.setText("");
        currentStage.close();
    }

}
