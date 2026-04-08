package ru.itmo.nemat.commands;



import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * The type Remove all by character server command.
 */
public class RemoveAllByCharacterServerCommand extends ServerCommand {


    /**
     * Instantiates a new Remove all by character server command.
     */
    public RemoveAllByCharacterServerCommand() {
        super("remove_all_by_character", "удалить все элементы, значение которых совпадает с заданным");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException {
        if (request.getArgs().length == 0) {
            return new Response("Введите характер для удаления!", ResponseStatus.ERROR);
        }
        if (request.getArgs().length > 1) {
            return new Response("Слишком много аргументов!", ResponseStatus.ERROR);
        }

        try {
            DragonCharacter target = DragonCharacter.valueOf(request.getArgs()[0].trim().toUpperCase());
            if(dragonDAO.deleteDragonsByCharacter(connection, target, request.getLogin()) != ResponseStatus.OK){
                return new Response("Ошибка при удалении с базы данных",  ResponseStatus.ERROR);
            };

            int removed = collectionManager.removeByCharacter(target, request.getLogin());

            return new Response("Удалено драконов: " + removed, ResponseStatus.OK);
        } catch (IllegalArgumentException e) {
            return new Response("Такого характера не существует!"  + '\n' + "Доступные характеры: " + Arrays.toString(DragonCharacter.values()), ResponseStatus.VALIDATION_ERROR);
        }
    }
}