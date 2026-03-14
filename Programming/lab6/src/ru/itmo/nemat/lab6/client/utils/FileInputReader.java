package ru.itmo.nemat.lab6.client.utils;

import ru.itmo.nemat.lab6.common.utils.InputReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileInputReader implements InputReader {
    private final Scanner scanner;

    public FileInputReader(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new File(fileName));
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
    public boolean isInteractive() { return false; }
}