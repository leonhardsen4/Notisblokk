package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.KalendarController;
import com.leonhardsen.notisblokk.controller.ScheduleController;
import com.leonhardsen.notisblokk.model.Events;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class ScheduleView {

    private static KalendarController kalendarController;

    public static void setKalendarController(KalendarController controller) {
        kalendarController = controller;
    }

    public static void openNewEventView(Stage parentStage) throws IOException {
        openNewEventView(null, parentStage);
    }

    public static void openNewEventView(java.time.LocalDate dataSelecionada, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ScheduleView.class.getResource("Schedule.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        ScheduleController scheduleController = fxmlLoader.getController();

        if (kalendarController != null) {
            scheduleController.setKalendarController(kalendarController);
        }

        if (dataSelecionada != null) {
            scheduleController.setDataSelecionada(dataSelecionada);
        }

        Stage scheduleStage = createStage(parentStage, scene);
        scheduleStage.setTitle("Novo Evento");
        scheduleStage.show();
    }

    public static void openEditEventView(Events evento, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ScheduleView.class.getResource("Schedule.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 450);
        ScheduleController scheduleController = fxmlLoader.getController(); // 🔥 AGORA AQUI

        if (kalendarController != null) {
            scheduleController.setKalendarController(kalendarController);
        }

        scheduleController.setEventoParaEdicao(evento);

        Stage scheduleStage = createStage(parentStage, scene);
        scheduleStage.setTitle("Editar Evento: " + evento.getNome());
        scheduleStage.show();
    }

    private static Stage createStage(Stage parentStage, Scene scene) {
        Stage scheduleStage = new Stage();
        scheduleStage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(
                ScheduleView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png")
        )));
        scheduleStage.initOwner(parentStage);
        scheduleStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        scheduleStage.setResizable(true);
        scheduleStage.setMinWidth(650);
        scheduleStage.setMinHeight(450);
        scheduleStage.setScene(scene);
        return scheduleStage;
    }
}