package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'save'. Сохраняет текущую коллекцию в файл.
 *
 * @author Nemati213
 */
public class SaveCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для выполнения сохранения.
     * @param printer           Инструмент для вывода сообщений.
     */
    public SaveCommand (CollectionManager collectionManager, OutputPrinter printer) {
        super("save", "сохранить коллекцию");
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

        collectionManager.saveCollection();
        //printer.println("Коллекция успешно сохранена.");
        return true;
    }
}