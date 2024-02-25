package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.view.LoginView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class JusNoteController implements Initializable {

    @FXML public AnchorPane mainPane;
    @FXML public AnchorPane drawerPane;
    @FXML public AnchorPane statusBar;
    @FXML public Label lblDataHora;
    @FXML public Label lblUsuario;
    @FXML public Label lblInicio;
    @FXML public ImageView imgBurger;
    public Stage jusNoteStage;

    boolean isOpen;
    public static JusNoteController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        Timeline clock = getClock();
        clock.play();

        lblInicio.setOnMouseClicked(event -> {
            LoginView.openMainScreen();
            closeSideMenu();
        });

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), drawerPane);
        translateTransition.setToX(-180);
        translateTransition.play();
        isOpen = false;

        imgBurger.setOnMouseClicked(event -> {
            if (isOpen) {
                closeSideMenu();
            } else {
                openSideMenu();
            }
        });

        mainPane.setOnMouseClicked(event -> {
            if (isOpen) {
                closeSideMenu();
            }
        });

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

    public void openSideMenu() {
        drawerPane.toFront();
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), drawerPane);
        translateTransition.setToX(0);
        translateTransition.play();
        isOpen = true;
        mainPane.setDisable(true);
    }

    public void closeSideMenu() {
        drawerPane.toFront();
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), drawerPane);
        translateTransition.setToX(-180);
        translateTransition.play();
        isOpen = false;
        mainPane.setDisable(false);
    }

    public void setUser(Users user){
        lblUsuario.setText("USUÁRIO: " + user.getUser());
    }

    public void setCurrentStage(Stage jusNoteStage) {
        this.jusNoteStage = jusNoteStage;
    }
}
