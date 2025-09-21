package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.TagsDAO;
import com.leonhardsen.notisblokk.model.Tags;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para a tela de criação e edição de tags.
 * Gerencia as operações de criação, edição e exclusão de tags,
 * com validação adequada e integração com a tela principal.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class TagController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML public TextField txtTag;
    @FXML public Button btnSave;
    @FXML public ImageView imgSave;
    @FXML public Button btnDelete;
    @FXML public ImageView imgDelete;

    // ========== ATRIBUTOS ==========
    /** Stage atual da janela */
    private Stage currentStage;

    /** Tag sendo editada (null para nova tag) */
    private Tags tagItem;

    /** DAO para operações com tags */
    private TagsDAO tagsDAO;

    // ========== INICIALIZAÇÃO ==========

    /**
     * Inicializa o controlador e configura os componentes da interface.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagsDAO = new TagsDAO();
        setupEventHandlers();
    }

    /**
     * Configura os manipuladores de eventos dos botões.
     */
    private void setupEventHandlers() {
        btnDelete.setOnMouseClicked(event -> deleteTag());
        btnSave.setOnMouseClicked(event -> saveTag());
    }

    // ========== CONFIGURAÇÃO DE DADOS ==========

    /**
     * Define a tag para edição ou prepara a interface para nova tag.
     *
     * @param tagItem Tag a ser editada (null para nova tag)
     */
    public void setTagItem(Tags tagItem) {
        this.tagItem = tagItem;

        if (tagItem != null) {
            loadExistingTag();
        } else {
            setupNewTag();
        }
    }

    /**
     * Carrega os dados de uma tag existente na interface.
     */
    private void loadExistingTag() {
        txtTag.setText(tagItem.getTag());
        txtTag.selectAll();
        txtTag.requestFocus();
    }

    /**
     * Configura a interface para criação de nova tag.
     */
    private void setupNewTag() {
        txtTag.clear();
        txtTag.requestFocus();
    }

    // ========== AÇÕES PRINCIPAIS ==========

    /**
     * Salva a tag atual no banco de dados.
     * Realiza validações antes de salvar e trata duplicatas.
     */
    private void saveTag() {
        if (!validateTagInput()) {
            return;
        }

        try {
            String tagName = txtTag.getText().trim();

            if (tagItem == null) {
                createNewTag(tagName);
            } else {
                updateExistingTag(tagName);
            }

        } catch (Exception e) {
            System.err.println("Erro ao salvar tag: " + e.getMessage());
            showAlert("Erro", "Não foi possível salvar a tag: " + e.getMessage());
        }
    }

    /**
     * Valida a entrada da tag antes de salvar.
     *
     * @return true se a entrada é válida, false caso contrário
     */
    private boolean validateTagInput() {
        String tagText = txtTag.getText();

        if (tagText == null || tagText.trim().isEmpty()) {
            showAlert("Campo Obrigatório", "Digite o nome da tag para salvar.");
            txtTag.requestFocus();
            return false;
        }

        // Validação de tamanho mínimo e máximo
        String trimmedText = tagText.trim();
        if (trimmedText.length() < 2) {
            showAlert("Tag Muito Curta", "A tag deve ter pelo menos 2 caracteres.");
            txtTag.requestFocus();
            return false;
        }

        if (trimmedText.length() > 50) {
            showAlert("Tag Muito Longa", "A tag deve ter no máximo 50 caracteres.");
            txtTag.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Cria uma nova tag no banco de dados.
     *
     * @param tagName Nome da nova tag
     */
    private void createNewTag(String tagName) {
        // Verifica se a tag já existe
        if (tagsDAO.findTag(tagName)) {
            showAlert("Tag Já Cadastrada",
                    "Esta tag já foi cadastrada. Escolha um nome diferente.");
            txtTag.selectAll();
            txtTag.requestFocus();
            return;
        }

        // Cria nova tag
        Tags newTag = new Tags();
        newTag.setTag(tagName);
        tagsDAO.save(newTag);

        closeWindow();
    }

    /**
     * Atualiza uma tag existente no banco de dados.
     *
     * @param tagName Novo nome da tag
     */
    private void updateExistingTag(String tagName) {
        // Verifica se o nome mudou
        if (tagName.equals(tagItem.getTag())) {
            closeWindow();
            return;
        }

        // Verifica se o novo nome já existe em outra tag
        if (tagsDAO.findTag(tagName)) {
            showAlert("Tag Já Cadastrada",
                    "Já existe uma tag com este nome. Escolha um nome diferente.");
            txtTag.selectAll();
            txtTag.requestFocus();
            return;
        }

        // Atualiza a tag
        tagItem.setTag(tagName);
        tagsDAO.update(tagItem);

        closeWindow();
    }

    /**
     * Exclui a tag atual após confirmação do usuário.
     * Avisa sobre a exclusão em cascata das notas relacionadas.
     */
    private void deleteTag() {
        if (tagItem == null) {
            closeWindow();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Exclusão de Tag");
        confirmationAlert.setHeaderText("Deseja realmente excluir esta tag?");
        confirmationAlert.setContentText(
                "Esta operação irá apagar também todas as notas relacionadas à tag \"" +
                        tagItem.getTag() + "\".\n\nEsta ação não pode ser desfeita."
        );

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    tagsDAO.delete(tagItem);
                    closeWindow();
                } catch (Exception e) {
                    System.err.println("Erro ao excluir tag: " + e.getMessage());
                    showAlert("Erro", "Não foi possível excluir a tag: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Fecha a janela e atualiza a interface principal.
     */
    public void closeWindow() {
        updateMainInterface();

        if (currentStage != null) {
            currentStage.close();
        }
    }

    /**
     * Atualiza a interface principal após alterações na tag.
     * Chama os métodos corretos do controlador principal refatorado.
     */
    private void updateMainInterface() {
        try {
            if (NotisblokkController.instance != null) {
                NotisblokkController.instance.refreshTagsList();
                NotisblokkController.instance.updateNotesDisplay();
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar interface principal: " + e.getMessage());
        }
    }

    // ========== MÉTODOS UTILITÁRIOS ==========

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

    /**
     * Obtém a tag atualmente sendo editada.
     *
     * @return A tag atual ou null se for nova tag
     */
    public Tags getTagItem() {
        return tagItem;
    }
}