package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'remove_last'. Удаляет последний элемент из коллекции.
 *
 * @author Nemati213
 */
public class RemoveLastCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для удаления элементов.
     * @param printer           Инструмент для вывода сообщений.
     */
    public RemoveLastCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("remove_last", "удалить последний добавленный элемент");
        this.collectionManager = collectionManager;
        this.printer = printer;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды (не требуются).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length > 0) {
            printer.printError("Команда '" + getName() + "' не принимает аргументов!");
            return false;
        }

        if (collectionManager.removeLast()) {
            printer.println("Последний элемент успешно удалён.");
        } else {
            printer.printError("Коллекция пуста, удалять нечего!");
        }
        return true;

    }
}