package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public class ExitClientCommand extends ClientCommand {

    public ExitClientCommand() {
        super("exit", "command.description.exit");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
        }

        System.exit(0);
        return Optional.empty();
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}