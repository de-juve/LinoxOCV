package entities;

import java.util.ArrayList;
import java.util.LinkedList;

public class Line {
    public LinkedList<Point> points;
    public int avgWidth = 0;
    public int label;
    ArrayList<Line> connection;

    public Line() {
        points = new LinkedList<>();
        connection = new ArrayList<>();
    }

    public void add( Point point ) {
        points.add( point );
    }

    public void addConnection( Line line ) {
        connection.add( line );
    }

}
