package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class RemoveAllByCharacterClientCommand extends ClientCommand {

    public RemoveAllByCharacterClientCommand() {
        super("remove_all_by_character", "command.description.remove_all_by_character");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("response.error.one_arg_required_with_name:" + getName());
        }

        try {
            DragonCharacter.valueOf(args[0].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgumentException("response.error.invalid_character:" + Arrays.toString(DragonCharacter.values()));

        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}