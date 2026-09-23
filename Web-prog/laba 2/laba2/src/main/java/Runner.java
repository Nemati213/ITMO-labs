import com.fastcgi.FCGIInterface;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public final class Runner {
    private static final Set<String> EXPECTED_PARAMETERS = Set.of("x", "y", "r");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final FCGIInterface fastCgi = new FCGIInterface();

    public void run() {
        while (fastCgi.FCGIaccept() >= 0) {
            try {
                if (!isGetRequest()) {
                    ResponseSender.sendError(405, "Method Not Allowed", "Поддерживается только метод GET");
                    continue;
                }

                CheckResult result = handleRequest();
                ResponseSender.sendSuccess(
                        result.x(),
                        result.y(),
                        result.r(),
                        result.hit(),
                        result.currentTime(),
                        result.executionTimeNanos()
                );
            } catch (ValidationException exception) {
                ResponseSender.sendError(400, "Bad Request", exception.getMessage());
            } catch (Exception exception) {
                exception.printStackTrace(System.err);
                ResponseSender.sendError(500, "Internal Server Error", "Внутренняя ошибка сервера");
            }
        }
    }

    private static boolean isGetRequest() {
        return "GET".equalsIgnoreCase(System.getProperty("REQUEST_METHOD", ""));
    }

    private CheckResult handleRequest() {
        long startedAt = System.nanoTime();
        String query = System.getProperty("QUERY_STRING", "");
        Map<String, String> parameters = QueryParser.parse(query);
        validateParameterNames(parameters);

        String rawX = parameters.get("x");
        String rawY = parameters.get("y");
        String rawR = parameters.get("r");

        Validator.validate(rawX, rawY, rawR);

        double x = parseDouble(rawX);
        double y = parseDouble(rawY);
        double r = parseDouble(rawR);
        boolean hit = AreaChecker.check(x, y, r);
        long executionTimeNanos = System.nanoTime() - startedAt;
        String currentTime = OffsetDateTime.now().format(TIME_FORMATTER);

        return new CheckResult(x, y, r, hit, currentTime, executionTimeNanos);
    }

    private static void validateParameterNames(Map<String, String> parameters) {
        for (String parameterName : parameters.keySet()) {
            if (!EXPECTED_PARAMETERS.contains(parameterName)) {
                throw new ValidationException("Неизвестный параметр: " + parameterName);
            }
        }
    }

    private static double parseDouble(String value) {
        return Double.parseDouble(value.trim().replace(',', '.'));
    }

    private record CheckResult(
            double x,
            double y,
            double r,
            boolean hit,
            String currentTime,
            long executionTimeNanos
    ) {
    }
}
