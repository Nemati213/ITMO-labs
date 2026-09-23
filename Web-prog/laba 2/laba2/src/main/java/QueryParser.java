import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryParser {
    private QueryParser() {
    }

    public static Map<String, String> parse(String query) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }

        Map<String, String> parameters = new LinkedHashMap<>();

        for (String pair : query.split("&", -1)) {
            int separatorIndex = pair.indexOf('=');

            if (separatorIndex <= 0) {
                throw new ValidationException("Некорректная строка запроса");
            }

            String name = decode(pair.substring(0, separatorIndex));
            String value = decode(pair.substring(separatorIndex + 1));

            if (name.isBlank()) {
                throw new ValidationException("Имя параметра не может быть пустым");
            }

            if (parameters.putIfAbsent(name, value) != null) {
                throw new ValidationException("Параметр " + name + " передан несколько раз");
            }
        }

        return Map.copyOf(parameters);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Некорректная кодировка параметров запроса");
        }
    }
}
