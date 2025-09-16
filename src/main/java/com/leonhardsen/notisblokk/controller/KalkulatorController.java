package com.leonhardsen.notisblokk.controller;

import com.leonhardsen.notisblokk.utils.ButtonEffectsManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class KalkulatorController implements Initializable {

    @FXML public AnchorPane rootPane;
    @FXML public SplitPane splitPane;
    @FXML private TextField display;
    @FXML private GridPane gridPane;
    @FXML private HBox botoesEspeciais;
    @FXML private TextArea historyArea;

    private double currentNumber = 0;
    private double previousNumber = 0;
    private String currentOperation = "";
    private boolean newInput = true;
    private boolean modoMonetario = false;

    private static final double MINIMUM_WAGE = 1518.00;
    private static final double UFESP = 37.02;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        display.setText("0");
        historyArea.setText("Histórico de Cálculos:\n");
        historyArea.setEditable(false);
        historyArea.setWrapText(true);

        aplicarEfeitosBotoes();
    }

    private void aplicarEfeitosBotoes() {
        try {
            javafx.application.Platform.runLater(() -> {
                if (gridPane != null) {
                    ButtonEffectsManager.configurarBotoesGrid(gridPane);
                }
                if (botoesEspeciais != null) {
                    ButtonEffectsManager.configurarBotoesEspeciais(botoesEspeciais);
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao aplicar efeitos nos botões: " + e.getMessage());
        }
    }

    private void addToHistory(String entry) {
        historyArea.appendText(entry + "\n");
        historyArea.setScrollTop(Double.MAX_VALUE);
    }

    @FXML
    private void handleDigit(ActionEvent event) {
        Button button = (Button) event.getSource();
        String digit = button.getText();

        if (newInput) {
            display.setText(digit);
            newInput = false;
            modoMonetario = false; // Sai do modo monetário ao digitar novo número
        } else {
            display.setText(display.getText() + digit);
        }

        addToHistory("Digito: " + digit);
    }

    @FXML
    private void handleDecimal(ActionEvent event) {
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
            addToHistory("Decimal adicionado");
        }
    }

    @FXML
    private void handleOperation(ActionEvent event) {
        Button button = (Button) event.getSource();
        String operation = button.getText();

        if (!newInput) {
            calculate();
        }

        currentOperation = operation;
        previousNumber = parseMonetario(display.getText());
        newInput = true;
        modoMonetario = false; // Operação reseta modo monetário

        addToHistory("Operação: " + operation);
    }

    @FXML
    private void handleEquals(ActionEvent event) {
        if (!newInput && !currentOperation.isEmpty()) {
            calculate();
            addToHistory("Resultado: " + display.getText());
            currentOperation = "";
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        display.setText("0");
        currentNumber = 0;
        previousNumber = 0;
        currentOperation = "";
        newInput = true;
        modoMonetario = false;
        addToHistory("Display limpo");
    }

    @FXML
    private void handlePercentage(ActionEvent event) {
        if (!newInput) {
            double percentage = parseMonetario(display.getText());

            // Se houver uma operação pendente, calcula a porcentagem do número anterior
            if (!currentOperation.isEmpty() && previousNumber != 0) {
                double value = previousNumber * (percentage / 100);
                display.setText(formatMonetario(value));
                newInput = true;
                modoMonetario = true;
                addToHistory(percentage + "% de " + formatMonetario(previousNumber) + " = " + formatMonetario(value));
            } else {
                // Comportamento original: converte para decimal
                double value = percentage / 100;
                display.setText(formatNumber(value));
                newInput = true;
                addToHistory("Porcentagem calculada: " + display.getText());
            }
        }
    }

    @FXML
    private void handleSimpleInterest(ActionEvent event) {
        Optional<Pair<Double, Pair<Double, Double>>> result = showInterestDialog("Juros Simples");

        result.ifPresent(data -> {
            double principal = data.getKey();
            double rate = data.getValue().getKey();
            double time = data.getValue().getValue();

            // Juros simples: M = P * (1 + i * t)
            double amount = principal * (1 + (rate / 100) * time);

            display.setText(formatMonetario(amount));
            newInput = true;
            modoMonetario = true;

            String historyEntry = String.format("Juros Simples: P=%.2f, i=%.2f%% a.m., t=%.0f meses → Montante=%.2f",
                    principal, rate, time, amount);
            addToHistory(historyEntry);
        });
    }

    @FXML
    private void handleCompoundInterest(ActionEvent event) {
        Optional<Pair<Double, Pair<Double, Double>>> result = showInterestDialog("Juros Compostos");

        result.ifPresent(data -> {
            double principal = data.getKey();
            double rate = data.getValue().getKey();
            double time = data.getValue().getValue();

            // Juros compostos: M = P * (1 + i)^t
            double amount = principal * Math.pow(1 + rate/100, time);

            display.setText(formatMonetario(amount));
            newInput = true;
            modoMonetario = true;

            String historyEntry = String.format("Juros Compostos: P=%.2f, i=%.2f%% a.m., t=%.0f meses → Montante=%.2f",
                    principal, rate, time, amount);
            addToHistory(historyEntry);
        });
    }

    @FXML
    private void handleMinimumWage(ActionEvent event) {
        display.setText(formatMonetario(MINIMUM_WAGE));
        newInput = true;
        modoMonetario = true;
        addToHistory("Salário Mínimo: " + formatMonetario(MINIMUM_WAGE));
    }

    @FXML
    private void handleUfesp(ActionEvent event) {
        display.setText(formatMonetario(UFESP));
        newInput = true;
        modoMonetario = true;
        addToHistory("UFESP: " + formatMonetario(UFESP));
    }

    private void calculate() {
        currentNumber = parseMonetario(display.getText());

        switch (currentOperation) {
            case "+":
                currentNumber = previousNumber + currentNumber;
                break;
            case "-":
                currentNumber = previousNumber - currentNumber;
                break;
            case "×":
                currentNumber = previousNumber * currentNumber;
                break;
            case "÷":
                if (currentNumber != 0) {
                    currentNumber = previousNumber / currentNumber;
                } else {
                    display.setText("Erro");
                    addToHistory("Erro: Divisão por zero");
                    return;
                }
                break;
        }

        // Se estava em modo monetário ou o resultado deve ser monetário, formata com 2 casas
        if (modoMonetario || currentOperation.equals("×") || currentOperation.equals("÷") ||
                previousNumber == MINIMUM_WAGE || previousNumber == UFESP ||
                currentNumber == MINIMUM_WAGE || currentNumber == UFESP) {
            display.setText(formatMonetario(currentNumber));
            modoMonetario = true;
        } else {
            display.setText(formatNumber(currentNumber));
        }

        newInput = true;
    }

    private String formatNumber(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.format("%.10f", number).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }

    // Formatar valores monetários
    private String formatMonetario(double number) {
        return String.format("%.2f", number).replace(".", ",");
    }

    private double parseMonetario(String value) {
        try {
            // Remove possíveis espaços e substitui vírgula por ponto
            return Double.parseDouble(value.replace(" ", "").replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Optional<Pair<Double, Pair<Double, Double>>> showInterestDialog(String title) {
        Dialog<Pair<Double, Pair<Double, Double>>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Insira os parâmetros para " + title);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        TextField principalField = new TextField();
        principalField.setPromptText("Capital inicial");

        TextField rateField = new TextField();
        rateField.setPromptText("Taxa de juros mensal (%)");

        TextField timeField = new TextField();
        timeField.setPromptText("Tempo (meses)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Capital inicial:"), 0, 0);
        grid.add(principalField, 1, 0);
        grid.add(new Label("Taxa de juros mensal (%):"), 0, 1);
        grid.add(rateField, 1, 1);
        grid.add(new Label("Tempo (meses):"), 0, 2);
        grid.add(timeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        principalField.textProperty().addListener((observable, oldValue, newValue) ->
                validateInputs(principalField, rateField, timeField, okButton));
        rateField.textProperty().addListener((observable, oldValue, newValue) ->
                validateInputs(principalField, rateField, timeField, okButton));
        timeField.textProperty().addListener((observable, oldValue, newValue) ->
                validateInputs(principalField, rateField, timeField, okButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    double principal = Double.parseDouble(principalField.getText());
                    double rate = Double.parseDouble(rateField.getText());
                    double time = Double.parseDouble(timeField.getText());

                    if (principal > 0 && rate > 0 && time > 0) {
                        return new Pair<>(principal, new Pair<>(rate, time));
                    }
                } catch (NumberFormatException e) {
                    // Não deve acontecer devido à validação
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void validateInputs(TextField principalField, TextField rateField, TextField timeField, javafx.scene.Node okButton) {
        try {
            double principal = parseMonetario(principalField.getText());
            double rate = parseMonetario(rateField.getText());
            double time = parseMonetario(timeField.getText());

            okButton.setDisable(principal <= 0 || rate <= 0 || time <= 0);
        } catch (NumberFormatException e) {
            okButton.setDisable(true);
        }
    }

    public void setCurrentStage() {
    }
}