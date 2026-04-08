package ru.itmo.nemat.commands;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * The type Info server command.
 */
public class InfoServerCommand extends ServerCommand {

    /**
     * Instantiates a new Info server command.
     */
    public InfoServerCommand() {
        super("info", "вывести информацию о коллекции");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length > 0) {
            return new Response("Команда " + getName() + " не принимает аргументов!", ResponseStatus.ERROR);
        }

        Date lastInitTime = collectionManager.getLastInitTime();

        String lastInitStr = (lastInitTime == null) ? "в этой сессии еще не было" : lastInitTime.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("Сведения о коллекции:\n");
        sb.append("  Тип: ").append(collectionManager.collectionType()).append("\n");
        sb.append("  Количество элементов: ").append(collectionManager.collectionSize()).append("\n");
        sb.append("  Дата последней инициализации: ").append(lastInitStr).append("\n");

        return new Response(sb.toString(), ResponseStatus.OK);
    }
}