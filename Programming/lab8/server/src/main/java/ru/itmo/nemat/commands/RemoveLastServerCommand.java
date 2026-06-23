package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

public class RemoveLastServerCommand extends ServerCommand {

    public RemoveLastServerCommand() {
        super("remove_last", "удалить последний элемент коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("response.error.no_args:" + getName(), ResponseStatus.ERROR);
        }

        ResponseStatus status = dragonService.removeLast(connection, request.getLogin());

        if (status == ResponseStatus.OK) {
            return new Response("response.remove_last.success", ResponseStatus.OK);
        }

        if (status == ResponseStatus.NOT_FOUND) {
            return new Response("response.remove_last.not_found:" + request.getLogin(), ResponseStatus.OK);
        }

        return new Response("response.remove_last.error", ResponseStatus.ERROR);
    }
}