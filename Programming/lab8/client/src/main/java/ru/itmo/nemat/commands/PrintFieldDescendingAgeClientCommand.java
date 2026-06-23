package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public class PrintFieldDescendingAgeClientCommand extends ClientCommand {

    public PrintFieldDescendingAgeClientCommand() {
        super("print_field_descending_age", "command.description.print_field_descending_age");
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