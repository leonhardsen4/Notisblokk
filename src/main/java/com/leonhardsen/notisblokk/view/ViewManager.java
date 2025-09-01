package com.leonhardsen.notisblokk.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ViewManager {

    public static void setScreen(String path, String anchor, AnchorPane mainPane) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewManager.class.getResource(path));

            // Verifica se o arquivo FXML existe
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Arquivo FXML não encontrado: " + path);
            }

            Parent root = fxmlLoader.load();
            AnchorPane childPane = (AnchorPane) root.lookup(anchor);

            if (childPane == null) {
                throw new RuntimeException("Elemento não encontrado: " + anchor);
            }

            childPane.setBackground(Background.fill(Color.WHITE));

            // Limpa e adiciona o novo conteúdo
            mainPane.getChildren().clear();
            mainPane.getChildren().add(childPane);

            // Configuração para redimensionamento automático
            configurarRedimensionamento(childPane, mainPane);

        } catch (Exception e) {
            e.fillInStackTrace();
            System.err.println("Falha ao carregar: " + path);
        }
    }

    private static void configurarRedimensionamento(AnchorPane childPane, AnchorPane mainPane) {

        childPane.setMinSize(0, 0);
        childPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Âncoras para preencher o container
        AnchorPane.setLeftAnchor(childPane, 0.0);
        AnchorPane.setRightAnchor(childPane, 0.0);
        AnchorPane.setTopAnchor(childPane, 0.0);
        AnchorPane.setBottomAnchor(childPane, 0.0);

        // Binding para redimensionamento responsivo
        childPane.prefWidthProperty().bind(mainPane.widthProperty());
        childPane.prefHeightProperty().bind(mainPane.heightProperty());
    }

}
