import java.nio.charset.StandardCharsets;

public final class ResponseSender {
    private static final String HTTP_RESPONSE_FORMAT = """
            HTTP/1.1 %d %s\r
            Content-Type: application/json; charset=UTF-8\r
            Content-Length: %d\r
            Cache-Control: no-store\r
            \r
            %s""";

    private static final String SUCCESS_JSON_FORMAT = """
            {"x":%s,"y":%s,"r":%s,"hit":%s,"currentTime":"%s","executionTimeNanos":%d}""";

    private static final String ERROR_JSON_FORMAT = """
            {"error":"%s"}""";

    private ResponseSender() {
    }

    public static void sendSuccess(
            double x,
            double y,
            double r,
            boolean hit,
            String currentTime,
            long executionTimeNanos
    ) {
        String body = SUCCESS_JSON_FORMAT.formatted(
                x,
                y,
                r,
                hit,
                escapeJson(currentTime),
                executionTimeNanos
        );

        send(200, "OK", body);
    }

    public static void sendError(int statusCode, String reasonPhrase, String message) {
        String body = ERROR_JSON_FORMAT.formatted(escapeJson(message));
        send(statusCode, reasonPhrase, body);
    }

    private static void send(int statusCode, String reasonPhrase, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String response = HTTP_RESPONSE_FORMAT.formatted(
                statusCode,
                reasonPhrase,
                bodyBytes.length,
                body
        );
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        System.out.write(responseBytes, 0, responseBytes.length);
        System.out.flush();
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length());

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);

            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append("\\u%04x".formatted((int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
