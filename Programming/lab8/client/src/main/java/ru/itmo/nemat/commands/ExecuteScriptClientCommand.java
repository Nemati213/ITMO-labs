package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.handler.ScriptExecutor;

import java.util.Optional;
import java.util.function.Supplier;

public class ExecuteScriptClientCommand extends ClientCommand {

    private final ScriptExecutor scriptExecutor;

    public ExecuteScriptClientCommand(ScriptExecutor scriptExecutor) {
        super("execute_script", "command.description.execute_script");
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
        }
        scriptExecutor.execute(args[0].trim());
        return Optional.empty();
    }
}