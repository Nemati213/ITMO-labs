package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

import java.util.Date;

/**
 * Команда 'info'. Выводит в стандартный поток вывода информацию о коллекции
 * (тип, дата инициализации, количество элементов и т.д.).
 *
 * @author Nemati213
 */
public class InfoCommand extends Command {


    private final OutputPrinter printer;
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для получения метаданных.
     * @param console           Инструмент для вывода сообщений.
     */
    public InfoCommand(CollectionManager collectionManager, OutputPrinter console) {
        super("info", "вывести информацию о коллеции");
        this.collectionManager = collectionManager;
        this.printer = console;

    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды (не требуются).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if(args.length > 0) {
            printer.printError("Команда " + getName() + " не принимает аргументов!");
            return false;
        }
        Date lastInitTime = collectionManager.getLastInitTime();
        Date lastSaveTime = collectionManager.getLastSaveTime();

        String lastInitStr = (lastInitTime == null) ? "в этой сессии еще не было" : lastInitTime.toString();
        String lastSaveStr = (lastSaveTime == null) ? "в этой сессии еще не было" : lastSaveTime.toString();

        printer.println("Сведения о коллекции:");
        printer.println("  Тип: " + collectionManager.collectionType());
        printer.println("  Количество элементов: " + collectionManager.collectionSize());
        printer.println("  Дата последней инициализации: " + lastInitStr);
        printer.println("  Дата последнего сохранения: " + lastSaveStr);

        return true;
    }

}
