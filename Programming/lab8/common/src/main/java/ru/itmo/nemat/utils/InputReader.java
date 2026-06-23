package ru.itmo.nemat.utils;

public interface InputReader {

    public String readLine();

    boolean hasNextLine();

    boolean isInteractive();

    public String readPassword();
}