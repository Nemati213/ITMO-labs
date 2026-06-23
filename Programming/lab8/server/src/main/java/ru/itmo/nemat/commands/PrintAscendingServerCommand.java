package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PrintAscendingServerCommand extends ServerCommand {

    public PrintAscendingServerCommand() {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("response.error.no_args:" + getName(), ResponseStatus.ERROR);
        }

        List<Dragon> sortedDragons = dragonService.getAllDragons(connection).stream()
                .sorted()
                .collect(Collectors.toList());

        if (sortedDragons.isEmpty()) {
            return new Response("response.error.empty_collection", ResponseStatus.OK);
        }

        return new Response("response.print_ascending.success", ResponseStatus.OK, sortedDragons);
    }
}