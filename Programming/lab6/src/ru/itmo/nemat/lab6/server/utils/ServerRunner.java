package ru.itmo.nemat.lab6.server.utils;

import ru.itmo.nemat.lab6.common.utils.Serializer;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;
import ru.itmo.nemat.lab6.server.managers.ServerCommandManager;
import ru.itmo.nemat.lab6.server.network.RequestHandler;
import ru.itmo.nemat.lab6.server.network.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRunner {
    private static final Logger logger = Logger.getLogger(ServerRunner.class.getName());

    private final TCPServer tcpServer;
    private final ServerCommandManager commandManager;
    private final CollectionManager collectionManager;
    private final Serializer serializer;
    private boolean isRunning;

    public ServerRunner(TCPServer tcpServer, ServerCommandManager commandManager,
                        CollectionManager collectionManager, Serializer serializer) {
        this.tcpServer = tcpServer;
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.serializer = serializer;
    }

    public void run() {
        setupShutdownHook();
        isRunning = true;

        try {
            tcpServer.start();
            logger.info("Сервер запущен в однопоточном режиме (polling).");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (isRunning) {
                if (System.in.available() > 0) {
                    String line = reader.readLine();
                    if (line != null) {
                        handleConsoleInput(line);
                    }
                }

                Socket clientSocket = tcpServer.acceptConnection();
                if (clientSocket != null) {
                    logger.info("Новое подключение: " + clientSocket.getInetAddress());
                    new RequestHandler(clientSocket, commandManager, serializer).handle();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка в работе сервера", e);
        } finally {
            logger.info("Завершение работы. Сохранение коллекции...");
            collectionManager.saveCollection();
            tcpServer.stop();
        }
    }

    private void handleConsoleInput(String input) {
        String command = input.trim().toLowerCase();
        if (command.isEmpty()) return;

        switch (command) {
            case "save":
                collectionManager.saveCollection();
                System.out.println("Коллекция успешно сохранена вручную.");
                break;
            case "exit":
                isRunning = false;
                break;
            default:
                System.out.println("Неизвестная команда: " + command);
        }
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isRunning) {
                logger.info("Внезапное прерывание. Сохранение данных...");
                collectionManager.saveCollection();
            }
        }));
    }
}