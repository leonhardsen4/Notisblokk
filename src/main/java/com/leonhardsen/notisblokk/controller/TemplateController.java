package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.TemplateDAO;
import com.leonhardsen.notisblokk.model.Template;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para a tela de gestão de modelos.
 * Permite criar, editar e excluir modelos com formatação HTML.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class TemplateController implements Initializable {

    // ========== COMPONENTES FXML ==========
    @FXML public ListView<Template> listTemplates;
    @FXML public TextField txtTituloTemplate;
    @FXML public HTMLEditor htmlEditor;
    @FXML public Button btnNovoTemplate;
    @FXML public Button btnSalvarTemplate;
    @FXML public Button btnExcluirTemplate;

    // ========== ATRIBUTOS ==========
    private Stage currentStage;
    private TemplateDAO templateDAO;
    private Template selectedTemplate;

    /**
     * Inicializa o controlador configurando componentes e carregando dados iniciais.
     *
     * @param location A localização do FXML
     * @param resources Os recursos do bundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        templateDAO = new TemplateDAO();
        loadTemplates();

        // Configura listener para seleção de modelos
        listTemplates.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTemplate = newSelection;
                txtTituloTemplate.setText(selectedTemplate.getTitulo());
                loadTemplateContent(selectedTemplate);
            }
        });

        // Configura eventos dos botões
        btnNovoTemplate.setOnAction(event -> clearFields());
        btnSalvarTemplate.setOnAction(event -> saveTemplate());
        btnExcluirTemplate.setOnAction(event -> deleteTemplate());

        // Configura conteúdo padrão do editor
        htmlEditor.setHtmlText("<p>Digite o conteúdo do template aqui...</p>");
    }

    /**
     * Carrega os templates na lista.
     */
    private void loadTemplates() {
        try {
            ObservableList<Template> templates = templateDAO.getAll();
            listTemplates.setItems(templates);
        } catch (Exception e) {
            System.err.println("Erro ao carregar templates: " + e.getMessage());
            showAlert("Erro", "Não foi possível carregar os templates.");
        }
    }

    /**
     * Carrega o conteúdo HTML do template no editor.
     *
     * @param template O template a ser carregado
     */
    private void loadTemplateContent(Template template) {
        byte[] textoBytes = template.getTexto();
        if (textoBytes != null && textoBytes.length > 0) {
            try {
                String htmlContent = deserializeHtmlContent(textoBytes);
                htmlEditor.setHtmlText(htmlContent);
            } catch (Exception e) {
                System.err.println("Erro ao carregar conteúdo do template: " + e.getMessage());
                // Tenta interpretar como texto plano em caso de erro
                String fallbackContent = new String(textoBytes);
                htmlEditor.setHtmlText("<p>" + fallbackContent + "</p>");
            }
        } else {
            htmlEditor.setHtmlText("<p>Digite o conteúdo do template aqui...</p>");
        }
    }

    /**
     * Desserializa o conteúdo HTML a partir do array de bytes.
     *
     * @param data Array de bytes com o conteúdo serializado
     * @return ‘String’ com o conteúdo HTML
     * @throws IOException Se ocorrer erro de I/O
     * @throws ClassNotFoundException Se a classe não for encontrada
     */
    private String deserializeHtmlContent(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object content = ois.readObject();
            return content.toString();
        }
    }

    /**
     * Limpa os campos de texto e a seleção.
     */
    private void clearFields() {
        selectedTemplate = null;
        txtTituloTemplate.clear();
        htmlEditor.setHtmlText("<p>Digite o conteúdo do template aqui...</p>");
        listTemplates.getSelectionModel().clearSelection();
    }

    /**
     * Salva o template atual (novo ou editado) no banco de dados.
     */
    private void saveTemplate() {
        if (!validateFields()) {
            return;
        }

        try {
            String titulo = txtTituloTemplate.getText().trim();
            byte[] htmlContentBytes = serializeHtmlContent();

            if (selectedTemplate == null) {
                createNewTemplate(titulo, htmlContentBytes);
            } else {
                updateExistingTemplate(titulo, htmlContentBytes);
            }

            loadTemplates();
            clearFields();

        } catch (Exception e) {
            System.err.println("Erro ao salvar template: " + e.getMessage());
            showAlert("Erro", "Não foi possível salvar o template: " + e.getMessage());
        }
    }

    /**
     * Valida os campos obrigatórios antes de salvar.
     *
     * @return true se os campos são válidos, false caso contrário
     */
    private boolean validateFields() {
        String titulo = txtTituloTemplate.getText();
        if (titulo == null || titulo.trim().isEmpty()) {
            showAlert("Campo Obrigatório", "O título do template é obrigatório.");
            txtTituloTemplate.requestFocus();
            return false;
        }

        if (titulo.trim().length() < 2) {
            showAlert("Título Muito Curto", "O título deve ter pelo menos 2 caracteres.");
            txtTituloTemplate.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Serializa o conteúdo HTML para array de bytes.
     *
     * @return Array de bytes com o conteúdo serializado
     * @throws IOException Se ocorrer erro de I/O
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
     * Cria um modelo no banco de dados.
     *
     * @param titulo Título do template
     * @param htmlContentBytes Conteúdo HTML serializado
     */
    private void createNewTemplate(String titulo, byte[] htmlContentBytes) {
        Template newTemplate = new Template();
        newTemplate.setTitulo(titulo);
        newTemplate.setTexto(htmlContentBytes);
        templateDAO.save(newTemplate);
    }

    /**
     * Atualiza um modelo existente no banco de dados.
     *
     * @param titulo Título do template
     * @param htmlContentBytes Conteúdo HTML serializado
     */
    private void updateExistingTemplate(String titulo, byte[] htmlContentBytes) {
        selectedTemplate.setTitulo(titulo);
        selectedTemplate.setTexto(htmlContentBytes);
        templateDAO.update(selectedTemplate);
    }

    /**
     * Exclui o modelo selecionado após confirmação do utilizador.
     */
    private void deleteTemplate() {
        if (selectedTemplate == null) {
            showAlert("Nenhum Template Selecionado", "Selecione um template para excluir.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Exclusão de Template");
        confirmationAlert.setHeaderText("Deseja realmente excluir este template?");
        confirmationAlert.setContentText("Template: " + selectedTemplate.getTitulo());

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    templateDAO.delete(selectedTemplate);
                    loadTemplates();
                    clearFields();
                } catch (Exception e) {
                    System.err.println("Erro ao excluir template: " + e.getMessage());
                    showAlert("Erro", "Não foi possível excluir o template: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Exibe um alerta informativo para o utilizador.
     *
     * @param title Título do alerta
     * @param content Mensagem do alerta
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

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