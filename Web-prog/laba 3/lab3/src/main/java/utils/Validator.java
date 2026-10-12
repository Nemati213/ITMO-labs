package utils;

import com.sun.jdi.event.StepEvent;
import exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.Set;

public final class Validator {
    private static final BigDecimal MIN_Y = new BigDecimal("-3");
    private static final BigDecimal MAX_Y = new BigDecimal("5");
    private static final Set<BigDecimal> ALLOWED_X_VALUES = Set.of(
            new BigDecimal("-5"),
            new BigDecimal("-4"),
            new BigDecimal("-3"),
            new BigDecimal("-2"),
            new BigDecimal("-1"),
            BigDecimal.ZERO,
            BigDecimal.ONE,
            new BigDecimal("2"),
            new BigDecimal("3")
    );
    private static final Set<BigDecimal> ALLOWED_R_VALUES = Set.of(
            BigDecimal.ONE,
            new BigDecimal("2"),
            new BigDecimal("3"),
            new BigDecimal("4"),
            new BigDecimal("5")
    );

    private static final String FORM = "form";
    private static final String CANVAS = "canvas";

    private Validator() {
    }

    public static void validate(String x, String y, String r, String source, String timeZone) {
        boolean fromForm = validateSource(source);
        validateX(x, fromForm);
        validateY(y, fromForm);
        validateR(r, fromForm);
        validateTimeZone(timeZone);

    }

    private static void validateX(String x, boolean fromForm) {
        BigDecimal value = parseNumber(x, "X");

        if (fromForm && ALLOWED_X_VALUES.stream().noneMatch(allowed -> allowed.compareTo(value) == 0)) {
            throw new ValidationException("Недопустимое значение X");
        }
    }

    private static void validateY(String y, boolean fromForm) {
        BigDecimal value = parseNumber(y, "Y");

        if (fromForm && (value.compareTo(MIN_Y) < 0 || value.compareTo(MAX_Y) > 0)) {
            throw new ValidationException("Y должен находиться в диапазоне [-3; 5]");
        }
    }

    private static void validateR(String r, boolean fromForm) {
        BigDecimal value = parseNumber(r, "R");

        if (fromForm && ALLOWED_R_VALUES.stream().noneMatch(allowed -> allowed.compareTo(value) == 0)) {
            throw new ValidationException("R должен быть одним из значений: 1, 2, 3, 4, 5");
        }
    }

    private static BigDecimal parseNumber(String rawValue, String parameterName) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new ValidationException(parameterName + " не задан");
        }

        try {
            BigDecimal value = new BigDecimal(rawValue.trim().replace(',', '.'));
            if (!Double.isFinite(value.doubleValue())) {
                throw new ValidationException(parameterName + " должен быть конечным числом");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new ValidationException(parameterName + " должен быть числом");
        }
    }

    private static boolean validateSource(String source) {
        if(!FORM.equals(source) && !CANVAS.equals(source)) {
            throw new ValidationException("Некорректный источник данных");
        }
        return FORM.equals(source);
    }

    private static void validateTimeZone(String timeZone) {
        if (!java.time.ZoneId.getAvailableZoneIds().contains(timeZone)) {
            throw new ValidationException("Некорректный часовой пояс");
        }
    }
}
