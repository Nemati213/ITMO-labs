package ru.itmo.nemat.utils;

/**
 * The type Session asker.
 */
public class SessionAsker {

    private final OutputPrinter printer;
    private InputReader scanner;

    /**
     * Instantiates a new Session asker.
     *
     * @param printer the printer
     * @param scanner the scanner
     */
    public SessionAsker(OutputPrinter printer, InputReader scanner) {
        this.printer = printer;
        this.scanner = scanner;
    }

    /**
     * Sets scanner.
     *
     * @param scanner the scanner
     */
    public void setScanner(InputReader scanner) {
        this.scanner = scanner;
    }

    /**
     * Ask login string.
     *
     * @return the string
     */
    public String askLogin() {
        if (scanner.isInteractive()) printer.println("Введите ваш логин:");

        while (true) {
            if (scanner.isInteractive()) printer.print("> ");
            String line = readNextLine();
            line = line.trim();

            if (line.isEmpty()) {
                handleValidationError("Логин не может быть пустым!");
                continue;
            }

            if (!line.matches("^[a-zA-Z0-9]+$")) {
                handleValidationError("Логин должен состоять только из латиницы и цифр!");
                continue;
            }

            return line;
        }
    }

    private void handleValidationError(String message) {
        if (!scanner.isInteractive()) {
            throw new IllegalArgumentException("Ошибка в скрипте: " + message);
        }
        printer.printError(message);
    }

    /**
     * Ask password string.
     *
     * @return the string
     */
    public String askPassword() {
        if (scanner.isInteractive()) printer.println("Введите ваш пароль:");
        while (true) {
            if (scanner.isInteractive()) printer.print("> ");

            String line = scanner.readPassword();

            if (line == null) throw new IllegalArgumentException("Ввод прерван пользователем");
            line = line.trim();

            if (line.isEmpty()) {
                handleValidationError("Пароль не может быть пустым!");
                continue;
            }

            if (!line.matches("^[a-zA-Z0-9]+$")) {
                handleValidationError("Пароль должен состоять из латиницы и цифр!");
                continue;
            }

            return line;
        }
    }

    /**
     * Ask password for registration string.
     *
     * @return the string
     */
    public String askPasswordForRegistration() {
        while (true) {
            String firstPassword = askPassword();

            if (scanner.isInteractive()) printer.println("Повторите пароль для подтверждения:");
            if (scanner.isInteractive()) printer.print("> ");

            String secondPassword = scanner.readPassword();
            if (secondPassword == null) throw new IllegalArgumentException("Ввод прерван пользователем");
            secondPassword = secondPassword.trim();

            if (firstPassword.equals(secondPassword)) {
                return firstPassword;
            } else {
                handleValidationError("Пароли не совпадают! Повторите попытку");}
        }
    }

    private String readNextLine() {
        String line = scanner.readLine();
        if (line == null) throw new IllegalArgumentException("Ввод прерван пользователем");
        return line;
    }
}
