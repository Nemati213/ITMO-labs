package ru.itmo.nemat.commands;


import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Clear server command.
 */
public class ClearServerCommand extends ServerCommand {


    /**
     * Instantiates a new Clear server command.
     */
    public ClearServerCommand() {
        super("clear", "очистить коллекцию");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", ResponseStatus.ERROR);
        }

        if(dragonDAO.clearUserCollection(connection, request.getLogin()) == ResponseStatus.OK) {
            collectionManager.clearCollection(request.getLogin());
            return new Response("Коллекция успешно очищена.", ResponseStatus.OK);
        }
        return new Response("Ошибка при очистке коллекции", ResponseStatus.ERROR);
    }
}