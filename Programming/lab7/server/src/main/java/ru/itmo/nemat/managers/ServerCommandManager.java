package ru.itmo.nemat.managers;



import ru.itmo.nemat.commands.ServerCommand;
import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * The type Server command manager.
 */
public class ServerCommandManager {
    private static final Logger logger = Logger.getLogger(ServerCommandManager.class.getName());
    private static final int HISTORY_LIMIT = 50;

    private final Map<String, ServerCommand> commands = new HashMap<>();


    /**
     * Register.
     *
     * @param command the command
     */
    public void register(ServerCommand command) {
        commands.put(command.getName(), command);
        logger.config("Зарегистрирована команда: " + command.getName());
    }

    /**
     * Gets commands.
     *
     * @return the commands
     */
    public Map<String, ServerCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }


    /**
     * Execute response.
     *
     * @param request           the request
     * @param connection        the connection
     * @param dragonDAO         the dragon dao
     * @param collectionManager the collection manager
     * @param authService       the auth service
     * @return the response
     * @throws SQLException the sql exception
     */
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        String commandName = request.getCommandName();
        ServerCommand command = commands.get(commandName);

        String login = request.getLogin();
        String password = request.getPassword();

        if (command == null) {
            logger.warning("Попытка выполнить неизвестную команду: " + commandName);
            return new Response("Команда '" + commandName + "' не найдена на сервере.", ResponseStatus.NOT_FOUND);
        }
        if(command.isProtected() && authService.authenticate(connection, login, password) != ResponseStatus.AUTH_SUCCESS) {
            logger.warning("Попытка исполнить команду без входа");
            return new Response("Авторизация не удалась, пожалуйста, проверьте логин и пароль",  ResponseStatus.AUTH_ERROR);
        }

        logger.info("Выполнение команды: " + commandName);

        return command.execute(request, connection, dragonDAO, collectionManager, authService);
    }

}