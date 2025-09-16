package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.dao.DocumentDAO;
import com.leonhardsen.notisblokk.model.Document;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DigitalFolderController implements Initializable {

    @FXML public AnchorPane rootPane;
    @FXML private ListView<Document> listViewDocumentos;
    @FXML private TreeView<Document> treeViewDocumentos;
    @FXML private WebView documentWebView;
    @FXML private Label statusLabel;
    @FXML private Button btnEditarDocumento;
    @FXML private Button btnNovoDocumento;

    private DocumentDAO documentDAO;
    private Document documentoSelecionado;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentDAO = new DocumentDAO();
        configurarComponentes();
        carregarDocumentos();

        // Desabilitar botão de edição inicialmente
        if (btnEditarDocumento != null) {
            btnEditarDocumento.setDisable(true);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void configurarComponentes() {
        // Configurar a lista de documentos
        listViewDocumentos.setCellFactory(param -> new ListCell<Document>() {
            @Override
            protected void updateItem(Document item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getTitulo() + " - " + item.getDataCriacao());
                }
            }
        });

        // Configurar a árvore de documentos
        treeViewDocumentos.setCellFactory(param -> new TreeCell<Document>() {
            @Override
            protected void updateItem(Document item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Mostrar título + data para melhor identificação
                    setText(item.getTitulo() + " (" + item.getDataCriacao() + ")");

                    // Adicionar indentação visual baseada no nível
                    TreeItem<Document> treeItem = getTreeItem();
                    int nivel = 0;
                    while (treeItem != null && treeItem.getParent() != null) {
                        nivel++;
                        treeItem = treeItem.getParent();
                    }

                    // Adicionar recuo baseado no nível
                    setStyle("-fx-padding: 2 2 2 " + (nivel * 15 + 5) + ";");
                }
            }
        });

        // Configurar clique duplo na lista
        listViewDocumentos.setOnMouseClicked(this::tratarCliqueLista);

        // Configurar clique duplo na árvore
        treeViewDocumentos.setOnMouseClicked(this::tratarCliqueArvore);

        // Configurar menu de contexto
        configurarMenuContexto();

        // Configurar listeners de seleção
        listViewDocumentos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selecionarDocumento(newVal.getId());
                    } else {
                        desselecionarDocumento();
                    }
                });

        treeViewDocumentos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && newVal.getValue() != null) {
                        selecionarDocumento(newVal.getValue().getId());
                    } else {
                        desselecionarDocumento();
                    }
                });
    }

    private void configurarMenuContexto() {
        // Menu de contexto para lista
        ContextMenu contextMenuLista = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar Documento");
        MenuItem excluirItem = new MenuItem("Excluir Documento");

        editarItem.setOnAction(e -> editarDocumento());
        excluirItem.setOnAction(e -> excluirDocumento());

        contextMenuLista.getItems().addAll(editarItem, excluirItem);
        listViewDocumentos.setContextMenu(contextMenuLista);

        // Menu de contexto para árvore
        ContextMenu contextMenuArvore = new ContextMenu();
        MenuItem editarItemArvore = new MenuItem("Editar Documento");
        MenuItem excluirItemArvore = new MenuItem("Excluir Documento");

        editarItemArvore.setOnAction(e -> editarDocumento());
        excluirItemArvore.setOnAction(e -> excluirDocumento());

        contextMenuArvore.getItems().addAll(editarItemArvore, excluirItemArvore);
        treeViewDocumentos.setContextMenu(contextMenuArvore);
    }

    private void tratarCliqueLista(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 2) {
                // Clique duplo - editar documento
                editarDocumento();
            } else if (event.getClickCount() == 1) {
                // Clique simples - apenas selecionar
                Document selected = listViewDocumentos.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selecionarDocumento(selected.getId());
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            // Clique direito já é tratado pelo menu de contexto
        }
    }

    private void tratarCliqueArvore(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            TreeItem<Document> selected = treeViewDocumentos.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getValue() != null) {
                if (event.getClickCount() == 2) {
                    // Clique duplo - editar documento
                    editarDocumento();
                } else if (event.getClickCount() == 1) {
                    // Clique simples - apenas selecionar
                    selecionarDocumento(selected.getValue().getId());
                }
            }
        }
    }

    private void selecionarDocumento(int id) {
        documentoSelecionado = documentDAO.buscarPorId(id);
        if (documentoSelecionado != null) {
            visualizarDocumento(documentoSelecionado);
            statusLabel.setText("Documento selecionado: " + documentoSelecionado.getTitulo());
            if (btnEditarDocumento != null) {
                btnEditarDocumento.setDisable(false);
            }
        }
    }

    private void desselecionarDocumento() {
        documentoSelecionado = null;
        documentWebView.getEngine().loadContent("");
        statusLabel.setText("Nenhum documento selecionado");
        if (btnEditarDocumento != null) {
            btnEditarDocumento.setDisable(true);
        }
    }

    @FXML
    private void novoDocumento() {
        abrirEditorNovoDocumento();
    }

    @FXML
    private void editarDocumento() {
        if (documentoSelecionado != null) {
            abrirEditorDocumento(documentoSelecionado);
        } else {
            mostrarAlerta("Aviso", "Nenhum documento selecionado para editar.");
        }
    }

    private void excluirDocumento() {
        if (documentoSelecionado != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Excluir Documento");
            confirmacao.setContentText("Tem certeza que deseja excluir o documento '" + documentoSelecionado.getTitulo() + "'?");

            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                if (documentDAO.excluir(documentoSelecionado.getId())) {
                    statusLabel.setText("Documento excluído com sucesso!");
                    desselecionarDocumento();
                    carregarDocumentos();
                } else {
                    mostrarAlerta("Erro", "Não foi possível excluir o documento.");
                }
            }
        } else {
            mostrarAlerta("Aviso", "Nenhum documento selecionado para excluir.");
        }
    }

    private void abrirEditorNovoDocumento() {
        // Determinar o ID do documento pai (se houver seleção)
        int idDocumentoPai;
        if (documentoSelecionado != null) {
            idDocumentoPai = documentoSelecionado.getId();
        } else {
            idDocumentoPai = 0;
        }

        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Novo Documento");
        dialog.setHeaderText("Criar um novo documento");

        // Configurar botões
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, btnCancelar);

        // Criar formulário
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Título do documento");
        TextArea txtConteudo = new TextArea();
        txtConteudo.setPromptText("Conteúdo do documento");
        txtConteudo.setPrefRowCount(10);

        grid.add(new Label("Título:"), 0, 0);
        grid.add(txtTitulo, 1, 0);
        grid.add(new Label("Conteúdo:"), 0, 1);
        grid.add(txtConteudo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Converter resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                Document novoDoc = new Document();
                novoDoc.setTitulo(txtTitulo.getText());
                novoDoc.setConteudoAsString(txtConteudo.getText());
                novoDoc.setDataCriacao(LocalDate.now());
                novoDoc.setIdDocumentoPai(idDocumentoPai);
                return novoDoc;
            }
            return null;
        });

        // Processar resultado
        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(documento -> {
            if (documentDAO.salvar(documento)) {
                statusLabel.setText("Documento criado com sucesso!");
                carregarDocumentos();
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar o documento.");
            }
        });
    }

    private void abrirEditorDocumento(Document documento) {
        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Editar Documento");
        dialog.setHeaderText("Editando: " + documento.getTitulo());

        // Configurar botões
        ButtonType btnSalvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnExcluir = new ButtonType("Excluir", ButtonBar.ButtonData.OTHER);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, btnExcluir, btnCancelar);

        // Criar formulário
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtTitulo = new TextField(documento.getTitulo());
        TextArea txtConteudo = new TextArea(documento.getConteudoAsString());
        txtConteudo.setPrefRowCount(10);

        grid.add(new Label("Título:"), 0, 0);
        grid.add(txtTitulo, 1, 0);
        grid.add(new Label("Conteúdo:"), 0, 1);
        grid.add(txtConteudo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Configurar botão excluir
        Button btnExcluirButton = (Button) dialog.getDialogPane().lookupButton(btnExcluir);
        btnExcluirButton.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
            excluirDocumento();
        });

        // Converter resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                documento.setTitulo(txtTitulo.getText());
                documento.setConteudoAsString(txtConteudo.getText());
                return documento;
            }
            return null;
        });

        // Processar resultado
        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(documentoEditado -> {
            if (documentDAO.atualizar(documentoEditado)) {
                statusLabel.setText("Documento atualizado com sucesso!");
                visualizarDocumento(documentoEditado);
                carregarDocumentos();
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar as alterações.");
            }
        });
    }

    @FXML
    private void imprimirDocumento() {
        statusLabel.setText("Funcionalidade de impressão em desenvolvimento.");
    }

    @FXML
    private void imprimirSelecionados() {
        statusLabel.setText("Funcionalidade de impressão em desenvolvimento.");
    }

    @FXML
    private void imprimirTodos() {
        statusLabel.setText("Funcionalidade de impressão em desenvolvimento.");
    }

    private void carregarDocumentos() {
        // Carregar apenas metadados para melhor performance na lista
        listViewDocumentos.getItems().setAll(documentDAO.buscarMetadados());

        // Carregar hierarquia completa para a árvore
        carregarArvoreDocumentos();
    }

    private void carregarArvoreDocumentos() {
        List<Document> todosDocumentos = documentDAO.buscarTodosComHierarquia();

        // Criar mapa para acesso rápido aos documentos por ID
        Map<Integer, TreeItem<Document>> mapaDocumentos = new HashMap<>();
        Map<Integer, List<Document>> filhosPorPai = new HashMap<>();

        // Primeiro, organizar os documentos por pai
        for (Document doc : todosDocumentos) {
            int idPai = doc.getIdDocumentoPai();

            if (!filhosPorPai.containsKey(idPai)) {
                filhosPorPai.put(idPai, new ArrayList<>());
            }
            filhosPorPai.get(idPai).add(doc);

            // Criar TreeItem para cada documento
            mapaDocumentos.put(doc.getId(), new TreeItem<>(doc));
        }

        // Construir a árvore hierárquica
        TreeItem<Document> root = new TreeItem<>(new Document(0, "Documentos", null, null, 0));
        root.setExpanded(true);

        // Adicionar documentos raiz (com id_documento_pai = 0)
        if (filhosPorPai.containsKey(0)) {
            for (Document docRaiz : filhosPorPai.get(0)) {
                TreeItem<Document> itemRaiz = mapaDocumentos.get(docRaiz.getId());
                root.getChildren().add(itemRaiz);

                // Adicionar filhos recursivamente
                adicionarFilhosRecursivamente(itemRaiz, docRaiz.getId(), filhosPorPai, mapaDocumentos);
            }
        }

        treeViewDocumentos.setRoot(root);

        // Expandir todos os itens por padrão
        expandirTodos(treeViewDocumentos.getRoot());
    }

    // Método recursivo para adicionar filhos
    private void adicionarFilhosRecursivamente(TreeItem<Document> pai, int idPai,
                                               Map<Integer, List<Document>> filhosPorPai,
                                               Map<Integer, TreeItem<Document>> mapaDocumentos) {
        if (filhosPorPai.containsKey(idPai)) {
            for (Document filho : filhosPorPai.get(idPai)) {
                TreeItem<Document> itemFilho = mapaDocumentos.get(filho.getId());
                pai.getChildren().add(itemFilho);

                // Chamar recursivamente para os filhos deste filho
                adicionarFilhosRecursivamente(itemFilho, filho.getId(), filhosPorPai, mapaDocumentos);
            }
        }
    }

    // Expandir todos os nós
    private void expandirTodos(TreeItem<Document> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<Document> child : item.getChildren()) {
                expandirTodos(child);
            }
        }
    }

    private void visualizarDocumento(Document documento) {
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2 style='color: #2c3e50;'>" + documento.getTitulo() + "</h2>" +
                "<div style='background-color: #f8f9fa; padding: 10px; border-left: 4px solid #3498db; margin-bottom: 20px;'>" +
                "<p><strong>Data:</strong> " + documento.getDataCriacao() + "</p>" +
                "</div>" +
                "<div style='line-height: 1.6; white-space: pre-wrap;'>" + documento.getConteudoAsString() + "</div></body></html>";
        documentWebView.getEngine().loadContent(htmlContent);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}