package ru.itmo.nemat.managers;


import ru.itmo.nemat.commands.ServerCommand;
import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServerCommandManager {
    private static final Logger logger = Logger.getLogger(ServerCommandManager.class.getName());
    private static final int HISTORY_LIMIT = 50;

    private final Map<String, ServerCommand> commands = new HashMap<>();


    public void register(ServerCommand command) {
        commands.put(command.getName(), command);
        logger.config("Зарегистрирована команда: " + command.getName());
    }

    public Map<String, ServerCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }


    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        String commandName = request.getCommandName();
        ServerCommand command = commands.get(commandName);

        String login = request.getLogin();
        String password = request.getPassword();

        if (command == null) {
            logger.warning("Попытка выполнить неизвестную команду: " + commandName);
            return new Response("response.error.command_not_found:" + commandName, ResponseStatus.NOT_FOUND);
        }
        if(command.isProtected() && userService.login(connection, login, password) != ResponseStatus.AUTH_SUCCESS) {
            logger.warning("Попытка исполнить команду без входа");
            return new Response("response.auth.failed", ResponseStatus.AUTH_ERROR);
        }

        logger.info("Выполнение команды: " + commandName);

        return command.execute(request, connection, dragonService, userService);
    }

}