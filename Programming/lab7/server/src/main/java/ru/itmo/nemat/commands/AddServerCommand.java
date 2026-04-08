package ru.itmo.nemat.commands;


import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;
import ru.itmo.nemat.utils.DatabaseAddingResult;
import ru.itmo.nemat.utils.DragonDTO;

import java.sql.Connection;

/**
 * The type Add server command.
 */
public class AddServerCommand extends ServerCommand {

    /**
     * Instantiates a new Add server command.
     */
    public AddServerCommand() {
        super("add", "добавить дракона");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) {
        try {
            if (request.getArgs().length > 0) {
                return new Response("Команда add не принимает строковых аргументов!", ResponseStatus.ERROR);
            }

            DragonDTO dto = request.getDragon();

            if (dto == null) return new Response("Ошибка: объект дракона не передан!", ResponseStatus.ERROR);
            DatabaseAddingResult result =  dragonDAO.addDragon(connection, dto, request.getLogin());
            if (result == null) return new Response("Ошибка при добавлении в БД", ResponseStatus.ERROR);

            Dragon dragon = new Dragon(result.id(), dto, result.creationDate(), request.getLogin());
            collectionManager.addToCollection(dragon);

            return new Response("Дракон успешно добавлен!", ResponseStatus.OK);

        } catch (IllegalArgumentException e) {
            return new Response("Ошибка валидации данных: " + e.getMessage(), ResponseStatus.VALIDATION_ERROR);
        }
    }
}