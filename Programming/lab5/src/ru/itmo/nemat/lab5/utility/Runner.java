package ru.itmo.nemat.lab5.utility;

import ru.itmo.nemat.lab5.managers.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Класс Runner. Является ядром приложения, управляющим циклом ввода команд.
 * Поддерживает работу как в интерактивном режиме (консоль), так и в режиме выполнения скриптов.
 *
 * @author Nemati213
 */
public class Runner {
    private final CommandManager commandManager;
    private final List<String> scriptStack = new ArrayList<>();

    private InputReader scanner;
    private final OutputPrinter console;
    private final DragonAsker dragonAsker;

    /**
     * Конструктор раннера.
     * @param commandManager Менеджер команд для обработки ввода.
     * @param scanner        Текущий инструмент чтения данных.
     * @param console        Инструмент для вывода информации пользователю.
     * @param dragonAsker    Инструмент для интерактивного опроса данных дракона.
     */
    public Runner(CommandManager commandManager, InputReader scanner, OutputPrinter console, DragonAsker dragonAsker) {
        this.commandManager = commandManager;
        this.scanner = scanner;
        this.console = console;
        this.dragonAsker = dragonAsker;
    }

    /**
     * Запускает интерактивный режим работы с пользователем через стандартную консоль.
     * Цикл прерывается при завершении входного потока или вызове команды выхода.
     */
    public void interactiveMode() {
        console.println("Программа управления коллекцией драконов запущена.");
        console.println("Введите 'help' для получения списка команд.");

        while (true) {
            try {
                console.print("> ");
                if (!scanner.hasNextLine()) break;
                String line = scanner.readLine().trim();
                if (line.isEmpty()) continue;
                launchCommand(line);

            } catch (NoSuchElementException e) {
                console.printError("Пользовательский ввод не обнаружен!");
                break;
            }
        }
    }

    /**
     * Вспомогательный метод для парсинга строки и запуска команды через менеджер.
     * @param line Полная строка ввода.
     * @return true, если команда выполнена успешно, иначе false.
     */
    private boolean launchCommand(String line) {
        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();

        String[] args;
        if (parts.length > 1) {
            args = parts[1].split("\\s+");
        } else {
            args = new String[0];
        }
        return commandManager.execute(commandName, args);
    }

    /**
     * Реализует режим выполнения команд из файла.
     * Включает проверку на рекурсивный вызов и переключение источников ввода для Аскера.
     * @param fileName Путь к файлу скрипта.
     * @return true, если скрипт выполнен полностью и без критических ошибок, иначе false.
     */
    public boolean runScript(String fileName) {
        if (scriptStack.contains(fileName)) {
            console.printError("РЕКУРСИЯ! Скрипт '" + fileName + "' уже выполняется. Бесконечный цикл остановлен.");
            return false;
        }
        File file = new File(fileName);
        if (file.exists() && !file.canRead()) {
            console.printError("Нет прав на чтение файла скрипта!");
            return false;
        }
        scriptStack.add(fileName);
        InputReader oldReader = this.scanner;
        int lineNumber = 0;

        try {
            this.scanner = new FileInputReader(fileName);
            dragonAsker.setScanner(this.scanner);

            console.println("--- Начало выполнения скрипта: " + fileName + " ---");

            while (this.scanner.hasNextLine()) {
                lineNumber++;
                String line = this.scanner.readLine().trim();
                if (line.isEmpty()) continue;

                console.println("Выполняется: " + line);
                try {
                    if (!launchCommand(line)) {
                        console.printError("Ошибка в команде на строке " + lineNumber + ". Остановка скрипта.");
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    console.printError("Ошибка данных на строке " + lineNumber + ": " + e.getMessage());
                    console.printError("Выполнение скрипта прервано!");
                    break;
                }
            }
            console.println("--- Скрипт " + fileName + " успешно выполнен ---");
            return true;

        } catch (FileNotFoundException e) {
            console.printError("Файл скрипта не найден: " + fileName);
            return false;
        } finally {
            scriptStack.remove(fileName);
            this.scanner = oldReader;
            dragonAsker.setScanner(oldReader);
        }
    }
}