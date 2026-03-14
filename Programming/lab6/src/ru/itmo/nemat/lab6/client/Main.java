package ru.itmo.nemat.lab6.client;

import ru.itmo.nemat.lab6.client.commands.*;
import ru.itmo.nemat.lab6.client.managers.ClientCommandManager;
import ru.itmo.nemat.lab6.client.managers.InteractionManager;
import ru.itmo.nemat.lab6.client.utils.DragonBuilder;
import ru.itmo.nemat.lab6.client.utils.Runner;
import ru.itmo.nemat.lab6.common.utils.ConsolePrinter;
import ru.itmo.nemat.lab6.common.utils.ConsoleReader;
import ru.itmo.nemat.lab6.common.utils.Serializer;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        var printer = new ConsolePrinter();
        var reader = new ConsoleReader();
        var serializer = new Serializer();
        var asker = new DragonBuilder(printer, reader);

        String host = (args.length > 0) ? args[0] : "localhost";
        int port = 8080;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                printer.printError("Порт должен быть числом. Используется порт по умолчанию: 8080");
            }
        }

        var address = new InetSocketAddress(host, port);

        var networkManager = new InteractionManager(address, serializer, printer);
        var commandManager = new ClientCommandManager(printer);

        var runner = new Runner(commandManager, reader, printer, asker, networkManager);

        commandManager.register(new HelpClientCommand(commandManager, printer));
        commandManager.register(new HistoryClientCommand(commandManager, printer));
        commandManager.register(new ExitClientCommand());
        commandManager.register(new ExecuteScriptClientCommand(runner));

        commandManager.register(new AddClientCommand(asker));
        commandManager.register(new UpdateClientCommand(asker));
        commandManager.register(new ShowClientCommand());
        commandManager.register(new InfoClientCommand());
        commandManager.register(new ClearClientCommand());
        commandManager.register(new RemoveByIdClientCommand());
        commandManager.register(new RemoveLastClientCommand());
        commandManager.register(new SortClientCommand());
        commandManager.register(new RemoveAllByCharacterClientCommand());
        commandManager.register(new PrintAscendingClientCommand());
        commandManager.register(new PrintFieldDescendingAgeClientCommand());

        runner.interactiveMode();
    }
}
