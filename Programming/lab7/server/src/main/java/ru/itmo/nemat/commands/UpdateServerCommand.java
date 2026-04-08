package ru.itmo.nemat.commands;


import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;
import ru.itmo.nemat.utils.DragonDTO;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Update server command.
 */
public class UpdateServerCommand extends ServerCommand {

    /**
     * Instantiates a new Update server command.
     */
    public UpdateServerCommand() {
        super("update", "обновить значение элемента коллекции по ID");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO,
                            CollectionManager collectionManager, AuthService authService) throws SQLException {

        if (request.getArgs().length != 1) {
            return new Response("Нужен ID!", ResponseStatus.ERROR);
        }
        if (request.getDragon() == null) {
            return new Response("Нет данных для обновления!", ResponseStatus.VALIDATION_ERROR);
        }

        try {
            long id = Long.parseLong(request.getArgs()[0]);
            String login = request.getLogin();

            ResponseStatus status = dragonDAO.updateDragon(connection, id, login, request.getDragon());

            if (status == ResponseStatus.NOT_FOUND) {
                return new Response("Дракон с таким ID не найден", ResponseStatus.ERROR);
            }
            if (status == ResponseStatus.PERMISSION_DENIED) {
                return new Response("Этот дракон не принадлежит пользователю " + request.getLogin(), ResponseStatus.ERROR);
            }
            if (status != ResponseStatus.OK) {
                return new Response("Ошибка базы данных", ResponseStatus.ERROR);
            }

            collectionManager.updateByID(id, request.getDragon(), login);

            return new Response("Дракон успешно обновлен", ResponseStatus.OK);

        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом", ResponseStatus.ERROR);
        }
    }
}
