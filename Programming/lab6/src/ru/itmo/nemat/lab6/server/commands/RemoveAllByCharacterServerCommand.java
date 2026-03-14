package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.models.Dragon;
import ru.itmo.nemat.lab6.common.models.DragonCharacter;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class RemoveAllByCharacterServerCommand extends ServerCommand {

    private final CollectionManager collectionManager;

    public RemoveAllByCharacterServerCommand(CollectionManager collectionManager) {
        super("remove_all_by_character", "удалить все элементы, значение которых совпадает с заданным");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length == 0) {
            return new Response("Введите характер для удаления!", false);
        }
        if (request.getArgs().length > 1) {
            return new Response("Слишком много аргументов!", false);
        }

        try {
            DragonCharacter target = DragonCharacter.valueOf(request.getArgs()[0].trim().toUpperCase());
            Stack<Dragon> stack = collectionManager.getCollection();
            int sizeBefore = stack.size();

            List<Dragon> filtered = stack.stream()
                    .filter(d -> d.getCharacter() != target)
                    .collect(Collectors.toList());

            stack.clear();
            stack.addAll(filtered);

            int removed = sizeBefore - filtered.size();
            return new Response("Удалено драконов: " + removed, true);
        } catch (IllegalArgumentException e) {
            return new Response("Такого характера не существует!"  + '\n' + "Доступные характеры: " + Arrays.toString(DragonCharacter.values()), false);
        }
    }
}