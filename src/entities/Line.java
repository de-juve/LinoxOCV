package entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Line {
    public LinkedList<Point> points;
    public int avgWidth = 0;
    public int label;
    ArrayList<Line> connection;
    ArrayList<Point> borderPoints;

    public Line() {
        points = new LinkedList<>();
        connection = new ArrayList<>();
    }

    public void add(Point point) {
        points.add(point);
    }

    public void addConnection(Line line) {
        connection.add(line);
    }

    public ArrayList<Point> getBorderPoints() {
        if (borderPoints.isEmpty()) {
            borderPoints.add(points.get(0));
            borderPoints.add(points.get(points.size() - 1));
        }
        return borderPoints;
    }

    public ArrayList<Line> getConnection() {
        return connection;
    }

    public void extractBorderPoints() {
        borderPoints = new ArrayList<>();
        Iterator<Point> iterator = points.iterator();
        while (iterator.hasNext()) {
            Point current = iterator.next();
            if (current.connections.size() == 1) {
                borderPoints.add(current);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Line) {
            if (this.label == ((Line) obj).label) {
                return true;
            }
        }
        return false;
    }

}
