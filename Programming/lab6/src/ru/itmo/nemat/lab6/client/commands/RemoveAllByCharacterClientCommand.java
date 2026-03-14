package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.models.DragonCharacter;

import java.util.Arrays;
import java.util.Optional;

public class RemoveAllByCharacterClientCommand extends ClientCommand {

    public RemoveAllByCharacterClientCommand() {
        super("remove_all_by_character", "удалить из коллекции все элементы с заданным характером");
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент!");
        }

        try {
            DragonCharacter.valueOf(args[0].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgumentException("Указанного характера не существует! Доступные варианты: " +
                    Arrays.toString(DragonCharacter.values()));
        }

        return Optional.of(new Request(getName(), args));
    }
}