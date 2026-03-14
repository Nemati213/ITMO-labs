package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.utils.Runner;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class ExecuteScriptClientCommand extends ClientCommand {

    private final Runner runner;

    public ExecuteScriptClientCommand(Runner runner) {
        super("execute_script", "исполнить скрипт из указанного файла");
        this.runner = runner;
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент (имя файла)!");
        }

        runner.runScript(args[0].trim());

        return Optional.empty();
    }
}