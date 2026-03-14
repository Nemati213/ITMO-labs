package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

public class ClearServerCommand extends ServerCommand {

    private final CollectionManager collectionManager;

    public ClearServerCommand(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", false);
        }

        collectionManager.clearCollection();

        // Возвращаем ответ клиенту
        return new Response("Коллекция успешно очищена.", true);
    }
}