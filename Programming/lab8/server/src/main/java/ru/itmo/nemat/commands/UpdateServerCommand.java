package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateServerCommand extends ServerCommand {

    public UpdateServerCommand() {
        super("update", "обновить значение элемента коллекции по ID");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length != 1) {
            return new Response("response.error.id_required", ResponseStatus.ERROR);
        }
        if (request.getDragon() == null) {
            return new Response("response.update.error.no_data", ResponseStatus.VALIDATION_ERROR);
        }

        try {
            long id = Long.parseLong(request.getArgs()[0]);
            String login = request.getLogin();

            ResponseStatus status = dragonService.updateDragon(connection, login, id, request.getDragon());

            if (status == ResponseStatus.NOT_FOUND) {
                return new Response("response.remove_id.not_found", ResponseStatus.NOT_FOUND);            }
            if (status == ResponseStatus.PERMISSION_DENIED) {
                return new Response("response.update.denied:" + login, ResponseStatus.PERMISSION_DENIED);
            }
            if (status == ResponseStatus.OK) {
                return new Response("response.update.success", ResponseStatus.OK);
            }

            return new Response("response.update.error", ResponseStatus.ERROR);

        } catch (NumberFormatException e) {
            return new Response("response.error.id_not_number", ResponseStatus.VALIDATION_ERROR);        }
    }
}