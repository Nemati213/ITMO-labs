package ru.itmo.nemat.lab6.server;

import ru.itmo.nemat.lab6.common.utils.Serializer;
import ru.itmo.nemat.lab6.server.commands.*;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;
import ru.itmo.nemat.lab6.server.managers.DumpManager;
import ru.itmo.nemat.lab6.server.managers.ServerCommandManager;
import ru.itmo.nemat.lab6.server.network.TCPServer;
import ru.itmo.nemat.lab6.server.utils.ServerRunner;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        setupLogger();

        try {
            String portEnv = System.getenv("SERVER_PORT");
            int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

            var tcpServer = new TCPServer(port);
            var dumpManager = new DumpManager("MY_LAB_FILE");
            var collectionManager = new CollectionManager(dumpManager);
            var commandManager = new ServerCommandManager();
            var serializer = new Serializer();

            commandManager.register(new InfoServerCommand(collectionManager));
            commandManager.register(new ShowServerCommand(collectionManager));
            commandManager.register(new AddServerCommand(collectionManager));
            commandManager.register(new UpdateServerCommand(collectionManager));
            commandManager.register(new RemoveByIdServerCommand(collectionManager));
            commandManager.register(new ClearServerCommand(collectionManager));
            commandManager.register(new RemoveLastServerCommand(collectionManager));
            commandManager.register(new SortServerCommand(collectionManager));
            commandManager.register(new RemoveAllByCharacterServerCommand(collectionManager));
            commandManager.register(new PrintAscendingServerCommand(collectionManager));
            commandManager.register(new PrintFieldDescendingAgeServerCommand(collectionManager));

            var runner = new ServerRunner(tcpServer, commandManager, collectionManager, serializer);

            logger.info("Инициализация завершена. Запуск главного цикла сервера.");
            runner.run();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Критическая ошибка при запуске сервера", e);
        }
    }

    private static void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Не удалось настроить файл логов. Логирование будет только в консоль.");
        }
    }
}