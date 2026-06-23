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

public class ShowServerCommand extends ServerCommand {

    public ShowServerCommand() {
        super("show", "вывести все элементы коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("response.error.no_args:" + getName(), ResponseStatus.ERROR);
        }

        List<Dragon> dragons = dragonService.getAllDragons(connection);

        if (dragons.isEmpty()) {
            return new Response("response.error.empty_collection", ResponseStatus.OK);
        }

        String currentUser = request.getLogin();

        List<Dragon> sortedDragons = dragons.stream()
                .sorted((d1, d2) -> {
                    boolean isMine1 = d1.getOwnerLogin().equals(currentUser);
                    boolean isMine2 = d2.getOwnerLogin().equals(currentUser);

                    if (isMine1 && !isMine2) return -1;
                    if (!isMine1 && isMine2) return 1;

                    return d1.compareTo(d2);
                })
                .collect(Collectors.toList());

        return new Response("response.show.success", ResponseStatus.OK, sortedDragons);
    }
}