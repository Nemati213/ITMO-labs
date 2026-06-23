package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public class UpdateClientCommand extends ClientCommand {

    public UpdateClientCommand() {
        super("update", "command.description.update");
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

        DragonDTO dragon = dragonSupplier.get();
        var request = Request.builder()
                .commandName(getName())
                .args(args)
                .dragon(dragon)
                .login(login)
                .password(password)
                .build();
        return Optional.of(request);
    }
}