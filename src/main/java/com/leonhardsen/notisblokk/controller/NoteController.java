package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.NotesDAO;
import com.leonhardsen.notisblokk.dao.StatusDAO;
import com.leonhardsen.notisblokk.dao.TemplateDAO;
import com.leonhardsen.notisblokk.model.Notes;
import com.leonhardsen.notisblokk.model.Status;
import com.leonhardsen.notisblokk.model.Tags;
import com.leonhardsen.notisblokk.model.Template;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador para a tela de criação e edição de notas.
 * Gerencia um editor HTML para formatação de texto rico e
 * persiste o conteúdo como array de bytes no banco de dados.
 *
 * @author Leonhardsen
 * @version 2.1
 */
public class NoteController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML public TextField txtTitulo;
    @FXML public Button btnSave;
    @FXML public ImageView imgSave;
    @FXML public Button btnDelete;
    @FXML public ImageView imgDelete;
    @FXML public ComboBox<String> cmbStatus;
    @FXML public ComboBox<Template> cmbTemplate;
    @FXML public DatePicker dpDeadline;
    @FXML public HTMLEditor htmlEditor;

    // ========== ATRIBUTOS ==========
    /** Nota sendo editada (null para nova nota) */
    public Notes noteItem;

    /** Tag associada à nota */
    public Tags tagItem;

    /** DAO para operações com templates */
    private TemplateDAO templateDAO;

    /** Stage atual da janela */
    private Stage currentStage;

    /** Formatador de data brasileiro */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========== INICIALIZAÇÃO ==========

    /**
     * Inicializa o controlador configurando componentes e carregando dados iniciais.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        templateDAO = new TemplateDAO();

        setupComponents();
        loadInitialData();
    }

    /**
     * Configura os componentes da interface e seus eventos.
     */
    private void setupComponents() {
        // Configura combo de status
//        cmbStatus.setItems(loadStatusList());
//        cmbStatus.getSelectionModel().selectFirst();

        // Configura combo de status com bolinhas coloridas
        setupStatusComboBox();

        // Configura eventos dos botões
        btnDelete.setOnMouseClicked(event -> deleteNote());
        btnSave.setOnMouseClicked(event -> saveNote());

        // Configura o HTMLEditor com conteúdo padrão
        htmlEditor.setHtmlText("<p>Digite o conteúdo da sua nota aqui...</p>");

        // Listener para aplicação de templates
        cmbTemplate.getSelectionModel().selectedItemProperty().addListener((obs, oldTemplate, newTemplate) -> {
            if (newTemplate != null && !newTemplate.equals(oldTemplate)) {
                applyTemplate(newTemplate);
            }
        });
    }

    /**
     * Configura o ComboBox de status com bolinhas coloridas.
     */
    private void setupStatusComboBox() {
        ObservableList<String> statusList = loadStatusList();

        // Configura a cell factory para exibir bolinhas coloridas
        cmbStatus.setCellFactory(listView -> new ListCell<>() {
            private final Circle circle = new Circle(5);
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

        // Configura a button cell para mostrar a seleção atual
        cmbStatus.setButtonCell(new ListCell<>() {
            private final Circle circle = new Circle(5);
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

                    String colorHex = getColorForStatus(status);
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

                    setGraphic(container);
                    setText(null);
                }
            }
        });

        cmbStatus.setItems(statusList);
        cmbStatus.getSelectionModel().selectFirst();
    }

    /**
     * Obtém a cor hexadecimal para um determinado status.
     */
    private String getColorForStatus(String statusName) {
        try {
            StatusDAO statusDAO = new StatusDAO();
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

    /**
     * Carrega dados iniciais necessários para o funcionamento da tela.
     */
    private void loadInitialData() {
        loadTemplates();
    }

    /**
     * Carrega templates disponíveis no ComboBox.
     */
    private void loadTemplates() {
        try {
            ObservableList<Template> templates = templateDAO.getAll();
            cmbTemplate.setItems(templates);
        } catch (Exception e) {
            System.err.println("Erro ao carregar templates: " + e.getMessage());
        }
    }

    /**
     * Aplica um template selecionado ao editor HTML.
     */
    private void applyTemplate(Template template) {
        if (template != null && template.getTexto() != null && template.getTexto().length > 0) {
            try {
                String htmlContent = deserializeHtmlContent(template.getTexto());
                htmlEditor.setHtmlText(htmlContent);
            } catch (Exception e) {
                System.err.println("Erro ao aplicar template: " + e.getMessage());
                // Tenta interpretar como texto plano em caso de erro
                String fallbackContent = new String(template.getTexto());
                htmlEditor.setHtmlText("<p>" + fallbackContent + "</p>");
            }
        }
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO DE DADOS ==========

    /**
     * Define os dados para edição de uma nota existente ou criação de nova nota.
     *
     * @param tagItem Tag associada à nota (obrigatória)
     * @param noteItem Nota a ser editada (null para nova nota)
     * @param statusItem Status inicial (pode ser null)
     */
    public void setData(Tags tagItem, Notes noteItem, String statusItem) {
        // Validação de entrada
        if (tagItem == null) {
            throw new IllegalArgumentException("Tag não pode ser null");
        }

        this.tagItem = tagItem;
        this.noteItem = noteItem;

        if (noteItem != null) {
            loadExistingNote();
        } else {
            setupNewNote(statusItem);
        }

        // Foca no campo título para melhor UX
        txtTitulo.requestFocus();
    }

    /**
     * Carrega dados de uma nota existente nos campos da interface.
     */
    private void loadExistingNote() {
        txtTitulo.setText(noteItem.getTitulo());
        loadNoteContent();

        // Define status se disponível
        if (noteItem.getStatus() != null) {
            // Encontra o índice do status atual na lista
            String currentStatus = noteItem.getStatus();
            ObservableList<String> statusList = cmbStatus.getItems();

            for (int i = 0; i < statusList.size(); i++) {
                if (statusList.get(i).equals(currentStatus)) {
                    cmbStatus.getSelectionModel().select(i);
                    break;
                }
            }
        }

        loadDeadline();
    }

    /**
     * Carrega o conteúdo HTML da nota no editor.
     */
    private void loadNoteContent() {
        byte[] relatoBytes = noteItem.getRelato();
        if (relatoBytes != null && relatoBytes.length > 0) {
            try {
                String htmlContent = deserializeHtmlContent(relatoBytes);
                htmlEditor.setHtmlText(htmlContent);
            } catch (Exception e) {
                System.err.println("Erro ao carregar conteúdo da nota: " + e.getMessage());
                // Em caso de erro, tenta interpretar como texto plano
                String fallbackContent = new String(relatoBytes);
                htmlEditor.setHtmlText("<p>" + fallbackContent + "</p>");
            }
        }
    }

    /**
     * Desserializa o conteúdo HTML a partir do array de bytes.
     */
    private String deserializeHtmlContent(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object content = ois.readObject();
            return content.toString();
        }
    }

    /**
     * Carrega a data de deadline se disponível.
     */
    private void loadDeadline() {
        String deadline = noteItem.getDeadline();
        if (deadline != null && !deadline.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(deadline, DATE_FORMATTER);
                dpDeadline.setValue(date);
            } catch (Exception e) {
                System.err.println("Erro ao carregar deadline: " + e.getMessage());
            }
        }
    }

    /**
     * Configura a interface para criação de nova nota.
     */
    private void setupNewNote(String statusItem) {
        htmlEditor.setHtmlText("<p>Digite o conteúdo da sua nota aqui...</p>");

        // Define status inicial se fornecido
        if (statusItem != null) {
            // Encontra o índice do status na lista
            ObservableList<String> statusList = cmbStatus.getItems();

            for (int i = 0; i < statusList.size(); i++) {
                if (statusList.get(i).equals(statusItem)) {
                    cmbStatus.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    // ========== AÇÕES PRINCIPAIS ==========

    /**
     * Exclui a nota atual após confirmação do usuário.
     */
    private void deleteNote() {
        if (noteItem == null) {
            closeWindow();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exclusão de Nota");
        alert.setHeaderText("Deseja realmente excluir esta nota?");
        alert.setContentText("Nota: " + noteItem.getTitulo());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    NotesDAO notesDAO = new NotesDAO();
                    notesDAO.delete(noteItem);
                    closeWindow();
                } catch (Exception e) {
                    showAlert("Erro", "Não foi possível excluir a nota: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Salva a nota atual no banco de dados.
     */
    private void saveNote() {
        if (!validateFields()) {
            return;
        }

        try {
            NotesDAO notesDAO = new NotesDAO();
            String deadline = formatDeadline();
            byte[] htmlContentBytes = serializeHtmlContent();

            if (noteItem == null) {
                createNewNote(notesDAO, deadline, htmlContentBytes);
            } else {
                updateExistingNote(notesDAO, deadline, htmlContentBytes);
            }

            closeWindow();

        } catch (Exception e) {
            showAlert("Erro", "Erro ao salvar a nota: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida os campos obrigatórios antes de salvar.
     */
    private boolean validateFields() {
        String titulo = txtTitulo.getText();
        if (titulo == null || titulo.trim().isEmpty()) {
            showAlert("Campo Obrigatório", "O título da nota é obrigatório.");
            txtTitulo.requestFocus();
            return false;
        }

        if (cmbStatus.getSelectionModel().getSelectedItem() == null) {
            showAlert("Campo Obrigatório", "Selecione um status para a nota.");
            cmbStatus.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Formata a data de deadline para string no padrão brasileiro.
     */
    private String formatDeadline() {
        return dpDeadline.getValue() != null ?
                dpDeadline.getValue().format(DATE_FORMATTER) : null;
    }

    /**
     * Serializa o conteúdo HTML para array de bytes.
     */
    private byte[] serializeHtmlContent() throws IOException {
        String htmlContent = htmlEditor.getHtmlText();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(htmlContent);
            return baos.toByteArray();
        }
    }

    /**
     * Cria uma nova nota no banco de dados.
     */
    private void createNewNote(NotesDAO notesDAO, String deadline, byte[] htmlContentBytes) {
        Notes note = new Notes();
        note.setId_tag(tagItem.getId());
        note.setData(LocalDate.now().format(DATE_FORMATTER));
        note.setTitulo(txtTitulo.getText().trim());
        note.setRelato(htmlContentBytes);
        note.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
        note.setDeadline(deadline);

        notesDAO.save(note);
    }

    /**
     * Atualiza uma nota existente no banco de dados.
     */
    private void updateExistingNote(NotesDAO notesDAO, String deadline, byte[] htmlContentBytes) {
        noteItem.setTitulo(txtTitulo.getText().trim());
        noteItem.setRelato(htmlContentBytes);
        noteItem.setStatus(cmbStatus.getSelectionModel().getSelectedItem());
        noteItem.setDeadline(deadline);
        // A data de última modificação é definida automaticamente no DAO

        notesDAO.update(noteItem);
    }

    /**
     * Fecha a janela e atualiza a interface principal.
     */
    public void closeWindow() {
        // Atualiza a tabela na tela principal
        if (NotisblokkController.instance != null) {
            NotisblokkController.instance.updateNotesDisplay();
        }

        if (currentStage != null) {
            currentStage.close();
        }
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Carrega a lista de status disponíveis para as notas.
     *
     * @return Lista observável com os nomes dos status
     */
    public ObservableList<String> loadStatusList() {
        try {
            StatusDAO statusDAO = new StatusDAO();
            ObservableList<Status> statusFromDB = statusDAO.getAll();
            ObservableList<String> statusNames = FXCollections.observableArrayList();

            for (Status status : statusFromDB) {
                statusNames.add(status.getStatus());
            }

            return statusNames;
        } catch (Exception e) {
            System.err.println("Erro ao carregar lista de status: " + e.getMessage());
            // Retorna lista com status padrão em caso de erro
            return FXCollections.observableArrayList("A RESOLVER", "EM ANDAMENTO", "RESOLVIDO");
        }
    }

    /**
     * Exibe um alerta informativo para o usuário.
     *
     * @param title Título do alerta
     * @param message Mensagem do alerta
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ========== GETTERS E SETTERS ==========

    /**
     * Define o stage atual da janela.
     *
     * @param currentStage O stage a ser definido
     */
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Obtém o stage atual da janela.
     *
     * @return O stage atual
     */
    public Stage getCurrentStage() {
        return currentStage;
    }
}