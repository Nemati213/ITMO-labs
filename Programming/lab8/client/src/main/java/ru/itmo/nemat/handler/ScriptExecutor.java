package ru.itmo.nemat.handler;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.managers.InteractionManager;
import ru.itmo.nemat.utils.AuthFactory;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.DragonFactory;
import ru.itmo.nemat.utils.OutputPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Supplier;

public class ScriptExecutor {

    private final InteractionManager interactionManager;
    private final ClientCommandManager commandManager;
    private final OutputPrinter printer;

    private final SessionHandler sessionHandler;

    private final Set<String> scriptStack = new HashSet<>();

    public ScriptExecutor(InteractionManager interactionManager, ClientCommandManager commandManager,
                          OutputPrinter printer, SessionHandler sessionHandler) {
        this.interactionManager = interactionManager;
        this.commandManager = commandManager;
        this.printer = printer;
        this.sessionHandler = sessionHandler;
    }

    public void execute(String fileName) {
        if (scriptStack.contains(fileName)) {
            printer.printError("ui.script.recursion_error:" + fileName);
            return;
        }

        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            printer.printError("ui.script.file_access_error:" + fileName);
            return;
        }

        scriptStack.add(fileName);
        printer.println("ui.script.start:" + fileName);

        try (Scanner scanner = new Scanner(file)) {

            Supplier<String> lineReader = () -> {
                if (scanner.hasNextLine()) return scanner.nextLine().trim();
                throw new RuntimeException("ui.script.unexpected_eof");
            };

            Supplier<DragonDTO> dragonSupplier = () -> DragonFactory.getInstance().buildFromSequence(lineReader);

            int lineNumber = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                if (line.isEmpty() || line.startsWith("#")) continue;

                printer.println("ui.script.line_status:" + lineNumber + "," + line);

                String[] parts = line.split("\\s+");
                String commandName = parts[0];
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);

                try {
                    if (commandName.equals("execute_script") && args.length > 0) {
                        execute(args[0]);
                        continue;
                    }

                    String reqLogin = sessionHandler.getLogin();
                    String reqPassword = sessionHandler.getPassword();

                    if (commandName.equals("login")) {
                        String[] creds = AuthFactory.getInstance().buildFromSequence(lineReader);
                        reqLogin = creds[0];
                        reqPassword = creds[1];
                    } else if (commandName.equals("register")) {
                        String[] creds = AuthFactory.getInstance().buildRegistrationFromSequence(lineReader);
                        reqLogin = creds[0];
                        reqPassword = creds[1];
                    }

                    Optional<Request> request = commandManager.apply(commandName, args, reqLogin, reqPassword, dragonSupplier);

                    if (request.isPresent()) {
                        interactionManager.send(request.get());
                        Thread.sleep(50);
                    }

                } catch (Exception e) {
                    printer.printError("ui.script.line_error:" + lineNumber + "," + e.getMessage());
                    break;
                }
            }
            Thread.sleep(1000);

            printer.println("ui.script.success:" + fileName);

        } catch (FileNotFoundException e) {
            printer.printError("ui.script.file_not_found_error:" + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            scriptStack.remove(fileName);
        }
    }
}
