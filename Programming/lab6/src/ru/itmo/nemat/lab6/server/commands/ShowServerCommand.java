package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class ShowServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public ShowServerCommand(CollectionManager collectionManager) {
        super("show", "вывести все элементы коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", false);
        }

        if (collectionManager.collectionSize() == 0) {
            return new Response("Коллекция пуста.", true);
        }

        List<Dragon> sortedDragons = collectionManager.getCollection().stream()
                .sorted()
                .collect(Collectors.toList());

        return new Response("Список всех драконов:", true, sortedDragons);
    }
}