package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

public class InfoServerCommand extends ServerCommand {

    public InfoServerCommand() {
        super("info", "вывести информацию о коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("response.error.no_args:" + getName(), ResponseStatus.ERROR);
        }

        String info = dragonService.getInfoMessage(connection);
        return new Response("response.info.message:" + info, ResponseStatus.OK);
    }
}