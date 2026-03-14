package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class RemoveLastServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public RemoveLastServerCommand(CollectionManager collectionManager) {
        super("remove_last", "удалить последний элемент коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length > 0) {
            return new Response("Команда '" + getName() + "' не принимает аргументов!", false);
        }

        Stack<Dragon> stack = collectionManager.getCollection();
        if (stack.isEmpty()) {
            return new Response("Коллекция пуста, удалять нечего!", false);
        }

        List<Dragon> remaining = stack.stream()
                .limit(stack.size() - 1)
                .collect(Collectors.toList());

        stack.clear();
        stack.addAll(remaining);

        return new Response("Последний элемент успешно удалён", true);
    }
}