package ru.itmo.nemat.utils;

import ru.itmo.nemat.exceptions.InvalidArgumentException;
import ru.itmo.nemat.exceptions.InvalidLoginArgumentException;
import ru.itmo.nemat.models.Color;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.models.DragonType;

public class Validator {

    public static void validateLogin(String login, String password) throws InvalidLoginArgumentException {
        if (login.isEmpty() || password.isEmpty())
            throw new InvalidLoginArgumentException("validation.error.empty_auth");

        if(!login.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$"))
            throw new InvalidLoginArgumentException("validation.error.auth_regex");

    }

    public static void validateRegister(String login, String password, String confirmPassword) throws InvalidLoginArgumentException {
        if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new InvalidLoginArgumentException("validation.error.empty_auth");
        }
        if(!login.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$") ||  !confirmPassword.matches("^[a-zA-Z0-9]+$")) {
            throw new InvalidLoginArgumentException("validation.error.auth_regex");
        }
        if(!password.equals(confirmPassword)) {
            throw new InvalidLoginArgumentException("validation.error.password_mismatch");
        }
    }

    public static String validateName(String line) throws InvalidArgumentException {
        if(line.isEmpty()) throw new InvalidArgumentException("validation.error.empty_name");
        return line;
    }

    public static long validateAge(String line) throws InvalidArgumentException {
        if(line.isEmpty()) throw new InvalidArgumentException("validation.error.empty_age");
        try{
            long age = Long.parseLong(line);
            if(age < 0) throw new IllegalArgumentException("validation.error.negative_age");
            return age;
        } catch(NumberFormatException e){
            throw new InvalidArgumentException("validation.error.age_not_number");
        }
    }

    public static double validateX(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty())
            throw new InvalidArgumentException("validation.error.empty_x");
        try {
            double x = Double.parseDouble(line.trim());
            if (x > 200 || x < 0) throw new InvalidArgumentException("validation.error.range_x");
            return x;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("validation.error.x_not_number");
        }
    }

    public static Long validateY(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty())
            throw new InvalidArgumentException("validation.error.empty_y");
        try {
            long y = Long.parseLong(line.trim());
            if(y < 0 || y > 100) throw new InvalidArgumentException("validation.error.range_y");
            return y;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("validation.error.y_not_number");
        }
    }

    public static boolean validateCaveDecision(String line) throws InvalidArgumentException {
        String input = line.trim().toLowerCase();
        if (input.equals("yes") || input.equals("да")) return true;
        if (input.equals("no") || input.equals("нет") || input.isEmpty() || input.equals("null")) return false;
        throw new InvalidArgumentException("validation.error.yes_no");
    }

    public static Long validateDepth(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty() || line.equals("null")) return null;
        try {
            return Long.parseLong(line.trim());
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("validation.error.depth_not_number");
        }
    }

    public static Integer validateTreasures(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty() || line.equals("null")) return null;
        try {
            Integer value = Integer.parseInt(line.trim());
            if (value <= 0) throw new IllegalArgumentException("validation.error.negative_treasures");
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("validation.error.treasures_not_number");
        }
    }

    public static Color validateColor(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty())
            throw new InvalidArgumentException("validation.error.empty_color");
        try {
            return Color.valueOf(line.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("validation.error.invalid_color");
        }
    }

    public static DragonType validateDragonType(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty() || line.equals("null")) return null;
        try {
            return DragonType.valueOf(line.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("validation.error.invalid_type");
        }
    }

    public static DragonCharacter validateDragonCharacter(String line) throws InvalidArgumentException {
        if (line == null || line.trim().isEmpty() || line.equals("null")) return null;
        try {
            return DragonCharacter.valueOf(line.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("validation.error.invalid_character");
        }
    }
}

