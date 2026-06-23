package ru.itmo.nemat.handler;

import javafx.application.Platform;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.ClientCollectionManager;
import ru.itmo.nemat.managers.DialogManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.network.TCPClient;
import ru.itmo.nemat.utils.OutputPrinter;
import ru.itmo.nemat.utils.Serializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerListener implements Runnable {

    private final TCPClient tcpClient;
    private final ClientCollectionManager collectionManager;
    private final Serializer serializer;
    private volatile OutputPrinter printer;
    private volatile boolean running = true;

    public void stopListener() {
        running = false;
    }


    public ServerListener(TCPClient tcpClient, ClientCollectionManager collectionManager, Serializer serializer, OutputPrinter printer) {
        this.tcpClient = tcpClient;
        this.collectionManager = collectionManager;
        this.serializer = serializer;
        this.printer = printer;
    }


    public void setPrinter(OutputPrinter printer) {
        this.printer = printer;
    }
    public OutputPrinter getPrinter() {
        return printer;
    }

    @Override
    public void run() {
        while (running) {
            try {
                byte[] responseData = tcpClient.receive();

                Response response =  serializer.deserialize(responseData);

                Platform.runLater(() -> handleResponse(response));

            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                if (running) {
                    Platform.runLater(() ->
                            printer.printError("ui.error.connection_lost")
                    );
                }
                break;
            }
        }
    }

    private void handleResponse(Response response) {
        switch (response.getStatus()) {

            case OK:

                if (response.getMessage() != null && !response.getMessage().isEmpty()) {
                    printer.println(response.getMessage());
                }
                if (response.getDragons() != null && !response.getDragons().isEmpty()) {
                    collectionManager.setDragons((List<Dragon>) response.getDragons());
                }
                break;

            case DRAGON_ADDED:
                if (response.getDragons() != null) {
                    collectionManager.addDragons((List<Dragon>) response.getDragons());
                }
                break;

            case DRAGON_REMOVED:
                if (response.getDragons() != null) {
                    var dragonsToRemove = response.getDragons();
                    collectionManager.deleteDragons((List<Dragon>) dragonsToRemove);
                }
                break;

            case DRAGON_UPDATED:
                if (response.getDragons() != null) {
                    var dragonsToUpdate = response.getDragons();

                    collectionManager.updateDragons((List<Dragon>) dragonsToUpdate);
                }
                break;

            case UPDATE_NOTIFICATION:

                if (response.getDragons() != null) {
                    collectionManager.setDragons((List<Dragon>) response.getDragons());
                }
                break;


            case ERROR:
            case NOT_FOUND:
            case PERMISSION_DENIED:
            case VALIDATION_ERROR:
                printer.printError(response.getMessage());
                break;

            case AUTH_SUCCESS:
            case AUTH_ERROR:
            case REGISTRATION_ERROR:
                break;

            default:
        }
    }
}