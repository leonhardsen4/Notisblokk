package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.dao.StatusDAO;
import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Status;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.view.NoteView;
import com.leonhardsen.notisblokk.view.StatusView;
import com.leonhardsen.notisblokk.view.TagView;
import com.leonhardsen.notisblokk.view.TemplateView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador principal da aplicação Notisblokk.
 * Responsável por gerenciar a interface principal que exibe tags e notas,
 * coordenando as operações de filtragem e navegação entre as views.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class NotisblokkController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML public AnchorPane rootPane;
    @FXML public TextField txtPesquisa;
    @FXML public TextField txtPesquisaNotas;
    @FXML public Button btnTag;
    @FXML public Button btnNote;
    @FXML public ListView<Tags> listTag;
    @FXML public TableView<Notes> tblNote;
    @FXML public TableColumn<Notes, String> colTitulo;
    @FXML public TableColumn<Notes, String> colStatus;
    @FXML public TableColumn<Notes, String> colData;
    @FXML public TableColumn<Notes, String> colUltimaModificacao;
    @FXML public TableColumn<Notes, String> colDeadline;
    @FXML public ComboBox<String> cmbFiltro;
    @FXML public Button btnManageTemplates;

    // ========== ATRIBUTOS ==========
    /** Instância singleton do controlador para comunicação entre telas */
    public static NotisblokkController instance;

    /** DAOs para acesso aos dados */
    private NotesDAO notesDAO;
    private TagsDAO tagsDAO;
    private StatusDAO statusDAO;

    /** Referência para o stage atual */
    private Stage currentStage;

    /** Flag para prevenir loops durante atualizações programáticas */
    private boolean isUpdatingProgrammatically = false;

    /** Timer para verificações periódicas de prazo */
    private Timer deadlineTimer;

    // ========== INICIALIZAÇÃO ==========

    /**
     * Inicializa o controlador e configura todos os componentes da interface.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        initializeDAOs();
        setupInterface();
        loadInitialData();
        startDeadlineMonitoring();

    }

    /**
     * Inicializa os objetos DAO para acesso aos dados.
     */
    private void initializeDAOs() {
        try {
            notesDAO = new NotesDAO();
            tagsDAO = new TagsDAO();
            statusDAO = new StatusDAO();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar DAOs: " + e.getMessage());
            showAlert("Erro de Inicialização", "Não foi possível conectar ao banco de dados.");
        }
    }

    /**
     * Configura todos os componentes da interface.
     */
    private void setupInterface() {
        setupTableView();
        setupEventListeners();
        setupButtonActions();
        setupFilterComboBox();
    }

    /**
     * Carrega os dados iniciais na interface.
     */
    private void loadInitialData() {
        refreshTagsList();
        refreshNotesTable();
    }

    // ========== CONFIGURAÇÃO DA TABELA ==========

    /**
     * Configura a tabela de notas e suas colunas.
     */
    private void setupTableView() {
        // Configuração das colunas
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colUltimaModificacao.setCellValueFactory(new PropertyValueFactory<>("ultimaModificacao"));
        colUltimaModificacao.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUltimaModificacaoFormatada())
        );
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        // Configuração da coluna de status com bolinhas coloridas
        setupStatusColumn();

        // Configurações gerais da tabela
        tblNote.setEditable(false);

        // Configuração para duplo clique nas linhas
        tblNote.setRowFactory(tableView -> {
            TableRow<Notes> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleTableRowDoubleClick(row.getItem());
                }
            });
            return row;
        });
    }

    /**
     * Manipula o duplo clique em uma linha da tabela de notas.
     */
    private void handleTableRowDoubleClick(Notes note) {
        if (note != null) {
            Tags associatedTag = tagsDAO.findID(note.getId_tag());
            openNoteView(associatedTag, note);
        }
    }

    /**
     * Configura a coluna de status com bolinhas coloridas.
     */
    private void setupStatusColumn() {
        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Circle circle = new Circle(6);
            private final HBox container = new HBox(5);
            private final Label label = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(circle, label);
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    label.setText(status);

                    // Busca a cor correspondente ao status
                    String colorHex = getColorForStatus(status);

                    if (colorHex != null) {
                        try {
                            Color color = Color.web(colorHex);
                            circle.setFill(color);
                            circle.setStroke(Color.BLACK);
                            circle.setStrokeWidth(0.5);
                        } catch (Exception e) {
                            circle.setFill(Color.GRAY); // Cor padrão em caso de erro
                        }
                    } else {
                        circle.setFill(Color.GRAY); // Cor padrão se não encontrar
                    }

                    setGraphic(container);
                    setText(null);
                }
            }
        });
    }

    /**
     * Obtém a cor hexadecimal para um determinado status.
     */
    private String getColorForStatus(String statusName) {
        try {
            ObservableList<Status> allStatus = statusDAO.getAll();
            for (Status status : allStatus) {
                if (status.getStatus().equals(statusName)) {
                    return status.getCor();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cor do status: " + e.getMessage());
        }
        return null; // Retorna null se não encontrar
    }


    // ========== CONFIGURAÇÃO DE EVENTOS ==========

    /**
     * Configura todos os listeners de eventos da interface.
     */
    private void setupEventListeners() {
        setupTagListeners();
        setupFilterListeners();
        setupSearchListeners();
    }

    /**
     * Configura os eventos relacionados à lista de tags.
     */
    private void setupTagListeners() {
        // Listener para seleção de tag
        listTag.getSelectionModel().selectedItemProperty().addListener((obs, oldTag, newTag) -> {
            if (!isUpdatingProgrammatically) {
                refreshNotesTable();
                updateNoteButtonState();
            }
        });

        // Listener para cliques na lista
        listTag.setOnMouseClicked(this::handleTagListClick);

        // Seleciona a primeira tag por padrão
        Platform.runLater(() -> {
            if (!listTag.getItems().isEmpty()) {
                listTag.getSelectionModel().selectFirst();
            }
        });
    }

    /**
     * Manipula os cliques na lista de tags.
     */
    private void handleTagListClick(MouseEvent event) {
        Tags selectedTag = listTag.getSelectionModel().getSelectedItem();

        if (event.getClickCount() == 1) {
            updateNoteButtonState();
            refreshNotesTable();
        } else if (event.getClickCount() == 2 && selectedTag != null) {
            openTagView(selectedTag);
        }
    }

    /**
     * Configura os listeners dos filtros.
     */
    private void setupFilterListeners() {
        // Listener do combo de filtro
        cmbFiltro.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!isUpdatingProgrammatically && newValue != null) {
                clearTagSelectionAndRefresh();
            }
        });

        // Duplo clique no combo abre gerenciamento de status
        cmbFiltro.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openStatusView();
            }
        });
    }

    /**
     * Configura os listeners dos campos de pesquisa.
     */
    private void setupSearchListeners() {
        // Pesquisa de notas
        txtPesquisaNotas.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!isUpdatingProgrammatically) {
                clearTagSelectionAndRefresh();
            }
        });

        // Pesquisa de tags
        txtPesquisa.textProperty().addListener((obs, oldValue, newValue) -> refreshTagsList());
    }

    /**
     * Limpa a seleção de tag e atualiza a tabela (usado em filtros globais).
     */
    private void clearTagSelectionAndRefresh() {
        isUpdatingProgrammatically = true;
        listTag.getSelectionModel().clearSelection();
        isUpdatingProgrammatically = false;
        refreshNotesTable();
        updateNoteButtonState();
    }

    /**
     * Configura o combo box de filtros com os status disponíveis.
     */
    public void setupFilterComboBox() {
//        try {
//            String currentSelection = cmbFiltro.getValue();
//            cmbFiltro.setItems(loadFilterOptions());
//
//            // Tenta restaurar a seleção anterior
//            if (currentSelection != null && cmbFiltro.getItems().contains(currentSelection)) {
//                cmbFiltro.setValue(currentSelection);
//            } else {
//                cmbFiltro.getSelectionModel().selectFirst();
//            }
//        } catch (Exception e) {
//            System.err.println("Erro ao configurar filtros: " + e.getMessage());
//        }
        setupFilterComboBoxWithColors();
    }

    /**
     * Configura o ComboBox de filtros com bolinhas coloridas.
     */
    private void setupFilterComboBoxWithColors() {
        try {
            String currentSelection = cmbFiltro.getValue();
            ObservableList<String> filterOptions = loadFilterOptions();

            // Configura a cell factory para exibir bolinhas coloridas
            cmbFiltro.setCellFactory(listView -> new ListCell<>() {
                private final Circle circle = new Circle(5);
                private final HBox container = new HBox(5);
                private final Label label = new Label();

                {
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.getChildren().addAll(circle, label);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        label.setText(item);

                        if ("MOSTRAR TODOS".equals(item)) {
                            circle.setFill(Color.TRANSPARENT); // Sem cor para "MOSTRAR TODOS"
                            circle.setStroke(Color.TRANSPARENT);
                        } else {
                            String colorHex = getColorForStatus(item);
                            if (colorHex != null) {
                                try {
                                    Color color = Color.web(colorHex);
                                    circle.setFill(color);
                                    circle.setStroke(Color.BLACK);
                                    circle.setStrokeWidth(0.5);
                                } catch (Exception e) {
                                    circle.setFill(Color.GRAY);
                                }
                            } else {
                                circle.setFill(Color.GRAY);
                            }
                        }

                        setGraphic(container);
                        setText(null);
                    }
                }
            });

            // Configura a button cell para mostrar a seleção atual
            cmbFiltro.setButtonCell(new ListCell<>() {
                private final Circle circle = new Circle(5);
                private final HBox container = new HBox(5);
                private final Label label = new Label();

                {
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.getChildren().addAll(circle, label);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        label.setText(item);

                        if ("MOSTRAR TODOS".equals(item)) {
                            circle.setFill(Color.TRANSPARENT);
                            circle.setStroke(Color.TRANSPARENT);
                        } else {
                            String colorHex = getColorForStatus(item);
                            if (colorHex != null) {
                                try {
                                    Color color = Color.web(colorHex);
                                    circle.setFill(color);
                                    circle.setStroke(Color.BLACK);
                                    circle.setStrokeWidth(0.5);
                                } catch (Exception e) {
                                    circle.setFill(Color.GRAY);
                                }
                            } else {
                                circle.setFill(Color.GRAY);
                            }
                        }

                        setGraphic(container);
                        setText(null);
                    }
                }
            });

            cmbFiltro.setItems(filterOptions);

            // Tenta restaurar a seleção anterior
            if (currentSelection != null && filterOptions.contains(currentSelection)) {
                cmbFiltro.setValue(currentSelection);
            } else {
                cmbFiltro.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("Erro ao configurar filtros com cores: " + e.getMessage());
        }
    }

    // ========== CONFIGURAÇÃO DOS BOTÕES ==========

    /**
     * Configura as ações dos botões da interface.
     */
    private void setupButtonActions() {
        // Botão para gerenciar tags
        btnTag.setOnMouseClicked(event -> openTagView(null));

        // Botão para criar notas
        btnNote.setOnMouseClicked(event -> handleCreateNoteAction());

        // Botão para gerenciar templates
        btnManageTemplates.setOnAction(event -> openTemplateView());

        // Estado inicial do botão de nota
        updateNoteButtonState();
    }

    /**
     * Manipula a ação de criação de nova nota.
     */
    private void handleCreateNoteAction() {
        Tags selectedTag = listTag.getSelectionModel().getSelectedItem();
        if (selectedTag != null) {
            openNoteView(selectedTag, null);
        } else {
            showAlert("Seleção Obrigatória", "Selecione uma tag para criar uma nova nota.");
        }
    }

    /**
     * Atualiza o estado do botão de criar nota baseado na seleção de tag.
     */
    private void updateNoteButtonState() {
        boolean hasSelectedTag = listTag.getSelectionModel().getSelectedItem() != null;
        btnNote.setDisable(!hasSelectedTag);
    }

    // ========== MÉTODOS DE ATUALIZAÇÃO DE DADOS ==========

    /**
     * Atualiza a tabela de notas aplicando todos os filtros ativos.
     * Este é o método principal de filtragem da aplicação.
     */
    public void refreshNotesTable() {
        try {
            String statusFilter = cmbFiltro.getValue();
            String searchText = txtPesquisaNotas.getText();
            Tags selectedTag = listTag.getSelectionModel().getSelectedItem();

            // Obtém lista base de notas
            ObservableList<Notes> baseNotes = getBaseNotesList(selectedTag);

            // Aplica filtros
            ObservableList<Notes> filteredNotes = applyFilters(baseNotes, statusFilter, searchText);

            // Atualiza a interface
            tblNote.setItems(filteredNotes);
            updateNoteButtonState();

        } catch (Exception e) {
            System.err.println("Erro ao atualizar tabela de notas: " + e.getMessage());
            tblNote.setItems(FXCollections.observableArrayList());
        }
    }

    /**
     * Obtém a lista base de notas dependendo da tag selecionada.
     */
    private ObservableList<Notes> getBaseNotesList(Tags selectedTag) {
        if (selectedTag != null) {
            return notesDAO.getAll(selectedTag.getId(), "MOSTRAR TODOS");
        } else {
            return notesDAO.getAll();
        }
    }

    /**
     * Aplica os filtros de status e pesquisa sobre a lista de notas.
     */
    private ObservableList<Notes> applyFilters(ObservableList<Notes> notes, String statusFilter, String searchText) {
        return notes.stream()
                .filter(note -> applyStatusFilter(note, statusFilter))
                .filter(note -> applySearchFilter(note, searchText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Aplica o filtro de status em uma nota.
     */
    private boolean applyStatusFilter(Notes note, String statusFilter) {
        if (statusFilter == null || "MOSTRAR TODOS".equals(statusFilter)) {
            return true;
        }
        return statusFilter.equals(note.getStatus());
    }

    /**
     * Aplica o filtro de pesquisa textual em uma nota.
     */
    private boolean applySearchFilter(Notes note, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }

        String lowercaseSearch = searchText.toLowerCase();

        // Busca no título
        if (note.getTitulo() != null && note.getTitulo().toLowerCase().contains(lowercaseSearch)) {
            return true;
        }

        // Busca no conteúdo (array de bytes)
        if (note.getRelato() != null) {
            String content = new String(note.getRelato()).toLowerCase();
            return content.contains(lowercaseSearch);
        }

        return false;
    }

    /**
     * Atualiza a lista de tags aplicando o filtro de pesquisa.
     * Mantém a seleção atual se a tag ainda estiver na lista filtrada.
     */
    public void refreshTagsList() {
        try {
            Tags currentSelectedTag = listTag.getSelectionModel().getSelectedItem();
            String searchText = txtPesquisa.getText();

            ObservableList<Tags> tags = (searchText == null || searchText.trim().isEmpty()) ?
                    tagsDAO.getAll() : tagsDAO.search(searchText.trim());

            listTag.setItems(tags);

            // Restaura seleção se a tag ainda estiver presente
            if (currentSelectedTag != null && tags.contains(currentSelectedTag)) {
                isUpdatingProgrammatically = true;
                listTag.getSelectionModel().select(currentSelectedTag);
                isUpdatingProgrammatically = false;
            } else if (!tags.isEmpty()) {
                // Se não conseguiu restaurar, seleciona a primeira
                Platform.runLater(() -> {
                    isUpdatingProgrammatically = true;
                    listTag.getSelectionModel().selectFirst();
                    isUpdatingProgrammatically = false;
                });
            }

        } catch (Exception e) {
            System.err.println("Erro ao atualizar lista de tags: " + e.getMessage());
            listTag.setItems(FXCollections.observableArrayList());
        }
    }

    // ========== MÉTODOS DE NAVEGAÇÃO ==========

    /**
     * Abre a view de criação/edição de nota.
     */
    private void openNoteView(Tags tag, Notes note) {
        try {
            String status = note != null ? note.getStatus() : null;
            NoteView.openView(tag, note, status, getCurrentStage());
        } catch (IOException e) {
            System.err.println("Erro ao abrir view de nota: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a janela de edição de nota.");
        }
    }

    /**
     * Abre a view de criação/edição de tag.
     */
    private void openTagView(Tags tag) {
        try {
            TagView.openView(tag, getCurrentStage());
        } catch (IOException e) {
            System.err.println("Erro ao abrir view de tag: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a janela de gerenciamento de tags.");
        }
    }

    /**
     * Abre a view de gerenciamento de templates.
     */
    private void openTemplateView() {
        try {
            TemplateView.openView(getCurrentStage());
        } catch (IOException e) {
            System.err.println("Erro ao abrir view de templates: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a janela de gerenciamento de templates.");
        }
    }

    /**
     * Abre a view de gerenciamento de status.
     * Após o fechamento, atualiza os filtros e a tabela.
     */
    private void openStatusView() {
        try {
            StatusView.openView(getCurrentStage());
            // Atualiza interface após possíveis modificações
            Platform.runLater(() -> {
                setupFilterComboBox();
                refreshNotesTable();
            });
        } catch (IOException e) {
            System.err.println("Erro ao abrir view de status: " + e.getMessage());
            showAlert("Erro", "Não foi possível abrir a janela de gerenciamento de status.");
        }
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Cria a lista de opções para o combo box de filtros.
     * Inclui "MOSTRAR TODOS" e todos os status cadastrados.
     */
    public ObservableList<String> loadFilterOptions() {
        try {
            ObservableList<Status> statusList = statusDAO.getAll();
            ObservableList<String> filterList = FXCollections.observableArrayList();

            filterList.add("MOSTRAR TODOS");
            statusList.forEach(status -> filterList.add(status.getStatus()));

            return filterList;
        } catch (Exception e) {
            System.err.println("Erro ao carregar opções de filtro: " + e.getMessage());
            // Retorna lista padrão em caso de erro
            return FXCollections.observableArrayList("MOSTRAR TODOS", "A RESOLVER", "EM ANDAMENTO", "RESOLVIDO");
        }
    }

    /**
     * Exibe um alerta informativo para o usuário.
     */
    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Obtém o stage atual da aplicação.
     */
    private Stage getCurrentStage() {
        return currentStage != null ? currentStage : (Stage) rootPane.getScene().getWindow();
    }

    // ========== MONITORAMENTO DE PRAZOS ==========

    /**
     * Inicia o monitoramento automático de prazos das notas.
     * Executa verificações periódicas para identificar deadlines próximos.
     */
    private void startDeadlineMonitoring() {
        deadlineTimer = new Timer("DeadlineMonitor", true);
        deadlineTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        checkUpcomingDeadlines();
                    } catch (Exception e) {
                        System.err.println("Erro na verificação de prazos: " + e.getMessage());
                    }
                });
            }
        }, 5000); // Primeira execução em 5 segundos

        // Verificações posteriores a cada hora
        deadlineTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        checkUpcomingDeadlines();
                    } catch (Exception e) {
                        System.err.println("Erro na verificação periódica de prazos: " + e.getMessage());
                    }
                });
            }
        }, 3600000, 3600000); // Primeira execução em 1 hora, depois a cada hora

    }

    /**
     * Verifica prazos próximos e exibe notificações se necessário.
     * TODO: Implementar lógica específica de notificação baseada nas regras de negócio.
     */
    private void checkUpcomingDeadlines() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate alertThreshold = today.plusDays(3);

            System.out.println("Verificando deadlines - Hoje: " + today + ", Limite: " + alertThreshold);

            ObservableList<Notes> allNotes = notesDAO.getAll();
            List<Notes> urgentNotes = new ArrayList<>();

            for (Notes note : allNotes) {
                if (note.getDeadline() != null && !note.getDeadline().trim().isEmpty()) {
                    System.out.println("Verificando nota: " + note.getTitulo() + " - Deadline: " + note.getDeadline());
                    try {
                        LocalDate deadline = LocalDate.parse(note.getDeadline(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                        // Verifica se está dentro do prazo de alerta (3 dias ou menos)
                        if (!deadline.isBefore(today) && !deadline.isAfter(alertThreshold)) {
                            urgentNotes.add(note);
                            System.out.println("Nota urgente encontrada: " + note.getTitulo());
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao processar deadline da nota: " + note.getTitulo());
                    }
                }
            }

            // Exibe notificação se houver notas urgentes
            if (!urgentNotes.isEmpty()) {
                System.out.println("Exibindo alerta para " + urgentNotes.size() + " notas urgentes");
                showDeadlineAlert(urgentNotes);
            } else {
                System.out.println("Nenhuma nota urgente encontrada");
            }

        } catch (Exception e) {
            System.err.println("Erro na verificação de prazos: " + e.getMessage());
        }
    }

    private void showDeadlineAlert(List<Notes> urgentNotes) {
        StringBuilder message = new StringBuilder("Atenção! As seguintes notas têm prazos próximos:\n\n");

        for (Notes note : urgentNotes) {
            message.append("• ").append(note.getTitulo())
                    .append(" - Prazo: ").append(note.getDeadline()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Prazos Próximos");
        alert.setHeaderText("Notas com deadline em até 3 dias");
        alert.setContentText(message.toString());
        //alert.initOwner(getCurrentStage());
        alert.show();
    }

    /**
     * Para o monitoramento de prazos (usado na finalização da aplicação).
     */
    public void stopDeadlineMonitoring() {
        if (deadlineTimer != null) {
            deadlineTimer.cancel();
            deadlineTimer = null;
        }
    }

    // ========== GETTERS E SETTERS ==========

    /**
     * Define o stage atual da aplicação.
     */
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Método público para outras classes atualizarem a tabela de notas.
     * Usado principalmente pelo NoteController após salvar/excluir notas.
     */
    public void updateNotesDisplay() {
        refreshNotesTable();
    }

}