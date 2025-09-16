package com.leonhardsen.notisblokk.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ViewManager {

    private static final int VBOX_WIDTH = 80; // Largura fixa do VBox lateral
    private static final int DEFAULT_MIN_WIDTH = 800; // Largura mínima padrão
    private static final int DEFAULT_MIN_HEIGHT = 500; // Altura mínima padrão

    public static void setScreen(String path, String anchor, AnchorPane mainPane) {
        try {
            URL fxmlUrl = ViewManager.class.getResource(path);

            if (fxmlUrl == null) {
                System.err.println("Arquivo FXML não encontrado: " + path);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent root = fxmlLoader.load();

            AnchorPane childPane = (AnchorPane) root.lookup(anchor);

            if (childPane == null) {
                System.err.println("Elemento não encontrado: " + anchor);
                return;
            }

            childPane.setBackground(Background.fill(Color.WHITE));

            mainPane.getChildren().clear();
            mainPane.getChildren().add(childPane);

            configurarRedimensionamento(childPane, mainPane);
            configurarTamanhoMinimoJanela(childPane, mainPane);

        } catch (IOException e) {
            System.err.println("Erro ao carregar FXML: " + path);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro inesperado ao carregar: " + path);
            e.printStackTrace();
        }
    }

    private static void configurarRedimensionamento(AnchorPane childPane, AnchorPane mainPane) {
        // Remove qualquer bind anterior para evitar conflitos
        childPane.prefWidthProperty().unbind();
        childPane.prefHeightProperty().unbind();

        // Configura as âncoras para preencher o espaço disponível no mainPane
        AnchorPane.setLeftAnchor(childPane, 0.0);
        AnchorPane.setRightAnchor(childPane, 0.0);
        AnchorPane.setTopAnchor(childPane, 0.0);
        AnchorPane.setBottomAnchor(childPane, 0.0);

        // Configura o redimensionamento responsivo
        childPane.prefWidthProperty().bind(mainPane.widthProperty());
        childPane.prefHeightProperty().bind(mainPane.heightProperty());
    }

    private static void configurarTamanhoMinimoJanela(AnchorPane childPane, AnchorPane mainPane) {
        Stage stage = getStageFromPane(mainPane);
        if (stage != null) {
            // Obtém os tamanhos mínimos do childPane, usando valores padrão se forem inválidos
            double childMinWidth = getValidMinSize(childPane.getMinWidth(), DEFAULT_MIN_WIDTH);
            double childMinHeight = getValidMinSize(childPane.getMinHeight(), DEFAULT_MIN_HEIGHT);

            // Calcula o tamanho mínimo considerando o VBox fixo de 80px
            double minWidth = childMinWidth + VBOX_WIDTH + 20; // +20 para margem
            double minHeight = childMinHeight + 40; // +40 para barras de título e margem

            // Garante valores positivos
            minWidth = Math.max(minWidth, DEFAULT_MIN_WIDTH + VBOX_WIDTH);
            minHeight = Math.max(minHeight, DEFAULT_MIN_HEIGHT);

            // Aplica os tamanhos mínimos
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);

            // Garante que a janela não fique menor que o conteúdo mínimo
            if (stage.getWidth() < minWidth) {
                stage.setWidth(minWidth);
            }
            if (stage.getHeight() < minHeight) {
                stage.setHeight(minHeight);
            }
        }
    }

    // Obter um valor de tamanho mínimo válido
    private static double getValidMinSize(double size, double defaultValue) {
        return size >= 0 ? size : defaultValue;
    }

    // Obter o stage a partir do mainPane
    private static Stage getStageFromPane(AnchorPane pane) {
        if (pane != null && pane.getScene() != null) {
            return (Stage) pane.getScene().getWindow();
        }
        return null;
    }
}