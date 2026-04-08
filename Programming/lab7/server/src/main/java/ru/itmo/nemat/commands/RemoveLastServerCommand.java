package ru.itmo.nemat.commands;



import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * The type Remove last server command.
 */
public class RemoveLastServerCommand extends ServerCommand {

    /**
     * Instantiates a new Remove last server command.
     */
    public RemoveLastServerCommand() {
        super("remove_last", "удалить последний элемент коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", ResponseStatus.ERROR);
        }

        Long id = collectionManager.findLastByUser(request.getLogin());
        if (id == null)
            return new Response("У пользователя " + request.getLogin() + " нет драконов",  ResponseStatus.OK);

        if(dragonDAO.deleteDragonById(connection, id, request.getLogin()) != ResponseStatus.OK)
            return new Response("Ошибка при удалении с базы данных", ResponseStatus.ERROR);

        collectionManager.removeById(id, request.getLogin());

        return new Response("Последний элемент успешно удалён", ResponseStatus.OK);
    }
}