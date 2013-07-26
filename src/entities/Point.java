package entities;

public class Point {
    public int x;
    public int y;

    public Point(int _x, int _y) {
        x = _x;
        y = _y;
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
}
