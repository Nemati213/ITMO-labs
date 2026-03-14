package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class ExitClientCommand extends ClientCommand {

    public ExitClientCommand() {
        super("exit", "завершить работу клиентского приложения");
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        System.out.println("Завершение работы клиента...");
        System.exit(0);
        return Optional.empty();
    }
}