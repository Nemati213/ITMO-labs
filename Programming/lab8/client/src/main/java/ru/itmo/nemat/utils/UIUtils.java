package ru.itmo.nemat.utils;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;

import java.util.Locale;
import java.util.Map;

public class UIUtils {

    public static void setupLanguageMenu(Node icon) {
        if (icon == null) return;

        ContextMenu languageMenu = new ContextMenu();

        languageMenu.getStyleClass().add("context-menu");

        ToggleGroup group = new ToggleGroup();

        for (Map.Entry<String, Locale> entry : LocalizationManager.getSupportedLocales().entrySet()) {
            RadioMenuItem item = new RadioMenuItem(entry.getKey());
            item.setToggleGroup(group);

            item.getStyleClass().add("radio-menu-item");

            if (Locale.getDefault().getLanguage().equals(entry.getValue().getLanguage())) {
                item.setSelected(true);
            }

            item.setOnAction(e -> LocalizationManager.setLocale(entry.getValue()));
            languageMenu.getItems().add(item);
        }

        icon.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {

                if (icon.getScene() != null) {
                    String cssPath = UIUtils.class.getResource("/styles/main.css").toExternalForm();
                    if (!icon.getScene().getStylesheets().contains(cssPath)) {
                        icon.getScene().getStylesheets().add(cssPath);
                    }
                }

                languageMenu.show(icon, Side.BOTTOM, 0, 0);
            }
        });

        icon.setCursor(javafx.scene.Cursor.HAND);
    }
}