package ru.itmo.nemat.managers;

import ru.itmo.nemat.exceptions.ServerUnavailableException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.network.TCPClient;
import ru.itmo.nemat.utils.OutputPrinter;
import ru.itmo.nemat.utils.Serializer;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * The type Interaction manager.
 */
public class InteractionManager {
    private final SocketAddress address;
    private final Serializer serializer;
    private final OutputPrinter printer;
    private final TCPClient tcpClient;


    /**
     * Instantiates a new Interaction manager.
     *
     * @param address    the address
     * @param serializer the serializer
     * @param printer    the printer
     */
    public InteractionManager(SocketAddress address, Serializer serializer, OutputPrinter printer) {
        this.address = address;
        this.serializer = serializer;
        this.printer = printer;
        this.tcpClient = new TCPClient(address);
    }

    /**
     * Send and receive response.
     *
     * @param request the request
     * @return the response
     * @throws ServerUnavailableException the server unavailable exception
     */
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
