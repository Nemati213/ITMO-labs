package ru.itmo.nemat.commands;


import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class RemoveByIdClientCommand extends ClientCommand {

    public RemoveByIdClientCommand() {
        super("remove_by_id", "command.description.remove_by_id");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("response.error.one_arg_required_with_name:" + getName());
        }

        try {
            Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("response.error.id_not_number");
        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}