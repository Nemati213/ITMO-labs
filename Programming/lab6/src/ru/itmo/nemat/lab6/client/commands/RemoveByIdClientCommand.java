package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class RemoveByIdClientCommand extends ClientCommand {

    public RemoveByIdClientCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его идентификатору");
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент (ID)!");
        }

        try {
            Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("ID должен быть целым числом!");
        }

        return Optional.of(new Request(getName(), args));
    }
}