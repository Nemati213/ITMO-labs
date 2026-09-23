import java.math.BigDecimal;
import java.util.Set;

public final class Validator {
    private static final BigDecimal MIN_Y = new BigDecimal("-3");
    private static final BigDecimal MAX_Y = new BigDecimal("3");
    private static final BigDecimal MIN_R = new BigDecimal("2");
    private static final BigDecimal MAX_R = new BigDecimal("5");
    private static final Set<BigDecimal> ALLOWED_X_VALUES = Set.of(
            new BigDecimal("-2"),
            new BigDecimal("-1.5"),
            new BigDecimal("-1"),
            new BigDecimal("-0.5"),
            BigDecimal.ZERO,
            new BigDecimal("0.5"),
            BigDecimal.ONE,
            new BigDecimal("1.5"),
            new BigDecimal("2")
    );

    private Validator() {
    }

    public static void validate(String x, String y, String r) {
        validateX(x);
        validateY(y);
        validateR(r);
    }

    private static void validateX(String x) {
        BigDecimal value = parseNumber(x, "X");

        if (ALLOWED_X_VALUES.stream().noneMatch(allowed -> allowed.compareTo(value) == 0)) {
            throw new ValidationException("Недопустимое значение X");
        }
    }

    private static void validateY(String y) {
        BigDecimal value = parseNumber(y, "Y");

        if (value.compareTo(MIN_Y) < 0 || value.compareTo(MAX_Y) > 0) {
            throw new ValidationException("Y должен находиться в диапазоне [-3; 3]");
        }
    }

    private static void validateR(String r) {
        BigDecimal value = parseNumber(r, "R");

        if (value.compareTo(MIN_R) < 0 || value.compareTo(MAX_R) > 0) {
            throw new ValidationException("R должен находиться в диапазоне [2; 5]");
        }
    }

    private static BigDecimal parseNumber(String rawValue, String parameterName) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new ValidationException(parameterName + " не задан");
        }

        try {
            return new BigDecimal(rawValue.trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            throw new ValidationException(parameterName + " должен быть числом");
        }
    }
}
