package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Print ascending client command.
 */
public class PrintAscendingClientCommand extends ClientCommand {

    /**
     * Instantiates a new Print ascending client command.
     */
    public PrintAscendingClientCommand() {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
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
                .build());    }
}