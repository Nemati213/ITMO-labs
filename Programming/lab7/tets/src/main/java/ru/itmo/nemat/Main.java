package ru.itmo.nemat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Object[] forks = new Object[5];
        for(int i = 0; i < 5; i++) {
            forks[i] = new Object();
        }
        SocratThread[] threads = new SocratThread[5];
        for(int i = 0; i < 5; i++) {
            threads[i] = new SocratThread(forks[i], forks[(i + 1) % 5], "Socrat " + i);
        }
        for(int i = 0; i < 5; i++) {
        threads[i].start();
        }
    }
}