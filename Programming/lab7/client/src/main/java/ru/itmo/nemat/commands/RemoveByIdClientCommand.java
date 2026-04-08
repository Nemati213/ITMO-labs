package ru.itmo.nemat.commands;


import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Remove by id client command.
 */
public class RemoveByIdClientCommand extends ClientCommand {

    /**
     * Instantiates a new Remove by id client command.
     */
    public RemoveByIdClientCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его идентификатору");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент (ID)!");
        }

        try {
            Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("ID должен быть целым числом!");
        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}