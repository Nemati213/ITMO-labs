package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class PrintAscendingServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public PrintAscendingServerCommand(CollectionManager collectionManager) {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", false);
        }

        List<Dragon> sortedDragons = collectionManager.getCollection().stream()
                .sorted()
                .collect(Collectors.toList());

        if (sortedDragons.isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }

        return new Response("Элементы в порядке возрастания:", true, sortedDragons);
    }
}