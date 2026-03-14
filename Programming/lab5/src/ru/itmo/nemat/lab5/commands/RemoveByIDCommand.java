package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по его идентификатору.
 *
 * @author Nemati213
 */
public class RemoveByIDCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для удаления элементов.
     * @param printer           Инструмент для вывода сообщений.
     */
    public RemoveByIDCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("remove_by_id", "удалить дракона по ID");
        this.collectionManager = collectionManager;
        this.printer = printer;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды. Ожидается один аргумент: ID дракона (long).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            printer.printError("Введите ID!");
            return false;
        }
        if (args.length > 1) {
            printer.printError("Слишком много аргументов!");
            return false;
        }

        try {
            long id = Long.parseLong(args[0]);

            if (!collectionManager.isExist(id)) {
                printer.println("Дракон с таким ID не найден.");
                return true;
            }

            collectionManager.getCollection().removeIf(dragon -> dragon.getId().equals(id));
            printer.println("Дракон успешно удалён.");
            return true;

        } catch (NumberFormatException e) {
            printer.printError("ID должен быть числом!");
            return false;
        }
    }
}