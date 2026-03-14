package ru.itmo.nemat.lab6.client.managers;

import ru.itmo.nemat.lab6.client.exceptions.ServerUnavailableException;
import ru.itmo.nemat.lab6.client.network.TCPClient;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;
import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.utils.Serializer;

import java.io.IOException;
import java.net.SocketAddress;

public class InteractionManager {
    private final SocketAddress address;
    private final Serializer serializer;
    private final OutputPrinter printer;
    private final TCPClient tcpClient;


    public InteractionManager(SocketAddress address, Serializer serializer, OutputPrinter printer) {
        this.address = address;
        this.serializer = serializer;
        this.printer = printer;
        this.tcpClient = new TCPClient(address);
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
                    printer.printError("Разрыв связи во время обмена данными. Попытка " + currentAttempt + " из " + maxExchangeAttempts);

                    if (currentAttempt < maxExchangeAttempts) {
                        try { Thread.sleep(2000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                    }
                }
                finally {
                    tcpClient.disconnect();
                }
            }

            throw new ServerUnavailableException("Не удалось доставить запрос после нескольких разрывов связи.");

        } catch (IOException | ClassNotFoundException e) {
            printer.printError("Критическая ошибка сериализации: " + e.getMessage());
            throw new RuntimeException("Невозможно обработать данные", e);
        }
    }
    private void connectWithRetry() throws ServerUnavailableException {
        int attempts = 0;
        int maxAttempts = 5;

        while (attempts < maxAttempts) {
            try {
                tcpClient.connect();
                return;
            } catch (IOException e) {
                attempts++;
                printer.printError("Сервер недоступен. Попытка переподключения " + attempts + " из " + maxAttempts + "...");
                if (attempts >= maxAttempts) throw new ServerUnavailableException("Не удалось связаться с сервером");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException q) {
                    Thread.currentThread().interrupt();
                    throw new ServerUnavailableException("Ожидание переподключения было прервано");
                }
            }
        }
    }


}
