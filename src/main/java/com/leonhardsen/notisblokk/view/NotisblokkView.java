package com.leonhardsen.notisblokk.view;

import javafx.scene.layout.AnchorPane;

/**
 * Classe responsável por gerenciar a abertura da view principal da aplicação Notisblokk.
 * Utiliza o ViewManager para carregar e exibir a interface principal.
 *
 * @author Leonhardsen
 * @version 2.0
 */
public class NotisblokkView extends ViewManager {

    /**
     * Abre a view principal da aplicação no painel especificado.
     * Carrega o arquivo FXML "Notisblokk.fxml" e o exibe no mainPane.
     *
     * @param mainPane O painel principal onde a view será carregada
     * @throws RuntimeException Se ocorrer um erro durante o carregamento do FXML
     */
    public static void openView(AnchorPane mainPane) {
        try {
            setScreen("Notisblokk.fxml", "#rootPane", mainPane);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao abrir a view principal: " + e.getMessage(), e);
        }
    }
}