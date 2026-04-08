package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Exit client command.
 */
public class ExitClientCommand extends ClientCommand {

    /**
     * Instantiates a new Exit client command.
     */
    public ExitClientCommand() {
        super("exit", "завершить работу клиентского приложения");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        System.out.println("Завершение работы клиента...");
        System.exit(0);
        return Optional.empty();
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}