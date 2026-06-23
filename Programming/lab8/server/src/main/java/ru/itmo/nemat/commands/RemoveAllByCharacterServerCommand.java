package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class RemoveAllByCharacterServerCommand extends ServerCommand {

    public RemoveAllByCharacterServerCommand() {
        super("remove_all_by_character", "удалить все элементы, значение которых совпадает с заданным");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException {
        if (request.getArgs().length == 0) {
            return new Response("response.error.no_character_arg", ResponseStatus.ERROR);
        }
        if (request.getArgs().length > 1) {
            return new Response("response.error.too_many_args", ResponseStatus.ERROR);
        }

        try {
            DragonCharacter target = DragonCharacter.valueOf(request.getArgs()[0].trim().toUpperCase());
            ResponseStatus status = dragonService.deleteDragonsByCharacter(connection, request.getLogin(), target);

            if (status == ResponseStatus.OK) {
                return new Response("response.remove_character.success:" + target, ResponseStatus.OK);
            }
            return new Response("response.remove_character.error", ResponseStatus.ERROR);
        } catch (IllegalArgumentException e) {
            return new Response("response.error.invalid_character:" + Arrays.toString(DragonCharacter.values()), ResponseStatus.VALIDATION_ERROR);
        }
    }
}