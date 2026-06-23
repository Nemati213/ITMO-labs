package ru.itmo.nemat.controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ru.itmo.nemat.handler.UIHandler;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.utils.LocalizationManager;

import java.util.HashMap;
import java.util.Map;

public class MapController extends AbstractMainController {

    @FXML private AnchorPane mapContainer;
    @FXML private Pane mapPane;
    @FXML private Label mouseCoordsLabel;
    @FXML private Label mapTitleLabel;
    @FXML private Button deleteSelectedButton;


    private Circle selectedCircle = null;
    private Dragon selectedDragon = null;
    private final ContextMenu canvasMenu = new ContextMenu();
    private final Map<Long, Circle> dragonNodes = new HashMap<>();

    @Override
    public void setUiHandler(UIHandler uiHandler) {
        super.setUiHandler(uiHandler);

        filteredData.addListener((ListChangeListener<Dragon>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    for (Dragon d : change.getRemoved()) {
                        removeDragonWithAnimation(d);
                    }
                }
                if (change.wasAdded()) {
                    for (Dragon d : change.getAddedSubList()) {
                        addDragonWithAnimation(d);
                    }
                }
            }
        });

        for (Dragon dragon : filteredData) {
            addDragonWithAnimation(dragon);
        }
    }

    private double[] calculateDragonCoords(double mouseX, double mouseY) {
        double radius = 15;
        double padding = radius + 5;
        double usableWidth = mapPane.getWidth() - 2 * padding;
        double usableHeight = mapPane.getHeight() - 2 * padding;

        double rawX = ((mouseX - padding) / usableWidth) * 200.0;
        double rawY = (((mapPane.getHeight() - padding) - mouseY) / usableHeight) * 100.0;

        double finalX = Math.max(0, Math.min(200, Math.round(rawX * 10.0) / 10.0));
        double finalY = Math.max(0, Math.min(100, Math.round(rawY * 10.0) / 10.0));

        return new double[]{finalX, finalY};
    }

    @FXML
    @Override
    public void initialize() {
        super.initialize();

        if (mapTitleLabel != null) {
            mapTitleLabel.textProperty().bind(LocalizationManager.getBinding("map.title"));
            setRegionWidth(mapTitleLabel, 80);
        }
        if (deleteSelectedButton != null) deleteSelectedButton.textProperty().bind(LocalizationManager.getBinding("main.table.delete"));
        mouseCoordsLabel.textProperty().bind(LocalizationManager.getBinding("map.coords.initial"));
        setRegionWidth(mouseCoordsLabel, 90);
        mouseCoordsLabel.setAlignment(javafx.geometry.Pos.CENTER);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(mapPane.widthProperty());
        clip.heightProperty().bind(mapPane.heightProperty());
        mapPane.setClip(clip);

        MenuItem addItem = new MenuItem();
        addItem.textProperty().bind(LocalizationManager.getBinding("map.menu.add_here"));

        canvasMenu.getItems().setAll(addItem);
        canvasMenu.setAutoHide(true);
        mapPane.setCursor(javafx.scene.Cursor.CROSSHAIR);

        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> recalculatePositions());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> recalculatePositions());

        mapPane.setOnMouseClicked(event -> {
            if (canvasMenu.isShowing()) canvasMenu.hide();

            if (event.getButton() == MouseButton.SECONDARY && event.getTarget() == mapPane) {
                double[] coords = calculateDragonCoords(event.getX(), event.getY());
                coords[1] = Math.min(100, Math.round(coords[1]));
                addItem.setOnAction(e -> openAddDialog(coords[0], (long)coords[1]));
                canvasMenu.show(mapPane, event.getScreenX(), event.getScreenY());
            }
        });

        mapPane.setOnMouseMoved(event -> {
            double[] coords = calculateDragonCoords(event.getX(), event.getY());
            String formattedX = LocalizationManager.formatNumber(coords[0]);
            String formattedY = LocalizationManager.formatNumber(coords[1]);
            mouseCoordsLabel.textProperty().unbind();
            mouseCoordsLabel.setText("X: " + formattedX + " | Y: " + formattedY);
        });

        mapPane.setOnMouseExited(e -> mouseCoordsLabel.textProperty().bind(LocalizationManager.getBinding("map.status.outside")));
    }

    private Color getColorForOwner(String ownerLogin) {
        if (ownerLogin == null || ownerLogin.isEmpty()) return Color.GRAY;

        int hash = ownerLogin.hashCode();

        double hue = Math.abs(hash % 3600) / 10.0;
        double saturation = 0.5 + (Math.abs((hash / 320) % 40) / 100.0);
        return Color.hsb(hue, saturation, 0.9);
    }

    private void addDragonWithAnimation(Dragon dragon) {
        if (dragonNodes.containsKey(dragon.getId())) return;

        double radius = 15;
        Circle visualDragon = new Circle(radius);

        visualDragon.setFill(getColorForOwner(dragon.getOwnerLogin()));
        visualDragon.setStroke(Color.BLACK);
        visualDragon.setStrokeWidth(2);
        visualDragon.setCursor(javafx.scene.Cursor.HAND);

        updateNodePosition(visualDragon, dragon);

        Tooltip info = new Tooltip();
        String typeStr = (dragon.getType() != null ? dragon.getType().toString() : LocalizationManager.getString("form.prompt.type"));

        info.setText(
                LocalizationManager.getString("map.tooltip.id") + " " + dragon.getId() + "\n" +
                        LocalizationManager.getString("map.tooltip.name") + " " + dragon.getName() + "\n" +
                        LocalizationManager.getString("map.tooltip.type") + " " + typeStr + "\n" +
                        LocalizationManager.getString("map.tooltip.owner") + " " + dragon.getOwnerLogin()
        );

        info.getStyleClass().add("map-tooltip");
        info.setShowDelay(Duration.millis(100));
        Tooltip.install(visualDragon, info);

        ContextMenu contextMenu = createContextMenu(dragon);

        visualDragon.setOnMouseClicked(event -> {
            event.consume();
            if (canvasMenu.isShowing()) canvasMenu.hide();
            if (event.getButton() == MouseButton.PRIMARY) {
                selectCircle(visualDragon, dragon);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(visualDragon, event.getScreenX(), event.getScreenY());
            }
        });

        dragonNodes.put(dragon.getId(), visualDragon);
        mapPane.getChildren().add(visualDragon);

        visualDragon.setScaleX(0);
        visualDragon.setScaleY(0);

        ScaleTransition st = new ScaleTransition(Duration.millis(400), visualDragon);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        st.play();
    }

    private void removeDragonWithAnimation(Dragon dragon) {
        Circle visualDragon = dragonNodes.remove(dragon.getId());
        if (visualDragon != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), visualDragon);
            st.setToX(0);
            st.setToY(0);
            st.setOnFinished(e -> mapPane.getChildren().remove(visualDragon));
            st.play();

            if (selectedDragon != null && selectedDragon.getId().equals(dragon.getId())) {
                selectedCircle = null;
                selectedDragon = null;
            }
        }
    }

    private void recalculatePositions() {
        if (mapPane.getWidth() == 0 || mapPane.getHeight() == 0) return;
        for (Dragon dragon : filteredData) {
            Circle visualDragon = dragonNodes.get(dragon.getId());
            if (visualDragon != null) {
                updateNodePosition(visualDragon, dragon);
            }
        }
    }

    private void updateNodePosition(Circle visualDragon, Dragon dragon) {
        if (mapPane.getWidth() == 0 || mapPane.getHeight() == 0) return;

        double radius = 15;
        double padding = radius + 5;
        double usableWidth = mapPane.getWidth() - 2 * padding;
        double usableHeight = mapPane.getHeight() - 2 * padding;

        double x = padding + (dragon.getCoordinates().getX() / 200.0) * usableWidth;
        double y = (mapPane.getHeight() - padding) - (dragon.getCoordinates().getY() / 100.0) * usableHeight;

        visualDragon.setCenterX(x);
        visualDragon.setCenterY(y);
    }

    private void selectCircle(Circle circle, Dragon dragon) {
        if (selectedCircle != null) {
            selectedCircle.setStroke(Color.BLACK);
            selectedCircle.setStrokeWidth(2);
            selectedCircle.setEffect(null);
        }

        selectedCircle = circle;
        selectedDragon = dragon;

        selectedCircle.setStroke(Color.valueOf("#FF9AA2"));
        selectedCircle.setStrokeWidth(4);
        selectedCircle.setEffect(new DropShadow(15, Color.valueOf("#FF9AA2")));
    }

    @FXML
    protected void onDeleteSelectedClick() {
        if (selectedDragon == null) {
            DialogManager.showInfo(LocalizationManager.getString("main.dialog.remove_char.title"),
                    LocalizationManager.getString("map.dialog.select_info"));
            return;
        }

        String currentLogin = uiHandler.getSessionHandler().getLogin();
        if (!selectedDragon.getOwnerLogin().equals(currentLogin)) {
            DialogManager.showError(LocalizationManager.getString("main.dialog.denied.title"),
                    LocalizationManager.getString("main.dialog.denied.content"));
            return;
        }

        runCommand("remove_by_id", new String[]{String.valueOf(selectedDragon.getId())}, () -> null);
    }

    private ContextMenu createContextMenu(Dragon dragon) {
        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("context-menu");

        MenuItem editItem = new MenuItem();
        editItem.textProperty().bind(LocalizationManager.getBinding("main.context.edit"));
        MenuItem deleteItem = new MenuItem();
        deleteItem.textProperty().bind(LocalizationManager.getBinding("main.context.delete"));

        editItem.setOnAction(e -> {
            if (!dragon.getOwnerLogin().equals(uiHandler.getSessionHandler().getLogin())) {
                DialogManager.showError(LocalizationManager.getString("main.dialog.denied.title"),
                        LocalizationManager.getString("main.dialog.denied.content"));
                return;
            }
            openEditDialog(dragon);
        });

        deleteItem.setOnAction(e -> {
            if (!dragon.getOwnerLogin().equals(uiHandler.getSessionHandler().getLogin())) {
                DialogManager.showError("main.dialog.denied.title", "main.dialog.denied.title");
                return;
            }
            runCommand("remove_by_id", new String[]{String.valueOf(dragon.getId())}, () -> null);
        });

        menu.getItems().addAll(editItem, deleteItem);
        return menu;
    }
}