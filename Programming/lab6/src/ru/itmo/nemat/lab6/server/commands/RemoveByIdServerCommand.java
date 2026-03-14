package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class RemoveByIdServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public RemoveByIdServerCommand(CollectionManager collectionManager) {
        super("remove_by_id", "удалить элемент из коллекции по его идентификатору");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response("Команда ожидает ровно 1 аргумент!", false);
        }

        try {
            long id = Long.parseLong(request.getArgs()[0]);
            Stack<Dragon> stack = collectionManager.getCollection();

            List<Dragon> filtered = stack.stream()
                    .filter(dragon -> dragon.getId() != id)
                    .collect(Collectors.toList());

            if (filtered.size() == stack.size()) {
                return new Response("Дракон с ID " + id + " не найден.", false);
            }

            stack.clear();
            stack.addAll(filtered);

            return new Response("Дракон успешно удалён.", true);

        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом!", false);
        }
    }
}