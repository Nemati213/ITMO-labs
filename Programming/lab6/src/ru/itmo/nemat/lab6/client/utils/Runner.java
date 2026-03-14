package ru.itmo.nemat.lab6.client.utils;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.exceptions.ServerUnavailableException;
import ru.itmo.nemat.lab6.client.managers.ClientCommandManager;
import ru.itmo.nemat.lab6.client.managers.InteractionManager;
import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.utils.InputReader;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


public class Runner {
    private final ClientCommandManager commandManager;
    private final InteractionManager interactionManager;

    private final List<String> scriptStack = new ArrayList<>();
    private final OutputPrinter console;
    private final DragonBuilder dragonBuilder;
    private InputReader scanner;


    public Runner(ClientCommandManager commandManager, InputReader scanner, OutputPrinter console, DragonBuilder dragonBuilder, InteractionManager interactionManager) {
        this.commandManager = commandManager;
        this.scanner = scanner;
        this.console = console;
        this.dragonBuilder = dragonBuilder;
        this.interactionManager = interactionManager;
    }

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

    private boolean launchCommand(String line) {
        String[] parts = line.trim().split("\\s+", 2);
        String commandName = parts[0].toLowerCase();

        String[] args;
        if (parts.length > 1) {
            args = parts[1].split("\\s+");
        } else {
            args = new String[0];
        }
        try {
            Optional<Request> request = commandManager.apply(commandName, args);
            if(request.isEmpty()) return true;
            Response response = interactionManager.sendAndReceive(request.get());
            if (response == null) return false;
            if (response.isSuccess()) {
                console.println(response.getMessage());
                if (response.getDragons() != null && !response.getDragons().isEmpty())
                    for (var dragon : response.getDragons()) console.println(dragon);
                return true;

            } else {
                console.printError(response.getMessage());
                return false;
            }

        } catch (InvalidCommandArgumentException e) {
            console.printError(e.getMessage());
            return false;
        } catch (ServerUnavailableException e) {
            console.printError("Сервер не отвечает. Попробуйте позже");
            return false;
        }
    }

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
            dragonBuilder.setScanner(this.scanner);

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
            dragonBuilder.setScanner(oldReader);
        }
    }
}