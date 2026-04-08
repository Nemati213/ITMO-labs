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
 * The type Remove by id server command.
 */
public class RemoveByIdServerCommand extends ServerCommand {

    /**
     * Instantiates a new Remove by id server command.
     */
    public RemoveByIdServerCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его идентификатору");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length != 1) {
            return new Response("Команда ожидает ровно 1 аргумент!", ResponseStatus.ERROR);
        }

        try {
            long id = Long.parseLong(request.getArgs()[0]);

            ResponseStatus status = dragonDAO.deleteDragonById(connection, id, request.getLogin());

            if(status == ResponseStatus.OK) {
                collectionManager.removeById(id, request.getLogin());
                return new Response("Дракон с ID = " + id + " успешно удалён", ResponseStatus.OK);

            }

            if(status == ResponseStatus.PERMISSION_DENIED)
                return new Response("У вас нет прав на удаление этого дракона", ResponseStatus.OK);

            if (status == ResponseStatus.NOT_FOUND) {
                return new Response("Дракон с таким ID не найден",  ResponseStatus.NOT_FOUND);
            }

            return new Response("Ошибка при удалении с базы данных", ResponseStatus.ERROR);

        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом!", ResponseStatus.VALIDATION_ERROR);
        }
    }
}