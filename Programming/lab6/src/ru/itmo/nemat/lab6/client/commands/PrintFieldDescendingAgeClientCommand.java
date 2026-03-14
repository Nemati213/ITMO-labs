package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class PrintFieldDescendingAgeClientCommand extends ClientCommand {

    public PrintFieldDescendingAgeClientCommand() {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        return Optional.of(new Request(getName(), args));
    }
}