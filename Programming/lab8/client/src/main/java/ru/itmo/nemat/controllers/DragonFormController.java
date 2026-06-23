package ru.itmo.nemat.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.models.Color;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.models.DragonType;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.DragonFactory;
import ru.itmo.nemat.utils.LocalizationManager;

import java.util.HashMap;
import java.util.Map;

public class DragonFormController {

    @FXML private Label titleLabel;

    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField xField;
    @FXML private TextField yField;

    @FXML private ComboBox<Color> colorCombo;
    @FXML private ComboBox<DragonType> typeCombo;
    @FXML private ComboBox<DragonCharacter> characterCombo;

    @FXML private TextField depthField;
    @FXML private TextField treasuresField;

    @FXML private Button cancelButton;
    @FXML private Button okButton;

    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label xLabel;
    @FXML private Label yLabel;
    @FXML private Label colorLabel;
    @FXML private Label typeLabel;
    @FXML private Label characterLabel;
    @FXML private Label depthLabel;
    @FXML private Label treasuresLabel;


    private boolean confirmed = false;
    private DragonDTO dragonDTO;

    public void initialize() {
        colorCombo.setItems(FXCollections.observableArrayList(Color.values()));
        ObservableList<DragonType> types = FXCollections.observableArrayList(DragonType.values());
        types.add(0, null);
        typeCombo.setItems(types);
        ObservableList<DragonCharacter> chars = FXCollections.observableArrayList(DragonCharacter.values());
        chars.add(0, null);
        characterCombo.setItems(chars);

        setupEnumComboBox(colorCombo, "color");
        setupEnumComboBox(typeCombo, "dragon_type");
        setupEnumComboBox(characterCombo, "dragon_character");

        titleLabel.textProperty().bind(LocalizationManager.getBinding("form.title"));
        nameLabel.textProperty().bind(LocalizationManager.getBinding("form.label.name"));
        ageLabel.textProperty().bind(LocalizationManager.getBinding("form.label.age"));
        xLabel.textProperty().bind(LocalizationManager.getBinding("form.label.x"));
        yLabel.textProperty().bind(LocalizationManager.getBinding("form.label.y"));
        colorLabel.textProperty().bind(LocalizationManager.getBinding("form.label.color"));
        typeLabel.textProperty().bind(LocalizationManager.getBinding("form.label.type"));
        characterLabel.textProperty().bind(LocalizationManager.getBinding("form.label.character"));
        depthLabel.textProperty().bind(LocalizationManager.getBinding("form.label.depth"));
        treasuresLabel.textProperty().bind(LocalizationManager.getBinding("form.label.treasures"));

        nameField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.name"));
        ageField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.age"));
        xField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.x"));
        yField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.y"));
        colorCombo.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.color"));
        typeCombo.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.type"));
        characterCombo.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.character"));
        depthField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.depth"));
        treasuresField.promptTextProperty().bind(LocalizationManager.getBinding("form.prompt.treasures"));

        cancelButton.textProperty().bind(LocalizationManager.getBinding("form.button.cancel"));
        okButton.textProperty().bind(LocalizationManager.getBinding("form.button.ok"));
    }

    @FXML
    private void onOkClick() {
        try {
            Map<String, String> data = getFormData();

            this.dragonDTO = DragonFactory.getInstance().buildFromStrings(
                    data.get("name"),
                    data.get("age"),
                    data.get("x"),
                    data.get("y"),
                    data.get("color"),
                    data.get("type"),
                    data.get("character"),
                    data.get("hasCave"),
                    data.get("depth"),
                    data.get("treasures")
            );

            confirmed = true;
            closeWindow();
        } catch (Exception e) {
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }

    @FXML
    private void onCancelClick() {
        confirmed = false;
        closeWindow();
    }

    public void setEditMode() {
        titleLabel.textProperty().bind(LocalizationManager.getBinding("form.title.edit"));

    }

    public void setDragonData(Dragon dragon) {
        nameField.setText(dragon.getName());
        ageField.setText(String.valueOf(dragon.getAge()));
        xField.setText(String.valueOf(dragon.getCoordinates().getX()));
        yField.setText(String.valueOf(dragon.getCoordinates().getY()));
        colorCombo.setValue(dragon.getColor());
        typeCombo.setValue(dragon.getType());
        characterCombo.setValue(dragon.getCharacter());

        if (dragon.getCave() != null) {
            depthField.setText(String.valueOf(dragon.getCave().getDepth()));
            treasuresField.setText(String.valueOf(dragon.getCave().getNumberOfTreasures()));
        } else {
            depthField.setText("");
            treasuresField.setText("");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public DragonDTO getDragonDTO() {
        return dragonDTO;
    }

    public void setCoordinates(String x, String y) {
        xField.setText(x);
        yField.setText(y);
    }

    private Map<String, String> getFormData() {
        Map<String, String> data = new HashMap<>();

        data.put("name", nameField.getText());
        data.put("age", ageField.getText());
        data.put("x", xField.getText());
        data.put("y", yField.getText());

        data.put("color", colorCombo.getValue() != null ? colorCombo.getValue().toString() : "");
        data.put("type", typeCombo.getValue() != null ? typeCombo.getValue().toString() : "");
        data.put("character", characterCombo.getValue() != null ? characterCombo.getValue().toString() : "");

        String depth = depthField.getText();
        String treasures = treasuresField.getText();

        boolean hasCave = (!depth.trim().isEmpty() && !depth.trim().equals("null")) ||
                (!treasures.trim().isEmpty() && !treasures.trim().equals("null"));

        data.put("hasCave", hasCave ? "yes" : "no");
        data.put("depth", depth);
        data.put("treasures", treasures);

        return data;
    }

    private <T extends Enum<T>> void setupEnumComboBox(ComboBox<T> comboBox, String prefix) {
        comboBox.setConverter(new javafx.util.StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) return LocalizationManager.getString("form.prompt.type");
                return LocalizationManager.getString(prefix + "." + object.name());
            }
            @Override
            public T fromString(String string) { return null; }
        });

        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    textProperty().bind(LocalizationManager.getBinding("form.prompt.type"));
                } else {
                    textProperty().bind(LocalizationManager.getBinding(prefix + "." + item.name()));
                }
            }
        });

        LocalizationManager.bundleProperty().addListener((obs, old, newVal) -> {
            T selected = comboBox.getValue();
            comboBox.setValue(null);
            comboBox.setValue(selected);
        });
    }
}