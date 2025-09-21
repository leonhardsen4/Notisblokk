package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.StatusDAO;
import com.leonhardsen.notisblokk.model.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para a tela de gerenciamento de status das notas.
 * Permite criar, editar e excluir status personalizados com cores associadas,
 * fornecendo uma interface intuitiva com tabela e visualização de cores.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class StatusController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML public TextField txtStatus;
    @FXML public Button btnSave;
    @FXML public Button btnDelete;
    @FXML public ImageView imgSave;
    @FXML public ImageView imgDelete;
    @FXML public ColorPicker colorPicker;
    @FXML public TableView<Status> tblStatus;
    @FXML public TableColumn<Status, String> colStatus;
    @FXML public TableColumn<Status, String> colCor;

    // ========== ATRIBUTOS ==========
    /** Status sendo editado atualmente (null para novo status) */
    private Status selectedStatusItem;

    /** Lista observável de todos os status */
    private ObservableList<Status> statusList;

    /** DAO para operações com status */
    private StatusDAO statusDAO;

    /** Stage atual da janela */
    private Stage currentStage;

    /** Cor padrão para novos status */
    private static final Color DEFAULT_COLOR = Color.LIGHTBLUE;

    // ========== INICIALIZAÇÃO ==========

    /**
     * Inicializa o controlador e configura todos os componentes da interface.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusDAO = new StatusDAO();
        statusList = FXCollections.observableArrayList();

        setupInterface();
        loadInitialData();
    }

    /**
     * Configura todos os componentes da interface.
     */
    private void setupInterface() {
        setupTableView();
        setupEventHandlers();
        setupColorPicker();
        clearFields();
    }

    /**
     * Carrega os dados iniciais na interface.
     */
    private void loadInitialData() {
        refreshTable();
    }

    /**
     * Configura a tabela de status e suas colunas.
     */
    private void setupTableView() {
        // Configuração das colunas
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cor"));
        colCor.setStyle("-fx-alignment: CENTER");

        // Configuração da célula personalizada para exibir cores
        setupColorColumn();

        // Configuração dos eventos da tabela
        setupTableRowFactory();
    }

    /**
     * Configura a coluna de cores com visualização gráfica.
     */
    private void setupColorColumn() {
        colCor.setCellFactory(column -> new TableCell<Status, String>() {
            @Override
            protected void updateItem(String colorHex, boolean empty) {
                super.updateItem(colorHex, empty);

                if (colorHex == null || empty) {
                    setGraphic(null);
                } else {
                    try {
                        Color color = Color.web(colorHex);
                        Rectangle colorRect = new Rectangle(20, 20, color);
                        colorRect.setStroke(Color.BLACK);
                        colorRect.setStrokeWidth(1.0);
                        setGraphic(colorRect);
                    } catch (Exception e) {
                        // Em caso de cor inválida, exibe retângulo cinza
                        Rectangle errorRect = new Rectangle(20, 20, Color.GRAY);
                        setGraphic(errorRect);
                    }
                }
            }
        });
    }

    /**
     * Configura o comportamento das linhas da tabela.
     */
    private void setupTableRowFactory() {
        tblStatus.setRowFactory(tableView -> {
            TableRow<Status> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (event.getClickCount() == 1) {
                        handleSingleClick(row.getItem());
                    } else if (event.getClickCount() == 2) {
                        handleDoubleClick();
                    }
                }
            });
            return row;
        });
    }

    /**
     * Manipula clique simples na tabela (seleção para edição).
     */
    private void handleSingleClick(Status status) {
        selectedStatusItem = status;
        loadStatusForEditing(status);
    }

    /**
     * Manipula duplo clique na tabela (limpar seleção).
     */
    private void handleDoubleClick() {
        clearSelection();
    }

    /**
     * Configura os manipuladores de eventos dos botões.
     */
    private void setupEventHandlers() {
        btnSave.setOnMouseClicked(event -> saveStatus());
        btnDelete.setOnMouseClicked(event -> deleteStatus());
    }

    /**
     * Configura o color picker com cor padrão.
     */
    private void setupColorPicker() {
        colorPicker.setValue(DEFAULT_COLOR);
    }

    // ========== OPERAÇÕES CRUD ==========

    /**
     * Salva o status atual (novo ou editado) no banco de dados.
     */
    private void saveStatus() {
        if (!validateStatusInput()) {
            return;
        }

        try {
            String statusName = txtStatus.getText().trim().toUpperCase();
            String colorHex = colorToHex(colorPicker.getValue());

            if (selectedStatusItem == null) {
                createNewStatus(statusName, colorHex);
            } else {
                updateExistingStatus(statusName, colorHex);
            }

        } catch (Exception e) {
            System.err.println("Erro ao salvar status: " + e.getMessage());
            showAlert("Erro", "Não foi possível salvar o status: " + e.getMessage());
        }
    }

    /**
     * Valida os dados de entrada antes de salvar.
     */
    private boolean validateStatusInput() {
        String statusText = txtStatus.getText();

        if (statusText == null || statusText.trim().isEmpty()) {
            showAlert("Campo Obrigatório", "Digite o nome do status para salvar.");
            txtStatus.requestFocus();
            return false;
        }

        String trimmedText = statusText.trim();
        if (trimmedText.length() < 2) {
            showAlert("Status Muito Curto", "O status deve ter pelo menos 2 caracteres.");
            txtStatus.requestFocus();
            return false;
        }

        if (trimmedText.length() > 30) {
            showAlert("Status Muito Longo", "O status deve ter no máximo 30 caracteres.");
            txtStatus.requestFocus();
            return false;
        }

        if (colorPicker.getValue() == null) {
            showAlert("Cor Obrigatória", "Selecione uma cor para o status.");
            return false;
        }

        return true;
    }

    /**
     * Cria um novo status no banco de dados.
     */
    private void createNewStatus(String statusName, String colorHex) {
        // Verifica se já existe status ou cor igual
        if (statusDAO.findStatus(statusName, colorHex)) {
            showAlert("Duplicata Encontrada",
                    "Já existe um status com este nome ou cor. Escolha valores diferentes.");
            txtStatus.requestFocus();
            return;
        }

        Status newStatus = new Status();
        newStatus.setStatus(statusName);
        newStatus.setCor(colorHex);

        statusDAO.save(newStatus);
        refreshInterface();
    }

    /**
     * Atualiza um status existente no banco de dados.
     */
    private void updateExistingStatus(String statusName, String colorHex) {
        // Verifica se houve mudança
        if (statusName.equals(selectedStatusItem.getStatus()) &&
                colorHex.equals(selectedStatusItem.getCor())) {
            clearSelection();
            return;
        }

        // Verifica duplicatas excluindo o item atual
        if (statusDAO.findStatus(statusName, colorHex) &&
                (!statusName.equals(selectedStatusItem.getStatus()) ||
                        !colorHex.equals(selectedStatusItem.getCor()))) {
            showAlert("Duplicata Encontrada",
                    "Já existe outro status com este nome ou cor. Escolha valores diferentes.");
            txtStatus.requestFocus();
            return;
        }

        selectedStatusItem.setStatus(statusName);
        selectedStatusItem.setCor(colorHex);

        statusDAO.update(selectedStatusItem);
        refreshInterface();
    }

    /**
     * Exclui o status selecionado após confirmação.
     */
    private void deleteStatus() {
        if (selectedStatusItem == null) {
            showAlert("Nenhum Status Selecionado", "Selecione um status na tabela para excluir.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Exclusão de Status");
        confirmationAlert.setHeaderText("Deseja realmente excluir este status?");
        confirmationAlert.setContentText(
                "Status: " + selectedStatusItem.getStatus() + "\n\n" +
                        "ATENÇÃO: Todas as notas que usam este status podem ser afetadas."
        );

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    statusDAO.delete(selectedStatusItem);
                    refreshInterface();
                } catch (Exception e) {
                    System.err.println("Erro ao excluir status: " + e.getMessage());
                    showAlert("Erro", "Não foi possível excluir o status: " + e.getMessage());
                }
            }
        });
    }

    // ========== MÉTODOS DE INTERFACE ==========

    /**
     * Carrega um status selecionado para edição nos campos.
     */
    private void loadStatusForEditing(Status status) {
        if (status != null) {
            txtStatus.setText(status.getStatus());

            try {
                Color color = Color.web(status.getCor());
                colorPicker.setValue(color);
            } catch (Exception e) {
                colorPicker.setValue(DEFAULT_COLOR);
            }

            txtStatus.selectAll();
            txtStatus.requestFocus();
        }
    }

    /**
     * Limpa a seleção atual e os campos de entrada.
     */
    private void clearSelection() {
        selectedStatusItem = null;
        clearFields();
    }

    /**
     * Limpa todos os campos de entrada.
     */
    private void clearFields() {
        txtStatus.clear();
        colorPicker.setValue(DEFAULT_COLOR);
        txtStatus.requestFocus();
    }

    /**
     * Atualiza a tabela com os dados mais recentes do banco.
     */
    private void refreshTable() {
        try {
            statusList.setAll(statusDAO.getAll());
            tblStatus.setItems(statusList);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tabela: " + e.getMessage());
            statusList.clear();
        }
    }

    /**
     * Atualiza toda a interface após operações de modificação.
     */
    private void refreshInterface() {
        clearSelection();
        refreshTable();
        updateMainInterface();
    }

    /**
     * Atualiza a interface principal para refletir mudanças nos status.
     */
    private void updateMainInterface() {
        try {
            if (NotisblokkController.instance != null) {
                // Atualiza os filtros na tela principal
                NotisblokkController.instance.setupFilterComboBox();
                NotisblokkController.instance.updateNotesDisplay();
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar interface principal: " + e.getMessage());
        }
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

    /**
     * Converte uma cor JavaFX para string hexadecimal.
     */
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Exibe um alerta informativo para o usuário.
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
     */
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Obtém o stage atual da janela.
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Obtém a lista atual de status.
     */
    public ObservableList<Status> getStatusList() {
        return statusList;
    }

    /**
     * Obtém o status atualmente selecionado.
     */
    public Status getSelectedStatusItem() {
        return selectedStatusItem;
    }

}