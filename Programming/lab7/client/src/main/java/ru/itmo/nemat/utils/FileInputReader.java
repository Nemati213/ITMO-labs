package ru.itmo.nemat.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The type File input reader.
 */
public class FileInputReader implements InputReader {
    private final Scanner scanner;

    /**
     * Instantiates a new File input reader.
     *
     * @param fileName the file name
     * @throws FileNotFoundException the file not found exception
     */
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