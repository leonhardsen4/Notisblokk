package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.view.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    public BorderPane borderPane;
    public AnchorPane drawerPane;
    public AnchorPane statusBar;
    public Label lblDataHora;
    public Label lblUsuario;
    public Label lblNotisblokk;
    public Label lblKontakter;
    public Label lblSkecth;
    public Label lblKalender;
    public ImageView imgLogo;
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
                ChangePasswordView.ChangePasswordWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        lblNotisblokk.setOnMouseClicked(e -> NotisblokkView.openView(mainPane));

        lblKontakter.setOnMouseClicked(e-> KontakterView.openView(mainPane));

        lblSkecth.setOnMouseClicked(e -> SkecthView.openView(mainPane));

        lblKalender.setOnMouseClicked(e -> KalenderView.openView(mainPane));

        lblNotisblokk.setOnMouseEntered(event -> lblNotisblokk.setStyle("-fx-background-color: #4682B4"));

        lblNotisblokk.setOnMouseExited(event -> lblNotisblokk.setStyle("-fx-background-color:  #002d40"));

        lblKontakter.setOnMouseEntered(event -> lblKontakter.setStyle("-fx-background-color: #4682B4"));

        lblKontakter.setOnMouseExited(event -> lblKontakter.setStyle("-fx-background-color:  #002d40"));

        lblSkecth.setOnMouseEntered(event -> lblSkecth.setStyle("-fx-background-color: #4682B4"));

        lblSkecth.setOnMouseExited(event -> lblSkecth.setStyle("-fx-background-color:  #002d40"));

        lblKalender.setOnMouseEntered(event -> lblKalender.setStyle("-fx-background-color: #4682B4"));

        lblKalender.setOnMouseExited(event -> lblKalender.setStyle("-fx-background-color:  #002d40"));

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
}
