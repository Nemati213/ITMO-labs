package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.Comparator;
import java.util.stream.Collectors;

public class PrintFieldDescendingAgeServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingAgeServerCommand(CollectionManager collectionManager) {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
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

        String ages = collectionManager.getCollection().stream()
                .map(Dragon::getAge)
                .sorted(Comparator.reverseOrder())
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        return new Response("Список возрастов драконов (убывание):\n" + ages, true);
    }
}