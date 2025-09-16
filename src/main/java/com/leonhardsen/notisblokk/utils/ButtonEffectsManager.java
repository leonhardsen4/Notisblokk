package com.leonhardsen.notisblokk.utils;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class ButtonEffectsManager {

    // Cores pastéis originais
    public static final String NUMERO_COR = "#FFFACD";
    public static final String OPERACAO_COR = "#ADD8E6";
    public static final String IGUAL_COR = "#98FB98";
    public static final String LIMPAR_COR = "#FFB6C1";
    public static final String ESPECIAL_COR = "#3D3D66";

    // Cores vibrantes para hover
    public static final String NUMERO_HOVER = "#FFF380";
    public static final String OPERACAO_HOVER = "#87CEEB";
    public static final String IGUAL_HOVER = "#7CFC00";
    public static final String LIMPAR_HOVER = "#FF69B4";
    public static final String ESPECIAL_HOVER = "#5D5DAA";

    public static void aplicarEfeitosBotao(Button botao, String tipo) {
        if (botao == null) return;

        String corOriginal = obterCorOriginal(tipo);
        String corHover = obterCorHover(tipo);
        String corTexto = obterCorTexto(tipo);

        // Configurar cor original
        botao.setStyle("-fx-background-color: " + corOriginal + "; -fx-text-fill: " + corTexto + "; -fx-background-radius: 5;");

        // Efeito hover
        botao.hoverProperty().addListener((observable, oldValue, isHovering) -> {
            if (isHovering) {
                botao.setStyle("-fx-background-color: " + corHover + "; -fx-text-fill: " + corTexto + "; -fx-background-radius: 5; -fx-cursor: hand;");
            } else {
                botao.setStyle("-fx-background-color: " + corOriginal + "; -fx-text-fill: " + corTexto + "; -fx-background-radius: 5;");
            }
        });

        // Efeito de clique animado
        botao.setOnMousePressed(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), botao);
            scaleTransition.setToX(0.95);
            scaleTransition.setToY(0.95);
            scaleTransition.play();
        });

        botao.setOnMouseReleased(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), botao);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }

    private static String obterCorOriginal(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "numero" -> NUMERO_COR;
            case "operacao" -> OPERACAO_COR;
            case "igual" -> IGUAL_COR;
            case "limpar" -> LIMPAR_COR;
            case "especial" -> ESPECIAL_COR;
            default -> OPERACAO_COR;
        };
    }

    private static String obterCorHover(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "numero" -> NUMERO_HOVER;
            case "operacao" -> OPERACAO_HOVER;
            case "igual" -> IGUAL_HOVER;
            case "limpar" -> LIMPAR_HOVER;
            case "especial" -> ESPECIAL_HOVER;
            default -> OPERACAO_HOVER;
        };
    }

    private static String obterCorTexto(String tipo) {
        return "especial".equalsIgnoreCase(tipo) ? "#ffffff" : "#4682B4";
    }

    // Buscar botão por texto no GridPane
    private static Button buscarBotaoPorTexto(GridPane gridPane, String texto) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Button botao) {
                if (texto.equals(botao.getText())) {
                    return botao;
                }
            }
        }
        return null;
    }

    // Configurar botões
    public static void configurarBotoesGrid(GridPane gridPane) {
        if (gridPane == null) return;

        for (Node node : gridPane.getChildren()) {
            if (node instanceof Button botao) {
                String texto = botao.getText();
                String tipo = determinarTipoBotao(texto);
                aplicarEfeitosBotao(botao, tipo);
            }
        }
    }

    // Configurar botões especiais
    public static void configurarBotoesEspeciais(HBox botoesEspeciais) {
        if (botoesEspeciais == null) return;

        for (Node node : botoesEspeciais.getChildren()) {
            if (node instanceof Button botao) {
                aplicarEfeitosBotao(botao, "especial");
            }
        }
    }

    // Determinar o tipo do botão baseado no texto
    private static String determinarTipoBotao(String texto) {
        if (texto == null) return "operacao";

        // Botões numéricos
        if (texto.matches("[0-9]") || ".".equals(texto)) {
            return "numero";
        }
        // Botão limpar
        else if ("C".equals(texto)) {
            return "limpar";
        }
        // Botão igual
        else if ("=".equals(texto)) {
            return "igual";
        }
        // Botões de operação
        else if ("+".equals(texto) || "-".equals(texto) || "×".equals(texto) ||
                "÷".equals(texto) || "%".equals(texto)) {
            return "operacao";
        }
        // Default
        else {
            return "operacao";
        }
    }
}