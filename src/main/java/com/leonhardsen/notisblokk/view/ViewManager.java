//package com.leonhardsen.notisblokk.view;
//
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Background;
//import javafx.scene.paint.Color;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.net.URL;
//
//public class ViewManager {
//
//    private static final int VBOX_WIDTH = 80; // Largura fixa do VBox lateral
//    private static final int DEFAULT_MIN_WIDTH = 800; // Largura mínima padrão
//    private static final int DEFAULT_MIN_HEIGHT = 500; // Altura mínima padrão
//
//    public static void setScreen(String path, String anchor, AnchorPane mainPane) {
//        try {
//            URL fxmlUrl = ViewManager.class.getResource(path);
//
//            if (fxmlUrl == null) {
//                System.err.println("Arquivo FXML não encontrado: " + path);
//                return;
//            }
//
//            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
//            Parent root = fxmlLoader.load();
//
//            AnchorPane childPane = (AnchorPane) root.lookup(anchor);
//
//            if (childPane == null) {
//                System.err.println("Elemento não encontrado: " + anchor);
//                return;
//            }
//
//            childPane.setBackground(Background.fill(Color.WHITE));
//
//            mainPane.getChildren().clear();
//            mainPane.getChildren().add(childPane);
//
//            configurarRedimensionamento(childPane, mainPane);
//            configurarTamanhoMinimoJanela(childPane, mainPane);
//
//        } catch (IOException e) {
//            System.err.println("Erro ao carregar FXML: " + path);
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.err.println("Erro inesperado ao carregar: " + path);
//            e.printStackTrace();
//        }
//    }
//
//    private static void configurarRedimensionamento(AnchorPane childPane, AnchorPane mainPane) {
//        // Remove qualquer bind anterior para evitar conflitos
//        childPane.prefWidthProperty().unbind();
//        childPane.prefHeightProperty().unbind();
//
//        // Configura as âncoras para preencher o espaço disponível no mainPane
//        AnchorPane.setLeftAnchor(childPane, 0.0);
//        AnchorPane.setRightAnchor(childPane, 0.0);
//        AnchorPane.setTopAnchor(childPane, 0.0);
//        AnchorPane.setBottomAnchor(childPane, 0.0);
//
//        // Configura o redimensionamento responsivo
//        childPane.prefWidthProperty().bind(mainPane.widthProperty());
//        childPane.prefHeightProperty().bind(mainPane.heightProperty());
//    }
//
//    private static void configurarTamanhoMinimoJanela(AnchorPane childPane, AnchorPane mainPane) {
//        Stage stage = getStageFromPane(mainPane);
//        if (stage != null) {
//            // Obtém os tamanhos mínimos do childPane, usando valores padrão se forem inválidos
//            double childMinWidth = getValidMinSize(childPane.getMinWidth(), DEFAULT_MIN_WIDTH);
//            double childMinHeight = getValidMinSize(childPane.getMinHeight(), DEFAULT_MIN_HEIGHT);
//
//            // Calcula o tamanho mínimo considerando o VBox fixo de 80px
//            double minWidth = childMinWidth + VBOX_WIDTH + 20; // +20 para margem
//            double minHeight = childMinHeight + 40; // +40 para barras de título e margem
//
//            // Garante valores positivos
//            minWidth = Math.max(minWidth, DEFAULT_MIN_WIDTH + VBOX_WIDTH);
//            minHeight = Math.max(minHeight, DEFAULT_MIN_HEIGHT);
//
//            // Aplica os tamanhos mínimos
//            stage.setMinWidth(minWidth);
//            stage.setMinHeight(minHeight);
//
//            // Garante que a janela não fique menor que o conteúdo mínimo
//            if (stage.getWidth() < minWidth) {
//                stage.setWidth(minWidth);
//            }
//            if (stage.getHeight() < minHeight) {
//                stage.setHeight(minHeight);
//            }
//        }
//    }
//
//    // Obter um valor de tamanho mínimo válido
//    private static double getValidMinSize(double size, double defaultValue) {
//        return size >= 0 ? size : defaultValue;
//    }
//
//    // Obter o stage a partir do mainPane
//    private static Stage getStageFromPane(AnchorPane pane) {
//        if (pane != null && pane.getScene() != null) {
//            return (Stage) pane.getScene().getWindow();
//        }
//        return null;
//    }
//}

