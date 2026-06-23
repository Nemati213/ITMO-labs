package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

public class RemoveByIdServerCommand extends ServerCommand {

    public RemoveByIdServerCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его идентификатору");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length != 1) {
            return new Response("response.error.one_arg_required", ResponseStatus.ERROR);
        }

        try {
            long id = Long.parseLong(request.getArgs()[0]);

            ResponseStatus status = dragonService.deleteDragonById(connection, request.getLogin(), id);

            if (status == ResponseStatus.OK) {
                return new Response("response.remove_id.success:" + id, ResponseStatus.OK);
            }

            if (status == ResponseStatus.PERMISSION_DENIED) {
                return new Response("response.remove_id.denied", ResponseStatus.PERMISSION_DENIED);
            }

            if (status == ResponseStatus.NOT_FOUND) {
                return new Response("response.remove_id.not_found", ResponseStatus.NOT_FOUND);
            }

            return new Response("response.remove_id.error", ResponseStatus.ERROR);

        } catch (NumberFormatException e) {
            return new Response("response.error.id_not_number", ResponseStatus.VALIDATION_ERROR);
        }
    }
}