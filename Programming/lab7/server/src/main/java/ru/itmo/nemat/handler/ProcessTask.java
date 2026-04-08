package ru.itmo.nemat.handler;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;
import ru.itmo.nemat.utils.Serializer;

import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Process task.
 */
public class ProcessTask implements Runnable {

    private static final Logger logger = Logger.getLogger(ProcessTask.class.getName());

    private final Request request;
    private final Socket socket;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private final AuthService authService;
    private final ExecutorService sendPool;
    private final DatabaseManager databaseManager;
    private final DragonDAO dragonDAO;
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Process task.
     *
     * @param request           the request
     * @param socket            the socket
     * @param commandManager    the command manager
     * @param authService       the auth service
     * @param sendPool          the send pool
     * @param serializer        the serializer
     * @param databaseManager   the database manager
     * @param dragonDAO         the dragon dao
     * @param collectionManager the collection manager
     */
    public ProcessTask(Request request, Socket socket, ServerCommandManager commandManager,
                       AuthService authService, ExecutorService sendPool, Serializer serializer,
                       DatabaseManager databaseManager, DragonDAO dragonDAO, CollectionManager collectionManager) {
        this.request = request;
        this.socket = socket;
        this.commandManager = commandManager;
        this.authService = authService;
        this.sendPool = sendPool;
        this.serializer = serializer;
        this.databaseManager = databaseManager;
        this.dragonDAO = dragonDAO;
        this.collectionManager = collectionManager;
    }

    @Override
    public void run() {
        Response response;
        logger.info("Начало обработки команды '" + request.getCommandName() + "' для пользователя: " + request.getLogin());

        try (Connection connection = databaseManager.getConnection()) {

            response = commandManager.execute(request, connection, dragonDAO, collectionManager, authService);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при обработке запроса", e);
            response = new Response("Ошибка базы данных на сервере", ResponseStatus.ERROR);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Непредвиденная ошибка сервера", e);
            response = new Response("Внутренняя ошибка сервера", ResponseStatus.ERROR);
        }

        logger.info("Команда '" + request.getCommandName() + "' обработана. Статус: " + response.getStatus());
        sendPool.submit(new SendTask(response, socket, serializer));
    }
}
