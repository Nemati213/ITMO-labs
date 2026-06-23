package ru.itmo.nemat.controllers;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.nemat.handler.ScriptExecutor;
import ru.itmo.nemat.handler.UIHandler;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.models.Color;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.LocalizationManager;
import ru.itmo.nemat.utils.OutputPrinter;
import ru.itmo.nemat.utils.UIUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractMainController {

    protected UIHandler uiHandler;
    protected FilteredList<Dragon> filteredData;
    protected String searchText = "";
    protected boolean onlyMyDragons = false;
    protected Color selectedColorFilter = null;

    @FXML protected ImageView logoImage;
    @FXML protected Button cardViewButton;
    @FXML protected Button tableViewButton;
    @FXML protected SVGPath languageIcon;
    @FXML protected SVGPath logoutIcon;

    @FXML protected Button executeScriptButton;
    @FXML protected Button historyButton;
    @FXML protected Button helpButton;
    @FXML protected Button infoButton;
    @FXML protected Button clearButton;
    @FXML protected Button removeLastButton;
    @FXML protected Button removeByCharacterButton;

    @FXML protected Label usernameBadge;
    @FXML protected TextField searchField;
    @FXML protected Button filtersButton;
    @FXML protected Button addDragonButton;

    @FXML protected Label appTitleLabel;

    @FXML
    public void initialize() {
        if (appTitleLabel != null) appTitleLabel.textProperty().bind(LocalizationManager.getBinding("main.app_title"));
        if (cardViewButton != null) cardViewButton.textProperty().bind(LocalizationManager.getBinding("main.toggle.card"));
        if (tableViewButton != null) tableViewButton.textProperty().bind(LocalizationManager.getBinding("main.toggle.table"));
        if (executeScriptButton != null) executeScriptButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.script"));
        if (historyButton != null) historyButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.history"));
        if (helpButton != null) helpButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.help"));
        if (infoButton != null) infoButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.info"));
        if (clearButton != null) clearButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.clear"));
        if (removeLastButton != null) removeLastButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.rem_last"));
        if (removeByCharacterButton != null) removeByCharacterButton.textProperty().bind(LocalizationManager.getBinding("main.cmd.rem_char"));
        if (searchField != null) searchField.promptTextProperty().bind(LocalizationManager.getBinding("main.table.search"));
        if (filtersButton != null) filtersButton.textProperty().bind(LocalizationManager.getBinding("main.table.filters"));
        if (addDragonButton != null) addDragonButton.textProperty().bind(LocalizationManager.getBinding("main.table.add"));
        UIUtils.setupLanguageMenu(languageIcon);

        List<Button> commandButtons = Arrays.asList(
                executeScriptButton, historyButton, helpButton,
                infoButton, clearButton, removeLastButton, removeByCharacterButton
        );


        for (Button btn : commandButtons) {
            if (btn != null) {
                setRegionWidth(btn, 175);
                btn.setWrapText(true);
                btn.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            }
        }

        setRegionWidth(usernameBadge, 80);
        usernameBadge.setAlignment(javafx.geometry.Pos.CENTER);
        setRegionWidth(cardViewButton, 80);
        setRegionWidth(tableViewButton, 80);
        HBox.setHgrow(cardViewButton, Priority.ALWAYS);
        HBox.setHgrow(tableViewButton, Priority.ALWAYS);

        setRegionWidth(addDragonButton, 130);
        setRegionWidth(filtersButton, 80);

    }

    protected void setRegionWidth(Region region, int width) {
        region.setMinWidth(width);
        region.setPrefWidth(width);
    }

    public void setUiHandler(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
        usernameBadge.setText(uiHandler.getSessionHandler().getLogin());

        ObservableList<Dragon> masterData = uiHandler.getCollectionManager().getCollection();
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.searchText = newValue;
            updatePredicate();
        });
    }

    protected void updatePredicate() {
        filteredData.setPredicate(dragon -> {
            boolean matchesSearch = true;
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                matchesSearch = java.util.stream.Stream.of(
                                String.valueOf(dragon.getId()),
                                dragon.getName(),
                                String.valueOf(dragon.getAge()),
                                dragon.getCoordinates() != null ? String.valueOf(dragon.getCoordinates().getX()) : "",
                                dragon.getCoordinates() != null ? String.valueOf(dragon.getCoordinates().getY()) : "",
                                dragon.getCreationDate() != null ? LocalizationManager.formatDate(dragon.getCreationDate()) : "",
                                dragon.getColor() != null ? LocalizationManager.getString("color." + dragon.getColor().name()) : "",
                                dragon.getType() != null ? LocalizationManager.getString("dragon_type." + dragon.getType().name()) : "",
                                dragon.getCharacter() != null ? LocalizationManager.getString("dragon_character." + dragon.getCharacter().name()) : "",
                                dragon.getCave() != null ? String.valueOf(dragon.getCave().getDepth()) : "",
                                dragon.getCave() != null ? String.valueOf(dragon.getCave().getNumberOfTreasures()) : "",
                                dragon.getOwnerLogin()
                        )
                        .filter(java.util.Objects::nonNull)
                        .map(String::toLowerCase)
                        .anyMatch(field -> field.contains(lowerCaseFilter));
            }

            boolean matchesOwner = true;
            if (onlyMyDragons) {
                matchesOwner = dragon.getOwnerLogin().equals(uiHandler.getSessionHandler().getLogin());
            }

            boolean matchesColor = true;
            if (selectedColorFilter != null) {
                matchesColor = dragon.getColor() == selectedColorFilter;
            }

            return matchesSearch && matchesOwner && matchesColor;
        });
    }


    protected void runCommand(String commandName, String[] args, Supplier<DragonDTO> supplier) {
        new Thread(() -> {
            try {
                uiHandler.apply(commandName, args, supplier);
            } catch (Exception e) {
                DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
            }
        }).start();
    }

    @FXML protected void onHelpClick() { runCommand("help", new String[0], () -> null); }
    @FXML protected void onHistoryClick() { runCommand("history", new String[0], () -> null); }
    @FXML protected void onInfoClick() { runCommand("info", new String[0], () -> null); }
    @FXML protected void onClearClick() { runCommand("clear", new String[0], () -> null); }
    @FXML protected void onRemoveLastClick() { runCommand("remove_last", new String[0], () -> null); }

    @FXML
    protected void onAddClick() {
        openAddDialog(null, null);
    }

    protected void openAddDialog(Object prefillX, Object prefillY) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dragon_form.fxml"));
            Parent root = loader.load();
            DragonFormController controller = loader.getController();

            if (prefillX != null && prefillY != null) {
                controller.setCoordinates(prefillX.toString(), prefillY.toString());
            }

            Stage stage = new Stage();
            stage.titleProperty().bind(LocalizationManager.getBinding("form.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isConfirmed()) {
                var dragonDTO = controller.getDragonDTO();
                runCommand("add", new String[0], () -> dragonDTO);
            }
        } catch (Exception e) {
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }

    @FXML
    protected void onRemoveByCharacterClick() {
        List<DragonCharacter> characters = Arrays.asList(DragonCharacter.values());
        ChoiceDialog<DragonCharacter> dialog = new ChoiceDialog<>(characters.get(0), characters);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("my-dialog");
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

        dialog.titleProperty().bind(LocalizationManager.getBinding("main.dialog.remove_char.title"));
        dialog.headerTextProperty().bind(LocalizationManager.getBinding("main.dialog.remove_char.header"));
        dialog.contentTextProperty().bind(LocalizationManager.getBinding("main.dialog.remove_char.content"));
        dialog.setGraphic(null);

        Optional<DragonCharacter> result = dialog.showAndWait();
        result.ifPresent(selected -> {
            runCommand("remove_all_by_character", new String[]{selected.name()}, () -> null);
        });
    }

    protected void openEditDialog(Dragon dragon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dragon_form.fxml"));
            Parent root = loader.load();
            DragonFormController controller = loader.getController();

            controller.setEditMode();
            controller.setDragonData(dragon);

            Stage stage = new Stage();
            stage.titleProperty().bind(LocalizationManager.getBinding("form.title.edit"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isConfirmed()) {
                var dragoDTO = controller.getDragonDTO();
                runCommand("update", new String[]{String.valueOf(dragon.getId())}, () -> dragoDTO);
            }
        } catch (Exception e) {
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }


    @FXML
    protected void onFiltersClick() {
        ContextMenu filterMenu = new ContextMenu();
        filterMenu.getStyleClass().add("context-menu");

        CheckMenuItem myDragonsItem = new CheckMenuItem();
        myDragonsItem.textProperty().bind(LocalizationManager.getBinding("main.menu.my_dragons"));
        myDragonsItem.setSelected(onlyMyDragons);
        myDragonsItem.setOnAction(e -> {
            onlyMyDragons = myDragonsItem.isSelected();
            updatePredicate();
        });

        Menu colorMenu = new Menu();
        colorMenu.textProperty().bind(LocalizationManager.getBinding("main.menu.color_filter"));
        for (Color color : Color.values()) {
            RadioMenuItem colorItem = new RadioMenuItem();
            colorItem.textProperty().bind(LocalizationManager.getBinding("color." + color.name()));
            colorItem.setSelected(color == selectedColorFilter);
            colorItem.setOnAction(e -> {
                selectedColorFilter = color;
                updatePredicate();
            });
            colorMenu.getItems().add(colorItem);
        }

        MenuItem clearItem = new MenuItem();
        clearItem.textProperty().bind(LocalizationManager.getBinding("main.menu.clear_filters"));
        clearItem.setOnAction(e -> {
            onlyMyDragons = false;
            selectedColorFilter = null;
            updatePredicate();
        });

        filterMenu.getItems().addAll(myDragonsItem, colorMenu, new SeparatorMenuItem(), clearItem);
        filterMenu.show(filtersButton, Side.BOTTOM, 0, 0);
    }

    @FXML
    protected void onExecuteScriptClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LocalizationManager.getString("main.chooser.script.title"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LocalizationManager.getString("main.chooser.txt_files"), "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LocalizationManager.getString("main.chooser.all_files"), "*.*"));

        File selectedFile = fileChooser.showOpenDialog(usernameBadge.getScene().getWindow());
        if (selectedFile == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/script_console.fxml"));
            Parent root = loader.load();
            ScriptConsoleController consoleController = loader.getController();

            consoleController.setFileName(selectedFile.getName());
            OutputPrinter consolePrinter = consoleController.getPrinter();

            Stage stage = new Stage();
            stage.titleProperty().bind(LocalizationManager.getBinding("terminal.title"));
            stage.setScene(new Scene(root));
            stage.show();

            OutputPrinter originalPrinter = uiHandler.getServerListener().getPrinter();
            uiHandler.getServerListener().setPrinter(consolePrinter);

            stage.setOnHidden(event -> uiHandler.getServerListener().setPrinter(originalPrinter));

            ScriptExecutor executor = new ScriptExecutor(
                    uiHandler.getInteractionManager(),
                    uiHandler.getCommandManager(),
                    consolePrinter,
                    uiHandler.getSessionHandler()
            );

            new Thread(() -> {
                try {
                    executor.execute(selectedFile.getAbsolutePath());
                } catch (Exception e) {
                    consolePrinter.printError("ui.error.script_critical:" + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }

    @FXML
    protected void onTableViewClick() {
        if (this instanceof TableController) return;
        switchView("/fxml/table.fxml");
    }

    @FXML
    protected void onCardViewClick() {
        if (this instanceof MapController) return;
        switchView("/fxml/map.fxml");
    }

    private void switchView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            AbstractMainController controller = loader.getController();
            controller.setUiHandler(this.uiHandler);
            Stage stage = (Stage) usernameBadge.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }

    @FXML
    protected void onLogoutClick() {
        uiHandler.stopNetworking();
        uiHandler.getSessionHandler().clear();
        uiHandler.getCollectionManager().getCollection().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            AuthController authController = loader.getController();
            authController.setUiHandler(this.uiHandler);

            Stage stage = (Stage) usernameBadge.getScene().getWindow();
            Scene authScene = new Scene(root);
            stage.setScene(authScene);
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            DialogManager.showError(LocalizationManager.getString("ui.dialog.error.title"), e.getMessage());
        }
    }
}