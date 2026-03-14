package ru.itmo.nemat.lab5;

import ru.itmo.nemat.lab5.commands.*;
import ru.itmo.nemat.lab5.managers.*;
import ru.itmo.nemat.lab5.utility.ConsolePrinter;
import ru.itmo.nemat.lab5.utility.ConsoleReader;
import ru.itmo.nemat.lab5.utility.DragonAsker;
import ru.itmo.nemat.lab5.utility.Runner;

/**
 * Главный класс приложения.
 * Отвечает за инициализацию менеджеров, создание инструментов ввода-вывода,
 * регистрацию всех доступных команд и запуск программы в интерактивном режиме.
 *
 * @author Nemati213
 */
public class Main {
    /**
     * Точка входа в программу.
     * Выполняет сборку всех зависимостей и запускает цикл обработки команд.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {

        var printer = new ConsolePrinter();
        var scanner = new ConsoleReader();

        var dumpManager = new DumpManager("MY_LAB_FILE", printer);
        var collectionManager = new CollectionManager(dumpManager);
        var commandManager = new CommandManager(printer);

        var dragonAsker = new DragonAsker(printer, scanner);
        var runner = new Runner(commandManager, scanner, printer, dragonAsker);


        commandManager.register(new HelpCommand(commandManager, printer));
        commandManager.register(new InfoCommand(collectionManager, printer));
        commandManager.register(new ShowCommand(collectionManager, printer));
        commandManager.register(new AddCommand(collectionManager, printer, dragonAsker));
        commandManager.register(new UpdateCommand(collectionManager, printer, dragonAsker));
        commandManager.register(new RemoveByIDCommand(collectionManager, printer));
        commandManager.register(new ClearCommand(collectionManager, printer));
        commandManager.register(new SaveCommand(collectionManager, printer));
        commandManager.register(new ExecuteScriptCommand(printer, runner));
        commandManager.register(new ExitCommand(printer));
        commandManager.register(new RemoveLastCommand(collectionManager, printer));
        commandManager.register(new SortCommand(collectionManager, printer));
        commandManager.register(new HistoryCommand(commandManager, printer));
        commandManager.register(new RemoveAllByCharacterCommand(collectionManager, printer));
        commandManager.register(new PrintAscendingCommand(collectionManager, printer));
        commandManager.register(new PrintFieldDescendingAgeCommand(collectionManager, printer));

        runner.interactiveMode();
    }
}