package ru.itmo.nemat.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ru.itmo.nemat.handler.UIHandler;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.models.*;
import ru.itmo.nemat.utils.LocalizationManager;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class TableController extends AbstractMainController {

    @FXML private VBox tableContainer;
    @FXML private Label tableTitleLabel;
    @FXML private Button deleteSelectedButton;
    @FXML private TableView<Dragon> mainTable;

    @FXML private TableColumn<Dragon, Long> colId;
    @FXML private TableColumn<Dragon, String> colName;
    @FXML private TableColumn<Dragon, Double> colX;
    @FXML private TableColumn<Dragon, Long> colY;
    @FXML private TableColumn<Dragon, Date> colCreationDate;
    @FXML private TableColumn<Dragon, Color> colColor;
    @FXML private TableColumn<Dragon, DragonType> colType;
    @FXML private TableColumn<Dragon, DragonCharacter> colCharacter;
    @FXML private TableColumn<Dragon, Long> colCaveDepth;
    @FXML private TableColumn<Dragon, Integer> colTreasures;
    @FXML private TableColumn<Dragon, String> colOwner;

    @Override
    public void setUiHandler(UIHandler uiHandler) {
        super.setUiHandler(uiHandler);

        SortedList<Dragon> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(mainTable.comparatorProperty());
        mainTable.setItems(sortedData);
    }

    @FXML
    @Override
    public void initialize() {
        super.initialize();
        if (tableTitleLabel != null) tableTitleLabel.textProperty().bind(LocalizationManager.getBinding("main.table.title"));
        if (deleteSelectedButton != null) deleteSelectedButton.textProperty().bind(LocalizationManager.getBinding("main.table.delete"));

        colId.textProperty().bind(LocalizationManager.getBinding("main.col.id"));
        colName.textProperty().bind(LocalizationManager.getBinding("main.col.name"));
        colX.textProperty().bind(LocalizationManager.getBinding("main.col.x"));
        colY.textProperty().bind(LocalizationManager.getBinding("main.col.y"));
        colCreationDate.textProperty().bind(LocalizationManager.getBinding("main.col.date"));
        colColor.textProperty().bind(LocalizationManager.getBinding("main.col.color"));
        colType.textProperty().bind(LocalizationManager.getBinding("main.col.type"));
        colCharacter.textProperty().bind(LocalizationManager.getBinding("main.col.char"));
        colCaveDepth.textProperty().bind(LocalizationManager.getBinding("main.col.depth"));
        colTreasures.textProperty().bind(LocalizationManager.getBinding("main.col.treasures"));
        colOwner.textProperty().bind(LocalizationManager.getBinding("main.col.owner"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerLogin"));
        colCreationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colCharacter.setCellValueFactory(new PropertyValueFactory<>("character"));

        colX.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCoordinates() != null ? cellData.getValue().getCoordinates().getX() : null));
        colY.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCoordinates() != null ? cellData.getValue().getCoordinates().getY() : null));
        colCaveDepth.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCave() != null ? cellData.getValue().getCave().getDepth() : null));
        colTreasures.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCave() != null ? cellData.getValue().getCave().getNumberOfTreasures() : null));
        setRegionWidth(deleteSelectedButton, 80);
        setRegionWidth(tableTitleLabel, 90);


        colCreationDate.setCellFactory(column -> new TableCell<Dragon, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(LocalizationManager.formatDate(item));
            }
        });

        colColor.setCellFactory(column -> new TableCell<Dragon, Color>() {
            @Override protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(LocalizationManager.getString("color." + item.name()));
            }
        });
        colType.setCellFactory(column -> new TableCell<Dragon, DragonType>() {
            @Override protected void updateItem(DragonType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(LocalizationManager.getString("dragon_type." + item.name()));
            }
        });
        colCharacter.setCellFactory(column -> new TableCell<Dragon, DragonCharacter>() {
            @Override protected void updateItem(DragonCharacter item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(LocalizationManager.getString("dragon_character." + item.name()));
            }
        });

        javafx.util.Callback<TableColumn<Dragon, ? extends Number>, TableCell<Dragon, ? extends Number>> numberCellFactory =
                column -> new TableCell<Dragon, Number>() {
                    @Override protected void updateItem(Number item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(item));
                    }
                };

        colX.setCellFactory((javafx.util.Callback) numberCellFactory);
        colY.setCellFactory((javafx.util.Callback) numberCellFactory);
        colCaveDepth.setCellFactory((javafx.util.Callback) numberCellFactory);
        colTreasures.setCellFactory((javafx.util.Callback) numberCellFactory);

        Label placeholder = new Label();
        placeholder.textProperty().bind(LocalizationManager.getBinding("main.table.empty"));
        mainTable.setPlaceholder(placeholder);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem();
        editItem.textProperty().bind(LocalizationManager.getBinding("main.context.edit"));

        MenuItem deleteItem = new MenuItem();
        deleteItem.textProperty().bind(LocalizationManager.getBinding("main.context.delete"));

        editItem.getStyleClass().add("context-menu-item");
        deleteItem.getStyleClass().add("context-menu-item");
        contextMenu.getItems().addAll(editItem, deleteItem);

        deleteItem.setOnAction(event -> {
            Dragon selectedDragon = mainTable.getSelectionModel().getSelectedItem();
            if (selectedDragon != null) {
                runCommand("remove_by_id", new String[]{String.valueOf(selectedDragon.getId())}, () -> null);
            }
        });

        editItem.setOnAction(event -> {
            Dragon selectedDragon = mainTable.getSelectionModel().getSelectedItem();
            if (selectedDragon != null) {
                String currentLogin = uiHandler.getSessionHandler().getLogin();
                if (!selectedDragon.getOwnerLogin().equals(currentLogin)) {
                    DialogManager.showError(
                            LocalizationManager.getString("main.dialog.denied.title"),
                            LocalizationManager.getString("main.dialog.denied.content")
                    );
                    return;
                }
                openEditDialog(selectedDragon);
            }
        });

        mainTable.setRowFactory(tv -> {
            TableRow<Dragon> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            row.setOnMouseClicked(event -> {
                if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY && !row.isEmpty()) {
                    mainTable.getSelectionModel().select(row.getItem());
                }
            });
            return row;
        });

        LocalizationManager.bundleProperty().addListener((observable, oldValue, newValue) -> {
            mainTable.refresh();
        });
    }

    @FXML
    private void onDeleteSelectedClick() {
        Dragon selectedDragon = mainTable.getSelectionModel().getSelectedItem();
        if (selectedDragon == null) {
            DialogManager.showInfo(
                    LocalizationManager.getString("ui.dialog.info.title"),
                    LocalizationManager.getString("main.dialog.delete_info.content")
            );
            return;
        }
        runCommand("remove_by_id", new String[]{String.valueOf(selectedDragon.getId())}, () -> null);
    }
}