package ru.itmo.nemat.managers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import ru.itmo.nemat.utils.LocalizationManager;

public class DialogManager {

    public static void showError(String titleKey, String messageKey) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle(LocalizationManager.getString(titleKey));
            alert.setHeaderText(null);

            alert.setContentText(LocalizationManager.format(messageKey));

            setStyle(alert);
            alert.showAndWait();
        });
    }

    public static void showInfo(String titleKey, String messageKey) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(LocalizationManager.getString(titleKey));
            alert.setHeaderText(null);

            String content = LocalizationManager.format(messageKey);

            if (content.split("\n").length > 5) {
                TextArea textArea = new TextArea(content);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px;");

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);

                alert.getDialogPane().setContent(textArea);
                alert.getDialogPane().setPrefSize(800, 400);
            } else {
                alert.setContentText(content);
            }

            setStyle(alert);
            alert.showAndWait();
        });
    }
    private static void setStyle(Alert alert) {
        var styleResource = DialogManager.class.getResource("/styles/main.css");
        if (styleResource != null) {
            alert.getDialogPane().getStylesheets().add(styleResource.toExternalForm());
        }
        alert.getDialogPane().getStyleClass().add("my-dialog");
    }
}