package com.leonhardsen.notisblokk.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Gerenciador responsável por carregar e exibir views FXML na aplicação.
 * Fornece funcionalidades para carregamento de interfaces, redimensionamento
 * responsivo e configuração de tamanhos mínimos de janela.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class ViewManager {

    // ========== CONSTANTES ==========
    private static final int VBOX_WIDTH = 80;
    private static final int DEFAULT_MIN_WIDTH = 800;
    private static final int DEFAULT_MIN_HEIGHT = 500;
    private static final int WINDOW_MARGIN = 20;
    private static final int TITLE_BAR_HEIGHT = 40;

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Carrega e exibe uma view FXML no painel principal especificado.
     *
     * @param path Caminho relativo do arquivo FXML a ser carregado
     * @param anchor Seletor CSS do elemento AnchorPane raiz dentro do FXML
     * @param mainPane Painel principal onde a view será exibida
     * @throws IOException Se ocorrer um erro de I/O durante o carregamento do FXML
     * @throws IllegalArgumentException Se o arquivo FXML ou elemento anchor não forem encontrados
     */
    public static void setScreen(String path, String anchor, AnchorPane mainPane) throws IOException {
        validateParameters(path, anchor, mainPane);

        URL fxmlUrl = ViewManager.class.getResource(path);
        if (fxmlUrl == null) {
            throw new IllegalArgumentException("Arquivo FXML não encontrado: " + path);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();

        AnchorPane childPane = (AnchorPane) root.lookup(anchor);
        if (childPane == null) {
            throw new IllegalArgumentException("Elemento não encontrado com o seletor: " + anchor);
        }

        configureView(childPane, mainPane);
    }

    // ========== MÉTODOS PRIVADOS ==========

    /**
     * Valida os parâmetros de entrada para o carregamento da view.
     *
     * @param path Caminho do arquivo FXML
     * @param anchor Seletor do elemento anchor
     * @param mainPane Painel principal
     * @throws IllegalArgumentException Se algum parâmetro for inválido
     */
    private static void validateParameters(String path, String anchor, AnchorPane mainPane) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do FXML não pode ser nulo ou vazio");
        }
        if (anchor == null || anchor.trim().isEmpty()) {
            throw new IllegalArgumentException("Seletor anchor não pode ser nulo ou vazio");
        }
        if (mainPane == null) {
            throw new IllegalArgumentException("Painel principal não pode ser nulo");
        }
    }

    /**
     * Configura a view carregada no painel principal.
     *
     * @param childPane Painel filho carregado do FXML
     * @param mainPane Painel principal da aplicação
     */
    private static void configureView(AnchorPane childPane, AnchorPane mainPane) {
        // Configuração visual básica
        childPane.setBackground(Background.fill(Color.WHITE));

        // Limpa e adiciona o novo conteúdo
        mainPane.getChildren().clear();
        mainPane.getChildren().add(childPane);

        // Configurações de layout e redimensionamento
        configureAnchors(childPane);
        configureResponsiveResizing(childPane, mainPane);
        configureWindowMinSize(childPane, mainPane);
    }

    /**
     * Configura as âncoras para preencher o painel principal.
     *
     * @param childPane Painel filho a ser ancorado
     */
    private static void configureAnchors(AnchorPane childPane) {
        AnchorPane.setLeftAnchor(childPane, 0.0);
        AnchorPane.setRightAnchor(childPane, 0.0);
        AnchorPane.setTopAnchor(childPane, 0.0);
        AnchorPane.setBottomAnchor(childPane, 0.0);
    }

    /**
     * Configura o redimensionamento responsivo do painel filho.
     *
     * @param childPane Painel filho
     * @param mainPane Painel principal
     */
    private static void configureResponsiveResizing(AnchorPane childPane, AnchorPane mainPane) {
        // Remove binds anteriores para evitar memory leaks
        childPane.prefWidthProperty().unbind();
        childPane.prefHeightProperty().unbind();

        // Configura binds responsivos
        childPane.prefWidthProperty().bind(mainPane.widthProperty());
        childPane.prefHeightProperty().bind(mainPane.heightProperty());
    }

    /**
     * Configura o tamanho mínimo da janela baseado no conteúdo.
     *
     * @param childPane Painel filho com o conteúdo
     * @param mainPane Painel principal
     */
    private static void configureWindowMinSize(AnchorPane childPane, AnchorPane mainPane) {
        Stage stage = getStageFromPane(mainPane);
        if (stage != null) {
            double childMinWidth = getValidMinSize(childPane.getMinWidth(), DEFAULT_MIN_WIDTH);
            double childMinHeight = getValidMinSize(childPane.getMinHeight(), DEFAULT_MIN_HEIGHT);

            double minWidth = calculateMinWidth(childMinWidth);
            double minHeight = calculateMinHeight(childMinHeight);

            applyWindowMinSize(stage, minWidth, minHeight);
        }
    }

    /**
     * Calcula a largura mínima considerando elementos fixos da interface.
     *
     * @param contentMinWidth Largura mínima do conteúdo
     * @return Largura mínima total da janela
     */
    private static double calculateMinWidth(double contentMinWidth) {
        return Math.max(contentMinWidth + VBOX_WIDTH + WINDOW_MARGIN,
                DEFAULT_MIN_WIDTH + VBOX_WIDTH);
    }

    /**
     * Calcula a altura mínima considerando elementos fixos da interface.
     *
     * @param contentMinHeight Altura mínima do conteúdo
     * @return Altura mínima total da janela
     */
    private static double calculateMinHeight(double contentMinHeight) {
        return Math.max(contentMinHeight + TITLE_BAR_HEIGHT, DEFAULT_MIN_HEIGHT);
    }

    /**
     * Aplica o tamanho mínimo à janela, garantindo valores válidos.
     *
     * @param stage Stage da janela
     * @param minWidth Largura mínima
     * @param minHeight Altura mínima
     */
    private static void applyWindowMinSize(Stage stage, double minWidth, double minHeight) {
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

        // Garante que a janela atual não seja menor que o mínimo
        if (stage.getWidth() < minWidth) {
            stage.setWidth(minWidth);
        }
        if (stage.getHeight() < minHeight) {
            stage.setHeight(minHeight);
        }
    }

    /**
     * Obtém um valor de tamanho mínimo válido.
     *
     * @param size Tamanho a ser validado
     * @param defaultValue Valor padrão se o tamanho for inválido
     * @return Tamanho válido
     */
    private static double getValidMinSize(double size, double defaultValue) {
        return size >= 0 ? size : defaultValue;
    }

    /**
     * Obtém o Stage a partir de um AnchorPane.
     *
     * @param pane Painel para obter o Stage
     * @return Stage associado ao painel, ou null se não encontrado
     */
    private static Stage getStageFromPane(AnchorPane pane) {
        if (pane != null && pane.getScene() != null && pane.getScene().getWindow() instanceof Stage) {
            return (Stage) pane.getScene().getWindow();
        }
        return null;
    }
}