package entities;

import java.util.LinkedList;

public class Line {
    public LinkedList<Point> points;

    public Line() {
        points = new LinkedList<>();
    }

    public void add( Point point ) {
        points.add( point );
    }

}
