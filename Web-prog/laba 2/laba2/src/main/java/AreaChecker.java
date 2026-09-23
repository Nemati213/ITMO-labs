public final class AreaChecker {
    private AreaChecker() {
    }

    public static boolean check(double x, double y, double r) {
        return isInsideQuarterCircle(x, y, r)
                || isInsideTriangle(x, y, r)
                || isInsideRectangle(x, y, r);
    }

    private static boolean isInsideQuarterCircle(double x, double y, double r) {
        return x <= 0 && y >= 0 && x * x + y * y <= r * r;
    }

    private static boolean isInsideTriangle(double x, double y, double r) {
        return x >= 0 && y >= 0 && x + y <= r / 2;
    }

    private static boolean isInsideRectangle(double x, double y, double r) {
        return x >= 0 && x <= r / 2 && y <= 0 && y >= -r;
    }
}
