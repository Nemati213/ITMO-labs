package ru.itmo.nemat.controllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ru.itmo.nemat.utils.LocalizationManager;
import ru.itmo.nemat.utils.OutputPrinter;
import ru.itmo.nemat.utils.TextAreaPrinter;

import java.text.MessageFormat;

public class ScriptConsoleController {

    @FXML private Label terminalTitleLabel;
    @FXML private Label fileNameLabel;
    @FXML private TextArea consoleArea;
    @FXML private Button closeButton;

    private String currentFileName = "";
    private OutputPrinter textAreaPrinter;

    @FXML
    public void initialize() {
        textAreaPrinter = new TextAreaPrinter(consoleArea);

        terminalTitleLabel.textProperty().bind(LocalizationManager.getBinding("terminal.title"));
        closeButton.textProperty().bind(LocalizationManager.getBinding("terminal.button.close"));
    }

    public void setFileName(String fileName) {
        this.currentFileName = fileName;
        fileNameLabel.textProperty().bind(Bindings.createStringBinding(
                () -> MessageFormat.format(LocalizationManager.getString("terminal.file_prefix"), currentFileName),
                LocalizationManager.bundleProperty()
        ));
    }

    public OutputPrinter getPrinter() {
        return textAreaPrinter;
    }

    @FXML
    private void onCloseClick() {
        Stage stage = (Stage) consoleArea.getScene().getWindow();
        stage.close();
    }
}