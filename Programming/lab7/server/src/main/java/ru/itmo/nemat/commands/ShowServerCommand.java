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
 * The type Show server command.
 */
public class ShowServerCommand extends ServerCommand {

    /**
     * Instantiates a new Show server command.
     */
    public ShowServerCommand() {
        super("show", "вывести все элементы коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", ResponseStatus.ERROR);
        }

        if (collectionManager.collectionSize() == 0) {
            return new Response("Коллекция пуста.", ResponseStatus.OK);
        }

        String currentUser = request.getLogin();

        List<Dragon> sortedDragons = collectionManager.getCollection().stream()
                .sorted((d1, d2) -> {
                    boolean isMine1 = d1.getOwnerLogin().equals(currentUser);
                    boolean isMine2 = d2.getOwnerLogin().equals(currentUser);

                    if (isMine1 && !isMine2) return -1;
                    if (!isMine1 && isMine2) return 1;

                    return d1.compareTo(d2);
                })
                .collect(Collectors.toList());

        return new Response("Список всех драконов:", ResponseStatus.OK, sortedDragons);
    }
}