package entities;

import java.util.LinkedList;

public class Line {
    public LinkedList<Point> points;
    public int avgWidth = 0;

    public Line() {
        points = new LinkedList<>();
    }

    public void add(Point point) {
        points.add(point);
    }

}
