package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'clear'. Очищает коллекцию.
 *
 * @author Nemati213
 */
public class ClearCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;


    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции.
     * @param printer           Инструмент для вывода уведомлений и ошибок в процессе работы со скриптом.
     */
    public ClearCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.printer = printer;
    }

    /**
     * Выполняет команду.
     * @param args Аргументы команды (не требуются).
     * @return Успешность выполнения команды.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length > 0) {
            printer.printError("Команда '" + getName() + "' не принимает аргументов!");
            return false;
        }

        collectionManager.clearCollection();
        printer.println("Коллекция успешно очищена.");
        return true;
    }
}