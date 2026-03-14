package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;
import ru.itmo.nemat.lab5.models.DragonCharacter;

import java.util.Arrays;

/**
 * Команда 'remove_all_by_character'. Удаляет из коллекции все элементы,
 * значение поля character которых эквивалентно заданному.
 *
 * @author Nemati213
 */
public class RemoveAllByCharacterCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для удаления элементов.
     * @param printer           Инструмент для вывода сообщений.
     */
    public RemoveAllByCharacterCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("remove_all_by_character", "удалить все элементы, значение которых совпадает с заданным");
        this.collectionManager = collectionManager;
        this.printer = printer;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды. Ожидается один аргумент: имя константы DragonCharacter.
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            printer.printError("Введите характер для удаления!");
            return false;
        }
        if (args.length > 1) {
            printer.printError("Слишком много аргументов!");
            return false;
        }

        try {
            String input = args[0].trim().toUpperCase();
            DragonCharacter characterToRemove = DragonCharacter.valueOf(input);

            int sizeBefore = collectionManager.collectionSize();

            collectionManager.getCollection().removeIf(dragon ->
                    dragon.getCharacter() != null && dragon.getCharacter().equals(characterToRemove)
            );

            int sizeAfter = collectionManager.collectionSize();

            printer.println("Удаление завершено. Удалено драконов: " + (sizeBefore - sizeAfter));
            return true;

        } catch (IllegalArgumentException e) {
            printer.printError("Такого характера не существует!");
            printer.println("Доступные характеры: " + Arrays.toString(DragonCharacter.values()));
            return false;
        }
    }
}