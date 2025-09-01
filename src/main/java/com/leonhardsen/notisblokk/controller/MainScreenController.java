package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.view.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.leonhardsen.notisblokk.view.ChangePasswordView.ChangePasswordWindow;

public class MainScreenController implements Initializable {

    public AnchorPane rootPane;
    public Label lblDataHora;
    public Label lblUsuario;
    public MenuItem menuItemAnotacoes;
    public MenuItem menuItemContatos;
    public MenuItem menuItemRascunho;
    public MenuItem menuItemCalendario;
    public MenuItem menuItemAlterarSenha;
    public MenuItem menuItemSair;
    public Stage currentStage;
    public Users usr;

    public static MainScreenController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = this;

        Timeline clock = getClock();
        clock.play();


        lblUsuario.setOnMouseClicked(e -> {
            try {
                ChangePasswordWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menuItemAnotacoes.setOnAction(e -> NotisblokkView.openView(rootPane));
        menuItemContatos.setOnAction(e -> KontakterView.openView(rootPane));
        menuItemRascunho.setOnAction(e -> SkecthView.openView(rootPane));
        menuItemCalendario.setOnAction(e -> KalendarView.openView(rootPane));

        menuItemAlterarSenha.setOnAction(e -> {
            try {
                ChangePasswordWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        menuItemSair.setOnAction(e -> sairDoSistema());

    }

    @NotNull
    private Timeline getClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            String diaDaSemana = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-br")).toUpperCase();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataHoraAtual = diaDaSemana + ", " + now.format(formatter);
            lblDataHora.setText(dataHoraAtual);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        return clock;
    }

    public void setUser(Users user){
        usr = user;
        lblUsuario.setText("USUÁRIO: " + usr.getUser());
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void sairDoSistema() {
        if (currentStage != null) {
            currentStage.close();
        }
        Platform.exit();
        System.exit(0);
    }

}
