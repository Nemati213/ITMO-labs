package ru.itmo.nemat.managers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.itmo.nemat.models.Dragon;

import java.util.List;

public class ClientCollectionManager {
    private final ObservableList<Dragon> collection = FXCollections.observableArrayList();

    public ObservableList<Dragon> getCollection() {
        return collection;
    }

    public void setDragons(List<Dragon> dragons) {
        Platform.runLater(() -> collection.setAll(dragons));
    }
    public void addDragons(List<Dragon> dragons) {
        Platform.runLater(() -> collection.addAll(dragons));
    }
    public void deleteDragons(List<Dragon> dragons) {
        Platform.runLater(() -> collection.removeAll(dragons));
    }
    public void updateDragons(List<Dragon> dragons) {
        Platform.runLater(() -> {
            for (Dragon updatedDragon : dragons) {
                for (int i = 0; i < collection.size(); i++) {
                    if (collection.get(i).getId().equals(updatedDragon.getId())) {
                        collection.set(i, updatedDragon);
                        break;
                    }
                }
            }
        });
    }
}
