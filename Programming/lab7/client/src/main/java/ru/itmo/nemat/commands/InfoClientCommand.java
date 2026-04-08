package ru.itmo.nemat.commands;


import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Info client command.
 */
public class InfoClientCommand extends ClientCommand {

    /**
     * Instantiates a new Info client command.
     */
    public InfoClientCommand() {
        super("info", "вывести информацию о коллекции");
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