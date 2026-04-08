package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Print field descending age client command.
 */
public class PrintFieldDescendingAgeClientCommand extends ClientCommand {

    /**
     * Instantiates a new Print field descending age client command.
     */
    public PrintFieldDescendingAgeClientCommand() {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}