package ru.itmo.nemat.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.itmo.nemat.handler.UIHandler;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.utils.LocalizationManager;
import ru.itmo.nemat.utils.UIUtils;

import java.io.IOException;

public class AuthController {

    private UIHandler uiHandler;

    @FXML private ProgressBar progressBar;
    @FXML private SVGPath languageIcon;
    @FXML private Label aboutUsLink;
    @FXML private Label appTitleLabel;
    @FXML private TextField loginField;
    @FXML private Label loginLabel;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordLabel;

    @FXML private Label loginInstructionLabel;
    @FXML private Button loginButton;
    @FXML private Label noAccountLabel;
    @FXML private Label registerLink;

    @FXML private Label regInstructionLabel;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label confirmPasswordLabel;
    @FXML private Button registerButton;
    @FXML private Label alreadyHaveAccountLabel;
    @FXML private Label logInLink;

    public AuthController() {}

    public void setUiHandler(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @FXML
    public void initialize() {
        if (aboutUsLink != null) aboutUsLink.textProperty().bind(LocalizationManager.getBinding("auth.about"));
        if (appTitleLabel != null) appTitleLabel.textProperty().bind(LocalizationManager.getBinding("auth.app_title"));
        if (loginLabel != null) loginLabel.textProperty().bind(LocalizationManager.getBinding("auth.label.login"));
        if (loginField != null) loginField.promptTextProperty().bind(LocalizationManager.getBinding("auth.prompt.login"));
        if (passwordLabel != null) passwordLabel.textProperty().bind(LocalizationManager.getBinding("auth.label.password"));
        if (passwordField != null) passwordField.promptTextProperty().bind(LocalizationManager.getBinding("auth.prompt.password"));

        if (loginInstructionLabel != null) loginInstructionLabel.textProperty().bind(LocalizationManager.getBinding("auth.instruction"));
        if (loginButton != null) loginButton.textProperty().bind(LocalizationManager.getBinding("auth.button.login"));
        if (noAccountLabel != null) noAccountLabel.textProperty().bind(LocalizationManager.getBinding("auth.no_account"));
        if (registerLink != null) registerLink.textProperty().bind(LocalizationManager.getBinding("auth.signup"));

        if (regInstructionLabel != null) regInstructionLabel.textProperty().bind(LocalizationManager.getBinding("auth.instruction_reg"));
        if (confirmPasswordField != null) confirmPasswordField.promptTextProperty().bind(LocalizationManager.getBinding("auth.prompt.confirm_password"));
        if (confirmPasswordLabel != null) confirmPasswordLabel.textProperty().bind(LocalizationManager.getBinding("auth.label.confirm_password"));
        if (registerButton != null) registerButton.textProperty().bind(LocalizationManager.getBinding("auth.button.register"));
        if (alreadyHaveAccountLabel != null) alreadyHaveAccountLabel.textProperty().bind(LocalizationManager.getBinding("auth.already_have_account"));
        if (logInLink != null) logInLink.textProperty().bind(LocalizationManager.getBinding("auth.login_link"));

        UIUtils.setupLanguageMenu(languageIcon);

    }

    @FXML
    private void loginButtonClick(MouseEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();

        executeAuthTask(event, () -> {
            uiHandler.loginHandler(login, password);
        });
    }

    @FXML
    private void registerButtonClick(MouseEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        executeAuthTask(event, () -> {
            uiHandler.registerHandler(login, password, confirm);
        });
    }

    private void executeAuthTask(MouseEvent event, AuthAction action) {
        setLoadingState(true);

        new Thread(() -> {
            try {
                action.run();

                Platform.runLater(() -> {
                    try {
                        openMainWindow(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoadingState(false);
                    DialogManager.showError("ui.dialog.error.title", e.getMessage());
                });
            }
        }).start();
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisible(isLoading);
        progressBar.setProgress(isLoading ? -1.0 : 0);

        if (loginButton != null) loginButton.setDisable(isLoading);
        if (registerButton != null) registerButton.setDisable(isLoading);
    }

    @FXML
    private void handleSignUpLinkClick(MouseEvent event) throws IOException {
        switchScene("/fxml/register.fxml", event);
    }

    @FXML
    private void handleLoginLinkClick(MouseEvent event) throws IOException {
        switchScene("/fxml/login.fxml", event);
    }

    private void switchScene(String fxmlPath, MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        AuthController controller = loader.getController();
        controller.setUiHandler(uiHandler);

        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    private void openMainWindow(MouseEvent event) throws IOException {
        Parent currentRoot = progressBar.getScene().getRoot();
        Stage stage = (Stage) currentRoot.getScene().getWindow();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
                Parent newRoot = loader.load();
                TableController mainController = loader.getController();
                mainController.setUiHandler(this.uiHandler);

                newRoot.setOpacity(0);
                stage.getScene().setRoot(newRoot);
                stage.setMaximized(true);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fadeOut.play();
    }

    @FunctionalInterface
    private interface AuthAction {
        void run() throws Exception;
    }

    @FXML
    private void aboutUsCLicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about_us.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
}