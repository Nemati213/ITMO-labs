package ru.itmo.nemat.handlers;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;
import ru.itmo.nemat.utils.Serializer;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessTask implements Runnable {

    private static final Logger logger = Logger.getLogger(ProcessTask.class.getName());

    private final Request request;
    private final Socket socket;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private final UserService userService;
    private final ExecutorService sendPool;
    private final DatabaseManager databaseManager;
    private final DragonService dragonService;

    public ProcessTask(Request request, Socket socket, ServerCommandManager commandManager,
                       UserService userService, ExecutorService sendPool, Serializer serializer,
                       DatabaseManager databaseManager, DragonService dragonService) {
        this.request = request;
        this.socket = socket;
        this.commandManager = commandManager;
        this.userService = userService;
        this.sendPool = sendPool;
        this.serializer = serializer;
        this.databaseManager = databaseManager;
        this.dragonService = dragonService;
    }

    @Override
    public void run() {
        Response response;
        logger.info("Начало обработки команды '" + request.getCommandName() + "' для пользователя: " + request.getLogin());

        try (Connection connection = databaseManager.getConnection()) {

            response = commandManager.execute(request, connection, dragonService, userService);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при обработке запроса", e);
            response = new Response("error.server.database", ResponseStatus.ERROR);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Непредвиденная ошибка сервера", e);
            response = new Response("error.server.internal", ResponseStatus.ERROR);
        }

        logger.info("Команда '" + request.getCommandName() + "' обработана. Статус: " + response.getStatus());
        sendPool.submit(new SendTask(response, socket, serializer));
    }
}
