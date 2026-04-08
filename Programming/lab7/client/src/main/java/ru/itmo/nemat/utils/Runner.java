package ru.itmo.nemat.utils;


import ru.itmo.nemat.commands.ClientCommand;
import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.exceptions.ServerUnavailableException;
import ru.itmo.nemat.handler.SessionHandler;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.managers.InteractionManager;
import ru.itmo.nemat.models.ResponseStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


/**
 * The type Runner.
 */
public class Runner {
    private final ClientCommandManager commandManager;
    private final InteractionManager interactionManager;

    private final List<String> scriptStack = new ArrayList<>();
    private final OutputPrinter console;
    private final DragonBuilder dragonBuilder;
    private InputReader scanner;

    private final SessionHandler sessionHandler;
    private final SessionAsker sessionAsker;


    /**
     * Instantiates a new Runner.
     *
     * @param commandManager     the command manager
     * @param scanner            the scanner
     * @param console            the console
     * @param dragonBuilder      the dragon builder
     * @param interactionManager the interaction manager
     * @param sessionHandler     the session handler
     * @param sessionAsker       the session asker
     */
    public Runner(ClientCommandManager commandManager, InputReader scanner, OutputPrinter console, DragonBuilder dragonBuilder, InteractionManager interactionManager, SessionHandler sessionHandler, SessionAsker sessionAsker) {
        this.commandManager = commandManager;
        this.scanner = scanner;
        this.console = console;
        this.dragonBuilder = dragonBuilder;
        this.interactionManager = interactionManager;
        this.sessionHandler = sessionHandler;
        this.sessionAsker = sessionAsker;
    }

    /**
     * Interactive mode.
     */
    public void interactiveMode() {
        console.println("Программа управления коллекцией драконов запущена.");
        console.println("Доступные на данный момент команды:");
        for(var command: commandManager.getCommands().values()) {
            if(!command.isProtected()) {
                console.println(String.format(" %-30s : %s", command.getName(), command.getDescription()));
            }
        }
        console.println("Для доступа к остальным функциям, пожалуйста, авторизируйтесь");

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
        ClientCommand command = commandManager.getCommands().get(commandName);

        if(command != null && command.isProtected() && !sessionHandler.isLoggedIn()) {
            console.printError("Эта команда вам недоступна, пожалуйста, авторизируйтесь.");
            return false;
        }
        try {

            Optional<Request> request = commandManager.apply(commandName, args, sessionHandler.getLogin(), sessionHandler.getPassword());
            if(request.isEmpty()) return true;
            Request requestObj = request.get();
            Response response = interactionManager.sendAndReceive(request.get());
            if (response == null) return false;
            if (response.getStatus() == ResponseStatus.OK || response.getStatus() == ResponseStatus.AUTH_SUCCESS) {
                console.println(response.getMessage());

                if (response.getDragons() != null && !response.getDragons().isEmpty())
                    for (var dragon : response.getDragons()) console.println(dragon);

                if(response.getStatus() == ResponseStatus.AUTH_SUCCESS) {
                    for(var cm: commandManager.getCommands().values())
                        console.println(String.format(" %-30s : %s", cm.getName(), cm.getDescription()));

                    sessionHandler.setSession(requestObj.getLogin(), requestObj.getPassword());
                }



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

    /**
     * Run script boolean.
     *
     * @param fileName the file name
     * @return the boolean
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
            dragonBuilder.setScanner(this.scanner);
            sessionAsker.setScanner(this.scanner);

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
            sessionAsker.setScanner(oldReader);
        }
    }
}