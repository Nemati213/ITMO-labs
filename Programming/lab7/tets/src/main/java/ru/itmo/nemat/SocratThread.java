package ru.itmo.nemat;

import java.util.concurrent.ExecutionException;

public class SocratThread extends Thread {
    private final Object leftFork;
    private final Object rightFork;
    private final String name;

    public SocratThread(Object leftFork, Object rightFork, String name) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.name = name;

    }

    @Override
    public void run() {
        while(true) {
            System.out.println(name + " размышляет...");
            // (Опционально) убери или сократи этот сон

            synchronized (leftFork) {
                System.out.println(name + " ВЗЯЛ ЛЕВУЮ ВИЛКУ");

                // --- ВОТ ТУТ СЕКРЕТ ДЕДЛОКА ---
                try {
                    // Даем всем остальным потокам время тоже взять свою левую вилку
                    Thread.sleep(10);
                } catch (InterruptedException e) { return; }
                // ------------------------------

                synchronized (rightFork) {
                    System.out.println(name + " ест спагетти");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) { return; }
                }
            }
        }
    }
}

