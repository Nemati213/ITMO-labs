package ru.itmo.nemat.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.utils.LocalizationManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutUsController {

    @FXML private Label headerLabel;
    @FXML private Text descriptionText;
    @FXML private Label questionsLabel;
    @FXML private Label sourceCodeLabel;
    @FXML private Button closeButton;
    @FXML private Hyperlink githubLink;
    @FXML private Hyperlink emailLink;
    @FXML private SVGPath emailIcon;
    @FXML private SVGPath githubIcon;

    @FXML
    public void initialize() {
        headerLabel.textProperty().bind(LocalizationManager.getBinding("about.title"));
        descriptionText.textProperty().bind(LocalizationManager.getBinding("about.description"));
        questionsLabel.textProperty().bind(LocalizationManager.getBinding("about.questions"));
        emailLink.textProperty().bind(LocalizationManager.getBinding("about.email"));
        sourceCodeLabel.textProperty().bind(LocalizationManager.getBinding("about.sourceCode"));
        githubLink.textProperty().bind(LocalizationManager.getBinding("about.github"));
        closeButton.textProperty().bind(LocalizationManager.getBinding("about.button.ok"));
    }

    @FXML
    private void emailCLicked() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(emailLink.getText());
        clipboard.setContent(content);

        String oldText = emailLink.getText();
        emailLink.textProperty().unbind();
        emailLink.setText(LocalizationManager.getString("ui.about.copied"));

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            emailLink.textProperty().bind(LocalizationManager.getBinding("about.email"));
        });
        pause.play();
    }

    @FXML
    private void githubCLicked() {
        try {
            Desktop desktop = Desktop.getDesktop();
            String github = "https://github.com/nemati213";
            URI uri = new URI(github);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            DialogManager.showError(LocalizationManager.getString("ui.error.title"), e.getMessage());
        }
    }

    @FXML
    private void onOkClicked() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}