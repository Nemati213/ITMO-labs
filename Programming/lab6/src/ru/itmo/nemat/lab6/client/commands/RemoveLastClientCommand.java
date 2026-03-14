package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class RemoveLastClientCommand extends ClientCommand {

    public RemoveLastClientCommand() {
        super("remove_last", "удалить последний элемент коллекции");
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        return Optional.of(new Request(getName(), args));
    }
}