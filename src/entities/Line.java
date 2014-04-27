package entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Линия есть последовательность точек Point,
 * Линия имеет:
 *  начало и конец - borderPoints
 *  соседей - connection
 *  множество точек - points
 */
public class Line {
    public LinkedList<Point> points;
    public int avgWidth = 0;
    public int label;
    public double curvature;
    ArrayList<Line> connection;
    ArrayList<Point> borderPoints;

    public Line() {
        points = new LinkedList<>();
        connection = new ArrayList<>();
        borderPoints = new ArrayList<>();
        curvature = -1;
    }

    public void add(Point point) {
        points.add(point);
    }

    public Point direction() {
        Point p1 = points.get(0);
        Point p2 = points.get(points.size() - 1);
        return new Point(p1.x - p2.x, p1.y - p2.y);
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

    /**
     * Определим кривизну кривой, как средня кривизна в каждой точке
     * @return
     */
    public double getCurvature() {
         if(curvature < 0) {
            for(Point p : points) {
                curvature += p.getCurvature();
            }
             curvature /= points.size();
         }
        return curvature;
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
