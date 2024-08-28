package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.view.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

public class MainScreenController implements Initializable {

    public AnchorPane mainPane;
    public AnchorPane drawerPane;
    public AnchorPane statusBar;
    public Label lblDataHora;
    public Label lblUsuario;
    public Label lblNotisblokk;
    public Label lblKontakter;
    public Label lblSkecth;
    public ImageView imgBurger;
    public Stage currentStage;
    public Users usr;


    boolean isOpen;
    public static MainScreenController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = this;

        Timeline clock = getClock();
        clock.play();

        lblUsuario.setOnMouseClicked(e -> {
            try {
                ChangePasswordView.ChangePasswordWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        lblNotisblokk.setOnMouseClicked(e -> {
            NotisblokkView.openView(mainPane);
            closeSideMenu();
        });

        lblKontakter.setOnMouseClicked(e-> {
            KontakterView.openView(mainPane);
            closeSideMenu();
        });

        lblSkecth.setOnMouseClicked(e -> {
            SkecthView.openView(mainPane);
            closeSideMenu();
        });

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), drawerPane);
        translateTransition.setToX(-180);
        translateTransition.play();
        isOpen = false;

        imgBurger.setOnMouseClicked(e -> {
            if (isOpen) {
                closeSideMenu();
            } else {
                openSideMenu();
            }
        });

        mainPane.setOnMouseClicked(e -> {
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
        usr = user;
        lblUsuario.setText("USUÁRIO: " + usr.getUser());
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
