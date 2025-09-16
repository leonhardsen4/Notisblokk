package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.model.Users;
import com.leonhardsen.notisblokk.view.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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

    public AnchorPane rootPane;
    public Label lblDataHora;
    public Label lblUsuario;
    public ImageView iconHome;
    public ImageView iconNotes;
    public ImageView iconContacts;
    public ImageView iconSketch;
    public ImageView iconCalendar;
    public ImageView iconCalculator;
    public ImageView iconSettings;
    public ImageView iconShutdown;
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

        iconHome.setOnMouseClicked(event -> DocumentView.openView(rootPane));
        iconNotes.setOnMouseClicked(event -> NotisblokkView.openView(rootPane));
        iconContacts.setOnMouseClicked(event -> KontakterView.openView(rootPane));
        iconSketch.setOnMouseClicked(event -> SkecthView.openView(rootPane));
        iconCalendar.setOnMouseClicked(event -> KalendarView.openView(rootPane));
        iconCalculator.setOnMouseClicked(event -> {
            try {
                KalkulatorView.openView(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        iconSettings.setOnMouseClicked(event -> {
            try {
                ChangePasswordView.ChangePasswordWindow(currentStage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        iconShutdown.setOnMouseClicked(event -> sairDoSistema());

        Tooltip tooltipHome = new Tooltip("Hjem");
        Tooltip tooltipNotes = new Tooltip("Notisblokk");
        Tooltip tooltipContacts = new Tooltip("Kontakter");
        Tooltip tooltipSketch = new Tooltip("Skisse");
        Tooltip tooltipCalendar = new Tooltip("Kalendar");
        Tooltip tooltipCalculator = new Tooltip("Kalkulator");
        Tooltip tooltipSettings = new Tooltip("Innstillinger");
        Tooltip tooltipShutdown = new Tooltip("Gå Ut");
        Tooltip.install(iconHome, tooltipHome);
        Tooltip.install(iconNotes, tooltipNotes);
        Tooltip.install(iconContacts, tooltipContacts);
        Tooltip.install(iconSketch, tooltipSketch);
        Tooltip.install(iconCalendar, tooltipCalendar);
        Tooltip.install(iconCalculator, tooltipCalculator);
        Tooltip.install(iconSettings, tooltipSettings);
        Tooltip.install(iconShutdown, tooltipShutdown);

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
