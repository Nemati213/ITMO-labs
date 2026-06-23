package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public class AddClientCommand extends ClientCommand {

    public AddClientCommand() {
        super("add", "command.description.add");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
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