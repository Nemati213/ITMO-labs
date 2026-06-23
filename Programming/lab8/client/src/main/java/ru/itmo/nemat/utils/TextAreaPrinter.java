package ru.itmo.nemat.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaPrinter implements OutputPrinter {
    private final TextArea textArea;

    public TextAreaPrinter(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void println(Object o) {
        Platform.runLater(() -> textArea.appendText(LocalizationManager.format(o.toString()) + "\n"));
    }

    @Override
    public void print(Object o) {
        Platform.runLater(() -> textArea.appendText(LocalizationManager.format(o.toString())));
    }

    @Override
    public void printError(Object o) {
        Platform.runLater(() -> {
            String prefix = LocalizationManager.getString("ui.printer.error_prefix");
            textArea.appendText(prefix + " " + LocalizationManager.format(o.toString()) + "\n");
        });
    }
}