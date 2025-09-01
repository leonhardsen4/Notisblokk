package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.EventsDAO;
import com.leonhardsen.notisblokk.model.Events;
import com.leonhardsen.notisblokk.view.ScheduleView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KalendarController implements Initializable {

    @FXML public AnchorPane rootPane;
    @FXML public Button btnAnterior;
    @FXML public Button btnProximo;
    @FXML public GridPane gridCabecalho;
    @FXML public Button btnNovoEvento;
    @FXML public TextField txtPesquisa;
    @FXML private GridPane gridCalendario;
    @FXML private Label lblMesAno;
    @FXML private VBox vboxListaEventos;

    private LocalDate dataAtual;
    private LocalDate diaSelecionado;
    public Stage currentStage;

    private final EventsDAO eventsDAO = new EventsDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataAtual = LocalDate.now();
        configurarGrid();
        atualizarCalendario();

        btnNovoEvento.setOnAction(e -> abrirTelaNovoEvento());

        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                if (diaSelecionado != null) {
                    onDiaSelecionado(diaSelecionado);
                }
            } else {
                realizarPesquisa(newValue.trim());
            }
        });

    }

    private void configurarGrid() {
        for (RowConstraints row : gridCalendario.getRowConstraints()) {
            row.setVgrow(Priority.SOMETIMES);
            row.setMinHeight(60);
            row.setPrefHeight(80);
        }

        for (ColumnConstraints col : gridCalendario.getColumnConstraints()) {
            col.setHgrow(Priority.ALWAYS);
            col.setMinWidth(80);
            col.setPrefWidth(120);
        }
    }

    private void realizarPesquisa(String termoPesquisa) {
        vboxListaEventos.getChildren().clear();

        Label titulo = new Label("Resultados da pesquisa: '" + termoPesquisa + "'");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5px;");
        vboxListaEventos.getChildren().add(titulo);

        List<Events> eventosEncontrados = eventsDAO.searchEvents(termoPesquisa);

        if (eventosEncontrados.isEmpty()) {
            Label lblNenhumResultado = new Label("Nenhum evento encontrado para: '" + termoPesquisa + "'");
            lblNenhumResultado.setStyle("-fx-font-style: italic; -fx-padding: 10px;");
            vboxListaEventos.getChildren().add(lblNenhumResultado);
        } else {

            eventosEncontrados.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Events::getData))
                    .entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        LocalDate data = entry.getKey();
                        List<Events> eventosDaData = entry.getValue();
                        eventosDaData.sort(Comparator.comparing(Events::getHorarioInicial));

                        Label dataLabel = new Label("Data: " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        dataLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8px 5px 3px 5px; -fx-background-color: #f0f0f0;");
                        dataLabel.setMaxWidth(Double.MAX_VALUE);
                        vboxListaEventos.getChildren().add(dataLabel);


                        for (Events evento : eventosDaData) {
                            adicionarEventoNaListaPesquisa(evento);
                        }
                    });
        }
    }

    private void adicionarEventoNaListaPesquisa(Events evento) {
        HBox eventoItem = new HBox(10);
        eventoItem.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-width: 0 0 1px 0;");
        eventoItem.setAlignment(Pos.CENTER_LEFT);

        eventoItem.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editarEvento(evento);
            }
        });

        Pane colorIndicator = new Pane();
        colorIndicator.setPrefSize(15, 15);
        colorIndicator.setStyle("-fx-background-radius: 3px;");

        String corEvento = evento.getCor() != null ? evento.getCor() : "#ffffff";
        colorIndicator.setStyle(colorIndicator.getStyle() +
                "-fx-background-color: " + corEvento + "; " +
                "-fx-border-color: " + escurecerCor(corEvento) + "; " +
                "-fx-border-width: 1px;");

        // Adicionar horário
        Label horarioLabel = new Label(evento.getHorarioInicialAsString() + " - " + evento.getHorarioFinalAsString());
        horarioLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 80px;");

        // Adicionar nome do evento
        Label descLabel = new Label(evento.getNome());
        descLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        // Adicionar data (para contexto na pesquisa)
        Label dataLabel = new Label(evento.getData().format(DateTimeFormatter.ofPattern("dd/MM")));
        dataLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-min-width: 50px;");

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-font-size: 10px; -fx-padding: 2px 5px;");
        btnEditar.setOnAction(e -> editarEvento(evento));

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-font-size: 10px; -fx-padding: 2px 5px; -fx-background-color: #ff4444; -fx-text-fill: white;");
        btnExcluir.setOnAction(e -> excluirEvento(evento));

        eventoItem.getChildren().addAll(colorIndicator, dataLabel, horarioLabel, descLabel, btnEditar, btnExcluir);
        vboxListaEventos.getChildren().add(eventoItem);
    }

    @FXML
    private void mesAnterior() {
        dataAtual = dataAtual.minusMonths(1);
        atualizarCalendario();
    }

    @FXML
    private void proximoMes() {
        dataAtual = dataAtual.plusMonths(1);
        atualizarCalendario();
    }

    public void atualizarCalendario() {
        String nomeMes = dataAtual.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                Locale.forLanguageTag("pt-BR")
        );
        nomeMes = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
        lblMesAno.setText(nomeMes + " " + dataAtual.getYear());
        gridCalendario.getChildren().clear();

        LocalDate firstDayOfMonth = dataAtual.withDayOfMonth(1);
        int daysInMonth = dataAtual.lengthOfMonth();
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int daysInPreviousMonth = dataAtual.minusMonths(1).lengthOfMonth();

        startDayOfWeek = startDayOfWeek % 7;

        int day = 1;
        int dayOfPreviousMonth = daysInPreviousMonth - startDayOfWeek + 1;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                BorderPane cellPane = new BorderPane();
                cellPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 2px;");

                if ((row == 0 && col < startDayOfWeek) || day > daysInMonth) {
                    if (row == 0 && col < startDayOfWeek) {
                        createDayCell(cellPane, String.valueOf(dayOfPreviousMonth), true, false);
                        dayOfPreviousMonth++;
                    } else {
                        createDayCell(cellPane, String.valueOf(day - daysInMonth), false, true);
                        day++;
                    }
                } else {
                    LocalDate cellDate = dataAtual.withDayOfMonth(day);
                    createDayCell(cellPane, String.valueOf(day), false, false);
                    adicionarEventosReais(cellPane, cellDate); // ✅ MANTÉM (já está simplificado)

                    final int currentDay = day;
                    cellPane.setOnMouseClicked(event -> onDiaSelecionado(dataAtual.withDayOfMonth(currentDay)));
                    day++;
                }
                gridCalendario.add(cellPane, col, row);
            }
        }

        if (diaSelecionado != null) {
            onDiaSelecionado(diaSelecionado);
        }
    }

    private void onDiaSelecionado(LocalDate data) {
        this.diaSelecionado = data;
        vboxListaEventos.getChildren().clear();

        txtPesquisa.clear();

        Label titulo = new Label("Eventos para " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5px;");
        vboxListaEventos.getChildren().add(titulo);

        List<Events> eventos = eventsDAO.findByDate(data);
        for (Events evento : eventos) {
            adicionarEventoNaLista(evento);
        }
    }

    private void createDayCell(BorderPane cellPane, String dayNumber, boolean isPreviousMonth, boolean isNextMonth) {
        Label dayLabel = new Label(dayNumber);

        if (isPreviousMonth || isNextMonth) {
            dayLabel.setStyle("-fx-font-weight: normal; -fx-text-fill: #aaaaaa; -fx-padding: 2px;");
            cellPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 2px; -fx-background-color: #f8f8f8;");
            cellPane.setDisable(true);
        } else {
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2px; -fx-alignment: center-left;");

            boolean isToday = dataAtual.withDayOfMonth(Integer.parseInt(dayNumber)).equals(LocalDate.now());
            if (isToday) {
                dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3; -fx-padding: 2px;");
                cellPane.setStyle("-fx-border-color: #2196F3; -fx-border-width: 1px; -fx-padding: 2px; -fx-background-color: #e3f2fd;");
            }
        }

        cellPane.setTop(dayLabel);

        if (!isPreviousMonth && !isNextMonth) {
            FlowPane eventosFlowPane = new FlowPane();
            eventosFlowPane.setStyle("-fx-padding: 2px; -fx-hgap: 2px; -fx-vgap: 2px;");
            eventosFlowPane.setPrefHeight(50.0);
            cellPane.setCenter(eventosFlowPane);
        }
    }

    private void adicionarEventosReais(BorderPane cellPane, LocalDate data) {
        FlowPane flowPane = (FlowPane) cellPane.getCenter();
        if (flowPane == null) return;

        List<Events> eventos = eventsDAO.findByDate(data);
        for (Events evento : eventos) {
            adicionarEventoVisual(flowPane, evento);
        }
    }

    private void adicionarEventoNaLista(Events evento) {
        HBox eventoItem = new HBox(10);
        eventoItem.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-width: 0 0 1px 0;");
        eventoItem.setAlignment(Pos.CENTER_LEFT);

        eventoItem.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editarEvento(evento);
            }
        });

        Pane colorIndicator = new Pane();
        colorIndicator.setPrefSize(15, 15);
        colorIndicator.setStyle("-fx-background-radius: 3px;");

        String corEvento = evento.getCor() != null ? evento.getCor() : "#ffffff";
        colorIndicator.setStyle(colorIndicator.getStyle() +
                "-fx-background-color: " + corEvento + "; " +
                "-fx-border-color: " + escurecerCor(corEvento) + "; " +
                "-fx-border-width: 1px;");

        Label horarioLabel = new Label(evento.getHorarioInicialAsString() + " - " + evento.getHorarioFinalAsString());
        horarioLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 80px;");

        Label descLabel = new Label(evento.getNome());
        descLabel.setStyle("-fx-font-size: 12px;");

        Label corLabel = new Label(corEvento);
        corLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-min-width: 70px;");

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-font-size: 10px; -fx-padding: 2px 5px;");
        btnEditar.setOnAction(e -> editarEvento(evento));

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-font-size: 10px; -fx-padding: 2px 5px; -fx-background-color: #ff4444; -fx-text-fill: white;");
        btnExcluir.setOnAction(e -> excluirEvento(evento));

        eventoItem.getChildren().addAll(colorIndicator, horarioLabel, descLabel, btnEditar, btnExcluir);
        vboxListaEventos.getChildren().add(eventoItem);
    }

    private void abrirTelaNovoEvento() {
        try {
            ScheduleView.setKalendarController(this);

            if (diaSelecionado != null) {
                ScheduleView.openNewEventView(diaSelecionado, currentStage);
            } else {
                ScheduleView.openNewEventView(currentStage);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            showAlert("Erro", "Não foi possível abrir a tela de agendamento.");
        }
    }

    private void editarEvento(Events evento) {
        try {
            ScheduleView.setKalendarController(this);
            ScheduleView.openEditEventView(evento, currentStage);
        } catch (Exception e) {
            e.fillInStackTrace();
            showAlert("Erro", "Não foi possível abrir a tela de edição.");
        }
    }

    private void excluirEvento(Events evento) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir Evento");
        alert.setContentText("Tem certeza que deseja excluir o evento: " + evento.getNome() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventsDAO.delete(evento.getId());
            atualizarCalendario();
            showAlert("Sucesso", "Evento excluído com sucesso!");
        }
    }

    public void atualizarCalendarioAposModificacao() {
        atualizarCalendario();
        if (diaSelecionado != null) {
            onDiaSelecionado(diaSelecionado);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void adicionarEventoVisual(FlowPane flowPane, Events evento) {
        StackPane eventoPane = new StackPane();
        eventoPane.setPrefSize(20, 20);
        eventoPane.setStyle("-fx-background-radius: 3px; -fx-border-radius: 3px; -fx-border-width: 1px;");

        Label horaLabel = new Label(evento.getHorarioInicialAsString().substring(0, 2));
        horaLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: #333; -fx-font-weight: bold;");
        horaLabel.setPadding(new Insets(1));
        eventoPane.getChildren().add(horaLabel);

        Tooltip tooltip = new Tooltip();
        tooltip.setText(evento.getNome() + " - " + evento.getHorarioInicialAsString() + " às " + evento.getHorarioFinalAsString());

        // 🔥 USAR A COR SALVA NO EVENTO
        String corEstilo = "-fx-background-color: " + evento.getCor() + "; -fx-border-color: " + escurecerCor(evento.getCor()) + ";";
        eventoPane.setStyle(eventoPane.getStyle() + corEstilo);

        Tooltip.install(eventoPane, tooltip);
        flowPane.getChildren().add(eventoPane);
    }

    private String escurecerCor(String corHex) {
        try {
            javafx.scene.paint.Color cor = javafx.scene.paint.Color.web(corHex);
            javafx.scene.paint.Color corEscura = cor.deriveColor(0, 1, 0.7, 1); // 70% mais escura
            return toHexString(corEscura);
        } catch (Exception e) {
            return "#000000";
        }
    }

    private String toHexString(javafx.scene.paint.Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }


}