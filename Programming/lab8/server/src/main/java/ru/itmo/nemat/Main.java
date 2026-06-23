package ru.itmo.nemat;



import ru.itmo.nemat.commands.*;
import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.database.UserDAO;
import ru.itmo.nemat.managers.ClientManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.network.TCPServer;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;
import ru.itmo.nemat.utils.Serializer;
import ru.itmo.nemat.utils.ServerRunner;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        setupLogger();

        try {

            var tcpServer = new TCPServer();
            var commandManager = new ServerCommandManager();
            var serializer = new Serializer();

            commandManager.register(new InfoServerCommand());
            commandManager.register(new ShowServerCommand());
            commandManager.register(new AddServerCommand());
            commandManager.register(new UpdateServerCommand());
            commandManager.register(new RemoveByIdServerCommand());
            commandManager.register(new ClearServerCommand());
            commandManager.register(new RemoveLastServerCommand());
            commandManager.register(new SortServerCommand());
            commandManager.register(new RemoveAllByCharacterServerCommand());
            commandManager.register(new PrintAscendingServerCommand());
            commandManager.register(new PrintFieldDescendingAgeServerCommand());
            commandManager.register(new LoginServerCommand());
            commandManager.register(new RegisterServerCommand());


            final ExecutorService processPool = Executors.newCachedThreadPool();
            final ExecutorService sendPool = Executors.newCachedThreadPool();


            var dataBaseManager = new DatabaseManager();
            var dragonDAO = new DragonDAO();
            var userDAO = new UserDAO();
            var clientManager = new ClientManager(sendPool, serializer);

            var userService = new UserService(userDAO);
            var dragonService = new DragonService(dragonDAO, userDAO,  clientManager);

            var runner = new ServerRunner(tcpServer, commandManager, serializer, userService, dataBaseManager, dragonService, processPool, sendPool, clientManager);

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