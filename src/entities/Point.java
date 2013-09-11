package entities;

public class Point {
    public int x;
    public int y;
    public Direction direction;
    public int width;

    public Point(int _x, int _y) {
        x = _x;
        y = _y;
        width = -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (this.x == ((Point) obj).x && this.y == ((Point) obj).y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "( " + x + "; " + y + ") DIRECTION: " + direction;
    }
}
