package aa;

public class Vector2E {
    public double x;
    public double y;

    public Vector2E() {
        this(0, 0);
    }

    public Vector2E(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2E copy() {
        return new Vector2E(x, y);
    }

    public Vector2E add(Vector2E other) {
        return new Vector2E(this.x + other.x, this.y + other.y);
    }

    public Vector2E sub(Vector2E other) {
        return new Vector2E(this.x - other.x, this.y - other.y);
    }

    public Vector2E mul(double s) {
        return new Vector2E(this.x * s, this.y * s);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2E normalized() {
        double len = length();
        if (len == 0) return new Vector2E(0, 0);
        return new Vector2E(x / len, y / len);
    }

    public double distance(Vector2E other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Vector2E limit(double max) {
        double len = length();
        if (len > max && len > 0) {
            double factor = max / len;
            return new Vector2E(x * factor, y * factor);
        }
        return this;
    }

    public static double dot(Vector2E a, Vector2E b) {
        return a.x * b.x + a.y * b.y;
    }

    public static double angleBetween(Vector2E a, Vector2E b) {
        double lenA = a.length();
        double lenB = b.length();
        if (lenA == 0 || lenB == 0) return 0;
        double cos = dot(a, b) / (lenA * lenB);
        cos = Math.max(-1.0, Math.min(1.0, cos));
        return Math.acos(cos);
    }
}
