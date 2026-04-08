package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.models.DragonCharacter;

import java.util.Arrays;
import java.util.Optional;

/**
 * The type Remove all by character client command.
 */
public class RemoveAllByCharacterClientCommand extends ClientCommand {

    /**
     * Instantiates a new Remove all by character client command.
     */
    public RemoveAllByCharacterClientCommand() {
        super("remove_all_by_character", "удалить из коллекции все элементы с заданным характером");
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент!");
        }

        try {
            DragonCharacter.valueOf(args[0].trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgumentException("Указанного характера не существует! Доступные варианты: " +
                    Arrays.toString(DragonCharacter.values()));
        }

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(login)
                .password(password)
                .build());
    }
}