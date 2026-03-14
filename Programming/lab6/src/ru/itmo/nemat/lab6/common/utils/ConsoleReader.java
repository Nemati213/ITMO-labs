package ru.itmo.nemat.lab6.common.utils;

import java.util.Scanner;

public class ConsoleReader implements InputReader {

    private final Scanner scanner;

    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public boolean isInteractive() { return true; }
}
