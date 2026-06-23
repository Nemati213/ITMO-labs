package ru.itmo.nemat.utils;


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

    @Override
    public String readPassword() {
        return scanner.nextLine();
    }

}