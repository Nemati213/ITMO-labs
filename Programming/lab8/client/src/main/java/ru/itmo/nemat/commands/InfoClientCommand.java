package ru.itmo.nemat.commands;


import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public class InfoClientCommand extends ClientCommand {

    public InfoClientCommand() {
        super("info", "command.description.info");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}