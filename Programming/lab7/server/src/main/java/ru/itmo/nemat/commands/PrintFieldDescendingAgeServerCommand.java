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
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The type Print field descending age server command.
 */
public class PrintFieldDescendingAgeServerCommand extends ServerCommand {

    /**
     * Instantiates a new Print field descending age server command.
     */
    public PrintFieldDescendingAgeServerCommand() {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", ResponseStatus.ERROR);
        }

        if (collectionManager.collectionSize() == 0) {
            return new Response("Коллекция пуста.", ResponseStatus.OK);
        }

        String ages = collectionManager.getCollection().stream()
                .map(Dragon::getAge)
                .sorted(Comparator.reverseOrder())
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        return new Response("Список возрастов драконов (убывание):\n" + ages, ResponseStatus.OK);
    }
}