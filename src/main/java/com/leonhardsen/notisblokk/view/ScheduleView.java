package com.leonhardsen.notisblokk.view;

import com.leonhardsen.notisblokk.controller.ScheduleController;
import com.leonhardsen.notisblokk.model.Scheduling;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ScheduleView {

    public static void openView(Scheduling schedule, Stage parentStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ScheduleView.class.getResource("Schedule.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 540, 450);
        ScheduleController scheduleController = fxmlLoader.getController();
        Stage scheduleStage = new Stage();
        scheduleController.setCurrentStage(scheduleStage);
        scheduleStage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(ScheduleView.class.getResourceAsStream("/com/leonhardsen/notisblokk/image/puzzle-game.png"))));
        scheduleController.setScheduleItem(schedule);
        scheduleStage.initOwner(parentStage);
        scheduleStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        scheduleStage.setResizable(true);
        scheduleStage.setMinWidth(540);
        scheduleStage.setMinHeight(450);
        scheduleStage.setTitle("Agendamento");
        scheduleStage.setScene(scene);
        scheduleStage.show();
    }
}
