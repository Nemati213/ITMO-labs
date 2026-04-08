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
import java.util.stream.Collectors;

/**
 * The type Print ascending server command.
 */
public class PrintAscendingServerCommand extends ServerCommand {

    /**
     * Instantiates a new Print ascending server command.
     */
    public PrintAscendingServerCommand() {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", ResponseStatus.ERROR);
        }

        List<Dragon> sortedDragons = collectionManager.getCollection().stream()
                .sorted()
                .collect(Collectors.toList());

        if (sortedDragons.isEmpty()) {
            return new Response("Коллекция пуста.", ResponseStatus.OK);
        }

        return new Response("Элементы в порядке возрастания:", ResponseStatus.OK, sortedDragons);
    }
}