package ru.itmo.nemat.utils;

import java.util.Scanner;

/**
 * The type Console reader.
 */
public class ConsoleReader implements InputReader {

    private final Scanner scanner;

    /**
     * Instantiates a new Console reader.
     */
    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public String readPassword() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return (chars == null) ? null : new String(chars);
        }
        return readLine();
    }

    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public boolean isInteractive() { return true; }
}
