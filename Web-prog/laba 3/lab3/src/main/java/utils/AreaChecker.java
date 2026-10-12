package utils;

public final class AreaChecker {
    private AreaChecker() {
    }

    public static boolean check(double x, double y, double r) {
        return isInsideTriangle(x, y, r)
                || isInsideRectangle(x, y, r)
                || isInsideQuarterCircle(x, y, r);
    }

    private static boolean isInsideQuarterCircle(double x, double y, double r) {
        return x >= 0 && y <= 0 && x * x + y * y <= r * r;
    }

    private static boolean isInsideTriangle(double x, double y, double r) {
        return x <= 0 && x >= -r && y >= 0 && y <= x + r;
    }

    private static boolean isInsideRectangle(double x, double y, double r) {
        return x >= 0 && x <= r && y >= 0 && y <= r / 2;
    }
}
