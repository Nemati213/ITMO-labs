package ru.itmo.nemat.lab5.commands;

/**
 * Абстрактная база для всех команд приложения.
 * Определяет общий контракт для реализации логики команд, их именования и описания.
 *
 * @author Nemati213
 */
public abstract class Command {
    private final String name;
    private final String description;

    /**
     * Конструктор для создания команды.
     * @param name Название команды.
     * @param description Описание команды.
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды.
     * @return true, если команда выполнена успешно, иначе false.
     */
    public abstract boolean execute(String... args);

    /**
     * @return Название команды.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Описание команды.
     */
    public String getDescription() {
        return description;
    }
}