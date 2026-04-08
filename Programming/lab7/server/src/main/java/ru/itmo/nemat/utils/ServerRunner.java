package ru.itmo.nemat.utils;



import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.handler.ReadTask;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.network.TCPServer;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Server runner.
 */
public class ServerRunner {
    private static final Logger logger = Logger.getLogger(ServerRunner.class.getName());

    private final TCPServer tcpServer;
    private final ServerCommandManager commandManager;
    private final CollectionManager collectionManager;
    private final Serializer serializer;
    private final AuthService authService;
    private final DatabaseManager databaseManager;
    private final DragonDAO dragonDAO;

    private final ExecutorService processPool = Executors.newCachedThreadPool();
    private final ExecutorService sendPool = Executors.newCachedThreadPool();

    private volatile boolean isRunning = true;

    /**
     * Instantiates a new Server runner.
     *
     * @param tcpServer         the tcp server
     * @param commandManager    the command manager
     * @param collectionManager the collection manager
     * @param serializer        the serializer
     * @param authService       the auth service
     * @param databaseManager   the database manager
     * @param dragonDAO         the dragon dao
     */
    public ServerRunner(TCPServer tcpServer, ServerCommandManager commandManager,
                        CollectionManager collectionManager, Serializer serializer,
                        AuthService authService, DatabaseManager databaseManager, DragonDAO dragonDAO) {
        this.tcpServer = tcpServer;
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.serializer = serializer;
        this.authService = authService;
        this.databaseManager = databaseManager;
        this.dragonDAO = dragonDAO;
    }

    /**
     * Run.
     */
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
                    logger.info("Новое подключение: " + clientSocket.getInetAddress());

                    ReadTask readTask = new ReadTask(
                            clientSocket, commandManager, serializer,
                            processPool, authService, sendPool,
                            databaseManager, dragonDAO, collectionManager
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