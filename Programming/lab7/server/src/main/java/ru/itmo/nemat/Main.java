package ru.itmo.nemat;



import ru.itmo.nemat.commands.*;
import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.database.UserDAO;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.network.TCPServer;
import ru.itmo.nemat.utils.AuthService;
import ru.itmo.nemat.utils.Serializer;
import ru.itmo.nemat.utils.ServerRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The type Main.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        setupLogger();

        try {
            String portEnv = System.getenv("SERVER_PORT");
            int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;

            var tcpServer = new TCPServer(port);
            var collectionManager = new CollectionManager();
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

            var dataBaseManager = new DatabaseManager();
            var dragonDAO = new DragonDAO();
            var userDAO = new UserDAO();

            var authService = new AuthService(userDAO);
            try(Connection connection = dataBaseManager.getConnection()){
                Stack<Dragon> dragons = dragonDAO.getDragons(connection);
                collectionManager.loadCollection(dragons);
            }

            logger.info("Попытка запустить сервер на порту: " + port);
            var runner = new ServerRunner(tcpServer, commandManager, collectionManager, serializer, authService, dataBaseManager, dragonDAO);

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