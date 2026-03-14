package ru.itmo.nemat.lab6.client.utils;

import ru.itmo.nemat.lab6.common.models.*;
import ru.itmo.nemat.lab6.common.utils.DragonDTO;
import ru.itmo.nemat.lab6.common.utils.InputReader;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;

import java.util.Arrays;

public class DragonBuilder {

    private final OutputPrinter printer;
    private InputReader scanner;

    public DragonBuilder(OutputPrinter printer, InputReader scanner) {
        this.printer = printer;
        this.scanner = scanner;
    }
    public void setScanner(InputReader scanner) {
        this.scanner = scanner;
    }

    public String askName() {
        if (scanner.isInteractive()) printer.println("Введите имя дракона:");

        while (true) {
            if (scanner.isInteractive()) printer.print("> ");
            String line = readNextLine().trim();

            if (line.isEmpty()) {
                if (!scanner.isInteractive()) {
                    throw new IllegalArgumentException("Имя в скрипте не может быть пустым!");
                }
                printer.printError("Имя не может быть пустым!");
                continue;
            }
            return line;
        }
    }

    public Long askAge() {
        if (scanner.isInteractive()) printer.println("Введите возраст дракона:");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = readNextLine().trim();

                if (line.isEmpty()) {
                    if (!scanner.isInteractive()) throw new IllegalArgumentException("Возраст в скрипте не может быть пустым!");
                    printer.printError("Возраст не может быть пустым!");
                    continue;
                }
                Long age = Long.parseLong(line);
                if (age > 0) return age;
                if (!scanner.isInteractive()) throw new IllegalArgumentException("Возраст в скрипте должен быть > 0!");
                printer.printError("Возраст должен быть больше нуля!");

            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо возраста указано не число!");
                printer.printError("Это не целое число! Введите цифры.");
            }
        }
    }

    private double askX() {
        if (scanner.isInteractive()) printer.println("Введите координату X:");
        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = readNextLine().trim();

                if (line.isEmpty()) {
                    if (!scanner.isInteractive()) throw new IllegalArgumentException("Координата X в скрипте не может быть пустой!");
                    printer.printError("Координата не может быть пустой!");
                    continue;
                }
                double x = Double.parseDouble(line);
                if (x <= 543) return x;

                if (!scanner.isInteractive()) throw new IllegalArgumentException("X в скрипте > 543!");
                printer.printError("Координата X должна быть не больше 543!");

            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо X указано не число!");
                printer.printError("Это не число!");
            }
        }
    }

    private Long askY() {
        if (scanner.isInteractive()) printer.println("Введите координату Y:");
        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = readNextLine().trim();
                if (line.isEmpty()) {
                    if (!scanner.isInteractive()) throw new IllegalArgumentException("Координата Y в скрипте не может быть пустой!");
                    printer.printError("Координата не может быть пустой!");
                    continue;
                }
                return Long.parseLong(line);

            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо Y указано не число!");
                printer.printError("Это не целое число!");
            }
        }
    }

    public Coordinates askCoordinates() {
        return new Coordinates(askX(), askY());
    }

    public DragonCave askCave() {
        if (scanner.isInteractive()) printer.println("Хотите добавить пещеру? (yes/no):");

        while (true) {
            if (scanner.isInteractive()) printer.print("> ");
            String line = readNextLine().toLowerCase();
            if (line.equals("нет") || line.equals("no") || line.isEmpty() || line.equals("null")) return null;
            if (line.equals("да") || line.equals("yes")) break;
            if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте неверный ответ на вопрос о пещере!");
            printer.printError("Введите 'yes' или 'no'!");
        }

        return new DragonCave(askDepth(), askNumberOfTreasures());
    }

    private Long askDepth() {
        if (scanner.isInteractive()) printer.println("Введите глубину пещеры (Enter для null):");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = readNextLine().trim();
                if (line.isEmpty()) return null;
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо глубины указано не число!");
                printer.printError("Это не целое число! Введите цифры или нажмите Enter.");
            }
        }
    }

    private Integer askNumberOfTreasures() {
        if (scanner.isInteractive()) printer.println("Введите количество сокровищ (Enter для null):");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = readNextLine().trim();
                if (line.isEmpty()) return null;
                Integer value = Integer.parseInt(line);
                if (value > 0) return value;

                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте сокровища <= 0!");
                printer.printError("Количество сокровищ должно быть больше 0!");
            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо сокровищ указано не число!");
                printer.printError("Это не целое число! Введите цифры или нажмите Enter.");
            }
        }
    }

    public Color askColor() {
        if (scanner.isInteractive()) {
            printer.println("Список доступных цветов: " + Arrays.toString(Color.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите цвет:");
                printer.print("> ");
            }
            String input = readNextLine().toUpperCase();

            if (input.isEmpty()) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("Цвет в скрипте не может быть пустым!");
                printer.printError("Цвет не может быть пустым!");
                continue;
            }

            try {
                return Color.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте указан неверный цвет: " + input);
                printer.printError("Такого цвета нет в списке!");
            }
        }
    }

    public DragonType askDragonType() {
        if (scanner.isInteractive()) {
            printer.println("Список типов драконов: " + Arrays.toString(DragonType.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите тип (Enter для null):");
                printer.print("> ");
            }
            String input = readNextLine().toUpperCase();

            if (input.isEmpty()) return null;

            try {
                return DragonType.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте указан неверный тип: " + input);
                printer.printError("Такого типа нет в списке!");
            }
        }
    }

    public DragonCharacter askDragonCharacter() {
        if (scanner.isInteractive()) {
            printer.println("Список характеров: " + Arrays.toString(DragonCharacter.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите характер (Enter для null):");
                printer.print("> ");
            }
            String input = readNextLine().toUpperCase();

            if (input.isEmpty()) return null;

            try {
                return DragonCharacter.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте указан неверный характер: " + input);
                printer.printError("Такого характера нет в списке!");
            }
        }
    }

    public DragonDTO dragonDTO() {
        String name = askName();
        Coordinates coords = askCoordinates();
        Long age = askAge();
        Color color = askColor();
        DragonType type = askDragonType();
        DragonCharacter character = askDragonCharacter();
        DragonCave cave = askCave();

        return new DragonDTO(name, coords, age, color, type, character, cave);
    }

    private String readNextLine() {
        String line = scanner.readLine();
        if(line == null) throw new IllegalArgumentException("Ввод прерван пользователем");
        return line.trim();
    }
}