package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.utils.DragonDTO;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

public class AddServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public AddServerCommand(CollectionManager collectionManager) {
        super("add", "добавить дракона");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            if (request.getArgs().length > 0) {
                return new Response("Команда add не принимает строковых аргументов!", false);
            }

            DragonDTO dto = request.getDragon();
            if (dto == null) return new Response("Ошибка: объект дракона не передан!", false);

            collectionManager.addToCollection(dto);

            return new Response("Дракон успешно добавлен!", true);

        } catch (IllegalArgumentException e) {
            return new Response("Ошибка валидации данных: " + e.getMessage(), false);
        }
    }
}