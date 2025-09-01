package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.EventsDAO;
import com.leonhardsen.notisblokk.model.Events;
import com.leonhardsen.notisblokk.utils.ConflitoHorarioException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {

    @FXML private TextField txtAssunto;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> cmbInicio;
    @FXML private ComboBox<String> cmbTermino;
    @FXML private TextField txtDuracao;
    @FXML public ColorPicker colorPicker;
    @FXML private javafx.scene.web.HTMLEditor txtDescricao;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML public ImageView imgSave;
    @FXML public ImageView imgDelete;

    private Events eventoParaEdicao;
    private final EventsDAO eventsDAO = new EventsDAO();
    public Stage currentStage;
    public KalendarController kalendarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboBoxes();
        configurarListeners();
        colorPicker.setValue(javafx.scene.paint.Color.WHITE);
        btnSave.setOnAction(e -> salvarEvento());
        btnDelete.setOnAction(e -> excluirEvento());
    }

    private void configurarComboBoxes() {
        for (int hora = 0; hora < 24; hora++) {
            for (int minuto = 0; minuto < 60; minuto += 30) {
                String horario = String.format("%02d:%02d", hora, minuto);
                cmbInicio.getItems().add(horario);
                cmbTermino.getItems().add(horario);
            }
        }
    }

    private void configurarListeners() {
        cmbInicio.valueProperty().addListener((obs, oldVal, newVal) -> calcularDuracao());
        cmbTermino.valueProperty().addListener((obs, oldVal, newVal) -> calcularDuracao());

        colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            String corHex = toHexString(newVal);
            btnSave.setStyle("-fx-background-color: " + corHex + ";");
        });
    }

    private void calcularDuracao() {
        if (cmbInicio.getValue() != null && cmbTermino.getValue() != null) {
            try {
                LocalTime inicio = LocalTime.parse(cmbInicio.getValue());
                LocalTime fim = LocalTime.parse(cmbTermino.getValue());

                if (fim.isBefore(inicio)) {
                    txtDuracao.setText("Horário inválido");
                    return;
                }

                long minutos = java.time.Duration.between(inicio, fim).toMinutes();
                long horas = minutos / 60;
                minutos = minutos % 60;
                txtDuracao.setText(String.format("%02d:%02d", horas, minutos));
            } catch (Exception e) {
                txtDuracao.setText("Erro");
            }
        }
    }

    public void setDataSelecionada(java.time.LocalDate data) {
        if (data != null) {
            datePicker.setValue(data);
        }
        preencherCamposPadrao();
    }

    private void preencherCamposPadrao() {
        txtAssunto.clear();
        cmbInicio.setValue("00:00");
        cmbTermino.setValue("23:30");
        calcularDuracao();
        txtDescricao.setHtmlText("");
        btnDelete.setVisible(false);

        // Não limpa a data se já estiver preenchida
        if (datePicker.getValue() == null) {
            datePicker.setValue(java.time.LocalDate.now());
        }
    }

    public void setEventoParaEdicao(Events evento) {
        this.eventoParaEdicao = evento;
        preencherCamposEdicao();
    }

    private void preencherCamposEdicao() {
        if (eventoParaEdicao != null) {
            txtAssunto.setText(eventoParaEdicao.getNome());
            datePicker.setValue(eventoParaEdicao.getData());
            cmbInicio.setValue(eventoParaEdicao.getHorarioInicialAsString());
            cmbTermino.setValue(eventoParaEdicao.getHorarioFinalAsString());
            calcularDuracao();
            txtDescricao.setHtmlText(eventoParaEdicao.getObservacoes() != null ? eventoParaEdicao.getObservacoes() : "");

            if (eventoParaEdicao.getCor() != null) {
                colorPicker.setValue(javafx.scene.paint.Color.web(eventoParaEdicao.getCor()));
            } else {
                colorPicker.setValue(javafx.scene.paint.Color.WHITE);
            }

            btnDelete.setVisible(true);

        }
    }

    private void salvarEvento() {
        try {
            validarCampos();

            LocalTime inicio = LocalTime.parse(cmbInicio.getValue());
            LocalTime fim = LocalTime.parse(cmbTermino.getValue());
            String observacoes = txtDescricao.getHtmlText();
            LocalDate dataEvento = datePicker.getValue();
            String corHex = toHexString(colorPicker.getValue());

            Events evento;
            if (eventoParaEdicao != null) {
                evento = eventoParaEdicao;
                evento.setNome(txtAssunto.getText());
                evento.setData(dataEvento);
                evento.setHorarioInicial(inicio);
                evento.setHorarioFinal(fim);
                evento.setCor(corHex);
                evento.setObservacoes(observacoes);
                eventsDAO.update(evento);
            } else {
                evento = new Events();
                evento.setNome(txtAssunto.getText());
                evento.setData(dataEvento);
                evento.setHorarioInicial(inicio);
                evento.setHorarioFinal(fim);
                evento.setCor(corHex);
                evento.setObservacoes(observacoes);

                List<Events> eventosExistentes = eventsDAO.findByDate(dataEvento);
                for (Events existente : eventosExistentes) {
                    if (evento.conflitaCom(existente)) {
                        throw new ConflitoHorarioException(
                                "Conflito de horário com: " + existente.getNome() +
                                        " (" + existente.getHorarioInicialAsString() + " - " +
                                        existente.getHorarioFinalAsString() + ")"
                        );
                    }
                }

                eventsDAO.save(evento);
            }

            fecharJanela();
            if (kalendarController != null) {
                kalendarController.atualizarCalendarioAposModificacao();
            }

        } catch (ConflitoHorarioException e) {
            showAlert("Conflito de Horário", e.getMessage());
        } catch (Exception e) {
            e.fillInStackTrace();
            showAlert("Erro", "Não foi possível salvar o evento: " + e.getMessage());
        }
    }

    private String toHexString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    private void excluirEvento() {
        if (eventoParaEdicao != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText("Excluir Evento");
            alert.setContentText("Tem certeza que deseja excluir o evento: " + eventoParaEdicao.getNome() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eventsDAO.delete(eventoParaEdicao.getId());
                    fecharJanela();
                    if (kalendarController != null) {
                        kalendarController.atualizarCalendarioAposModificacao();
                    }
                }
            });
        }
    }

    private void validarCampos() {
        if (txtAssunto.getText().isEmpty()) {
            throw new IllegalArgumentException("O assunto é obrigatório");
        }
        if (cmbInicio.getValue() == null || cmbTermino.getValue() == null) {
            throw new IllegalArgumentException("Os horários são obrigatórios");
        }
        if (datePicker.getValue() == null) {
            throw new IllegalArgumentException("A data é obrigatória");
        }

        LocalTime inicio = LocalTime.parse(cmbInicio.getValue());
        LocalTime fim = LocalTime.parse(cmbTermino.getValue());

        if (fim.isBefore(inicio) || fim.equals(inicio)) {
            throw new IllegalArgumentException("O horário de término deve ser após o horário de início");
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void setKalendarController(KalendarController kalendarController) {
        this.kalendarController = kalendarController;
    }

}
