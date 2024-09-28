package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.UsersDAO;
import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.utils.Crypthograph;
import com.leonhardsen.notisblokk.utils.Database;
import com.leonhardsen.notisblokk.view.LoginView;
import com.leonhardsen.notisblokk.view.MainView;
import com.leonhardsen.notisblokk.view.RegisterView;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public TextField txtUsuario;
    public PasswordField txtSenha;
    public Button btnEntrar;
    public Label lblRecuperaSenha;
    public Label lblCadastro;

    public LoginView loginView;
    public Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database.createTables();
        txtUsuario.requestFocus();
        btnEntrar.setOnMouseClicked(e -> {
            try {
                efetuarLogin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        lblCadastro.setOnMouseClicked(e -> {
            try {
                RegisterView.openWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        lblRecuperaSenha.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Recuperação de senha");
            alert.setHeaderText(null);
            alert.setContentText("Se você esqueceu sua senha, envie um e-mail para leonhardsen4@gmail.com e faça a solicitação. Uma senha provisória será enviada, que você poderá alterar mais tarde.");
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
        });

        txtSenha.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    efetuarLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public void efetuarLogin() throws IOException {
        String usuario = txtUsuario.getText();
        String senha = Crypthograph.SHA256(txtSenha.getText());
        UsersDAO usersDAO = new UsersDAO();
        Users usr = usersDAO.find(usuario, senha);
        if (usr != null){
            MainView.openView(usr);
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

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
