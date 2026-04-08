package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Remove last client command.
 */
public class RemoveLastClientCommand extends ClientCommand {

    /**
     * Instantiates a new Remove last client command.
     */
    public RemoveLastClientCommand() {
        super("remove_last", "удалить последний элемент коллекции");
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