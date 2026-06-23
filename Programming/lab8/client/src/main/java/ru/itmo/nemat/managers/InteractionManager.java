package ru.itmo.nemat.managers;

import ru.itmo.nemat.exceptions.ServerUnavailableException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.network.TCPClient;
import ru.itmo.nemat.utils.OutputPrinter;
import ru.itmo.nemat.utils.Serializer;

import java.io.IOException;

public class InteractionManager {
    private final Serializer serializer;
    private final TCPClient tcpClient;

    public InteractionManager(String host, int port, Serializer serializer) {
        this.serializer = serializer;
        this.tcpClient = new TCPClient(host, port);
    }

    public Response sendAndReceive(Request request) throws ServerUnavailableException {
        int maxExchangeAttempts = 3;
        int currentAttempt = 0;

        try {
            byte[] data = serializer.serialize(request);

            while (currentAttempt < maxExchangeAttempts) {
                currentAttempt++;
                try {
                    connectWithRetry();

                    tcpClient.send(data);
                    byte[] responseData = tcpClient.receive();

                    return serializer.deserialize(responseData);

                } catch (IOException e) {
                    tcpClient.disconnect();
                    if (currentAttempt < maxExchangeAttempts) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            throw new ServerUnavailableException("ui.error.delivery_failed");

        } catch (IOException | ClassNotFoundException e) {
            throw new ServerUnavailableException("ui.error.delivery_failed");
        }
    }

    private void connectWithRetry() throws ServerUnavailableException {
        if (tcpClient.isConnected()) return;

        int attempts = 0;
        int maxAttempts = 5;

        while (attempts < maxAttempts) {
            try {
                tcpClient.connect();
                return;
            } catch (IOException e) {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new ServerUnavailableException("ui.error.server_unavailable_attempts:" + maxAttempts);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException q) {
                    Thread.currentThread().interrupt();
                    throw new ServerUnavailableException("ui.error.connection_interrupted");
                }
            }
        }
    }

    public void stop() {
        tcpClient.disconnect();
    }

    public void send(Request request) throws ServerUnavailableException {
        try {
            byte[] data = serializer.serialize(request);
            connectWithRetry();
            tcpClient.send(data);

        } catch (IOException e) {
            throw new ServerUnavailableException("ui.error.send_error:" + e.getMessage());
        }
    }
    public TCPClient getTcpClient() {
        return tcpClient;
    }
    public Serializer getSerializer() {
        return serializer;
    }
}