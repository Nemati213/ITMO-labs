package ru.itmo.nemat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HelloCallable implements Callable<ArrayList<String>> {
    private static int STRING_SIZE = 10;
    private static int ARRAY_SIZE = 10;

    @Override
    public ArrayList<String> call() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            try {
                String filename = randomGenerator() + ".txt";
                File f = new File(filename);
                f.createNewFile();
                String content = randomGenerator();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
                list.add(filename);
            } catch (IOException ignored) {}
        }
        Thread.sleep(1000);
        return list;
    }

    private String randomGenerator() {
        String alphabet = "ABCEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < STRING_SIZE; i++) {
            sb.append(alphabet.charAt((int) (Math.random() * alphabet.length())));
        }
        return sb.toString();
    }

}
