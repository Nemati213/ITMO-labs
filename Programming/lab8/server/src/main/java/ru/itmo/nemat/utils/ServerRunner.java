package ru.itmo.nemat.utils;



import ru.itmo.nemat.handlers.ReadTask;
import ru.itmo.nemat.managers.ClientManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.network.TCPServer;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRunner {
    private static final Logger logger = Logger.getLogger(ServerRunner.class.getName());

    private final TCPServer tcpServer;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private final UserService userService;
    private final DatabaseManager databaseManager;
    private final DragonService dragonService;
    private final ClientManager clientManager;

    private final ExecutorService processPool;
    private final ExecutorService sendPool;

    private volatile boolean isRunning = true;

    public ServerRunner(TCPServer tcpServer, ServerCommandManager commandManager,
                        Serializer serializer, UserService userService,DatabaseManager databaseManager, DragonService dragonService,
                        ExecutorService processPool, ExecutorService sendPool, ClientManager clientManager) {
        this.tcpServer = tcpServer;
        this.commandManager = commandManager;
        this.serializer = serializer;
        this.userService = userService;
        this.databaseManager = databaseManager;
        this.dragonService = dragonService;
        this.processPool = processPool;
        this.sendPool = sendPool;
        this.clientManager = clientManager;
    }

    public void run() {
        try {
            tcpServer.start();
            logger.info("Сервер запущен");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось запустить сервер (возможно, порт уже занят)", e);
            stop();
            return;
        }

        new Thread(this::handleConsoleInput).start();

        while (isRunning) {
            try {
                Socket clientSocket = tcpServer.acceptConnection();
                if (clientSocket != null) {
                    clientManager.addClient(clientSocket);
                    logger.info("Новое подключение: " + clientSocket.getInetAddress());

                    ReadTask readTask = new ReadTask(
                            clientSocket, commandManager, serializer,
                            processPool, userService, sendPool,
                            databaseManager, dragonService, clientManager
                    );
                    new Thread(readTask).start();
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                if (isRunning) {
                    logger.log(Level.WARNING, "Ошибка при подключении клиента", e);
                }
            }
        }

        stop();
    }
    private void handleConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning && scanner.hasNextLine()) {
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("exit")) {
                logger.info("Получена консольная команда на завершение работы");
                isRunning = false;
                stop();
            } else if (line.equals("save")) {
                logger.info("Команда save проигнорирована: данные и так в БД");
            }
        }
    }

    private void stop() {
        isRunning = false;
        tcpServer.stop();
        processPool.shutdown();
        logger.info("Ожидание завершения работы потоков выполнения...");
        sendPool.shutdown();
        logger.info("Сервер остановлен.");
    }
}