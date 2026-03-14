package ru.itmo.nemat.lab5.utility;

import ru.itmo.nemat.lab5.models.*;

import java.util.Arrays;

/**
 * Класс DragonAsker. Отвечает за чтение данных из входного потока и их валидацию.
 * Поддерживает два режима работы: интерактивный (с подсказками) и режим чтения скрипта (строгий).
 *
 * @author Nemati213
 */
public class DragonAsker {

    private final OutputPrinter printer;
    private InputReader scanner;

    /**
     * Конструктор аскера.
     * @param printer Инструмент для вывода сообщений.
     * @param scanner Текущий инструмент чтения данных.
     */
    public DragonAsker(OutputPrinter printer, InputReader scanner) {
        this.printer = printer;
        this.scanner = scanner;
    }
    /**
     * Устанавливает новый источник чтения данных.
     * Используется для переключения между консолью и файлами скриптов.
     * @param scanner Новый инструмент чтения данных.
     */
    public void setScanner(InputReader scanner) {
        this.scanner = scanner;
    }

    /**
     * Запрашивает имя дракона.
     * @return Считанное имя.
     * @throws IllegalArgumentException Если в режиме скрипта введено пустое значение.
     */
    public String askName() {
        if (scanner.isInteractive()) printer.println("Введите имя дракона:");

        while (true) {
            if (scanner.isInteractive()) printer.print("> ");
            String line = scanner.readLine().trim();

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

    /**
     * Запрашивает возраст дракона.
     * @return Считанный возраст.
     * @throws IllegalArgumentException Если в режиме скрипта данные некорректны.
     */
    public Long askAge() {
        if (scanner.isInteractive()) printer.println("Введите возраст дракона:");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = scanner.readLine().trim();

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

    /**
     * Вспомогательный метод для запроса координаты X.
     * @return Считанная координата X.
     */
    private double askX() {
        if (scanner.isInteractive()) printer.println("Введите координату X:");
        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = scanner.readLine().trim();

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

    /**
     * Вспомогательный метод для запроса координаты Y.
     * @return Считанная координата Y.
     */
    private Long askY() {
        if (scanner.isInteractive()) printer.println("Введите координату Y:");
        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = scanner.readLine().trim();
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

    /**
     * Собирает объект координат, запрашивая X и Y.
     * @return Объект Coordinates.
     */
    public Coordinates askCoordinates() {
        return new Coordinates(askX(), askY());
    }

    /**
     * Запрашивает данные о пещере дракона.
     * @return Объект DragonCave или null, если пользователь отказался от ввода.
     */
    public DragonCave askCave() {
        if (scanner.isInteractive()) printer.println("Хотите добавить пещеру? (yes/no):");

        while (true) {
            if (scanner.isInteractive()) printer.print("> ");
            String line = scanner.readLine().trim().toLowerCase();
            if (line.equals("нет") || line.equals("no") || line.isEmpty() || line.equals("null")) return null;
            if (line.equals("да") || line.equals("yes")) break;
            if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте неверный ответ на вопрос о пещере!");
            printer.printError("Введите 'yes' или 'no'!");
        }

        return new DragonCave(askDepth(), askNumberOfTreasures());
    }

    /**
     * Вспомогательный метод для запроса глубины пещеры.
     * @return Считанная глубина или null.
     */
    private Long askDepth() {
        if (scanner.isInteractive()) printer.println("Введите глубину пещеры (Enter для null):");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = scanner.readLine().trim();
                if (line.isEmpty()) return null;
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте вместо глубины указано не число!");
                printer.printError("Это не целое число! Введите цифры или нажмите Enter.");
            }
        }
    }

    /**
     * Вспомогательный метод для запроса количества сокровищ.
     * @return Считанное количество сокровищ или null.
     */
    private Integer askNumberOfTreasures() {
        if (scanner.isInteractive()) printer.println("Введите количество сокровищ (Enter для null):");

        while (true) {
            try {
                if (scanner.isInteractive()) printer.print("> ");
                String line = scanner.readLine().trim();
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

    /**
     * Запрашивает цвет дракона.
     * @return Элемент перечисления Color.
     */
    public Color askColor() {
        if (scanner.isInteractive()) {
            printer.println("Список доступных цветов: " + Arrays.toString(Color.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите цвет:");
                printer.print("> ");
            }
            String input = scanner.readLine().trim().toUpperCase();

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

    /**
     * Запрашивает тип дракона.
     * @return Элемент перечисления DragonType или null.
     */
    public DragonType askDragonType() {
        if (scanner.isInteractive()) {
            printer.println("Список типов драконов: " + Arrays.toString(DragonType.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите тип (Enter для null):");
                printer.print("> ");
            }
            String input = scanner.readLine().trim().toUpperCase();

            if (input.isEmpty()) return null;

            try {
                return DragonType.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте указан неверный тип: " + input);
                printer.printError("Такого типа нет в списке!");
            }
        }
    }

    /**
     * Запрашивает характер дракона.
     * @return Элемент перечисления DragonCharacter или null.
     */
    public DragonCharacter askDragonCharacter() {
        if (scanner.isInteractive()) {
            printer.println("Список характеров: " + Arrays.toString(DragonCharacter.values()));
        }

        while (true) {
            if (scanner.isInteractive()) {
                printer.println("Введите характер (Enter для null):");
                printer.print("> ");
            }
            String input = scanner.readLine().trim().toUpperCase();

            if (input.isEmpty()) return null;

            try {
                return DragonCharacter.valueOf(input);
            } catch (IllegalArgumentException e) {
                if (!scanner.isInteractive()) throw new IllegalArgumentException("В скрипте указан неверный характер: " + input);
                printer.printError("Такого характера нет в списке!");
            }
        }
    }
}