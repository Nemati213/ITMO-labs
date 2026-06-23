package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PrintFieldDescendingAgeServerCommand extends ServerCommand {

    public PrintFieldDescendingAgeServerCommand() {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
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

        String ages = dragons.stream()
                .map(Dragon::getAge)
                .sorted(Comparator.reverseOrder())
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));

        return new Response("response.print_ages.success:" + ages, ResponseStatus.OK);
    }
}