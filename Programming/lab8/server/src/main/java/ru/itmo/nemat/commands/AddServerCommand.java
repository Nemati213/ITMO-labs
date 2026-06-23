package ru.itmo.nemat.commands;


import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;

import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;

public class AddServerCommand extends ServerCommand {
    public AddServerCommand() {
        super("add", "добавить новый элемент в коллекцию");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) {
        ResponseStatus status = dragonService.addDragon(
                connection,
                request.getLogin(),
                request.getDragon()
        );

        if (status == ResponseStatus.OK) {
            return new Response("response.add.success", status);
        } else {
            return new Response("response.add.error", status);
        }
    }
}