package entities;

import java.util.ArrayList;

public class Point extends org.opencv.core.Point {
    public int x;
    public int y;
    public double curvature;
    public double[] curv = new double[2];
    public Direction direction;
    public ArrayList<Connection> connections;
    public int width;
    public double weight;
    public double fDegree, bDegree;
    public boolean isCrossroad;
    public boolean isExtreme;
    public int lineLabel;


    public Point(int _x, int _y) {
        x = _x;
        y = _y;
        width = -1;
        weight = -1;
        fDegree = -1;
        bDegree = -1;
        isCrossroad = false;
        isExtreme = true;
        connections = new ArrayList<>();
    }

    public Point(int _x, int _y, double _curv) {
        x = _x;
        y = _y;
        curvature = _curv;
        width = -1;
        weight = -1;
        fDegree = -1;
        bDegree = -1;
        isCrossroad = false;
        isExtreme = true;
        connections = new ArrayList<>();
    }

    public Point(Point p) {
        x = p.x;
        y = p.y;
        curvature = p.curvature;
        width = p.width;
        weight = p.weight;
        fDegree = p.fDegree;
        bDegree = p.bDegree;
        isCrossroad = p.isCrossroad;
        isExtreme = p.isExtreme;
        connections = new ArrayList<>();
    }

    public double minus( Point p ) {
        return Math.pow(Math.abs(this.x) - Math.abs(p.x), 2) + Math.pow(Math.abs(this.y) - Math.abs(p.y), 2);
    }

    public double len(Point p) {
        return  Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    public void addConnection(Connection _conn) {
        if (_conn.p2.equals(this)) {
            _conn.p2 = _conn.p1;
            _conn.p1 = this;
        }
        connections.add(_conn);
        if (connections.size() > 2) {
            isCrossroad = true;
        }
        if (connections.size() > 1) {
            isExtreme = false;
        }
    }

    public double getCurvature() {
        return Math.sqrt(Math.pow(curv[0], 2) + Math.pow(curv[1], 2));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Point) {
            if (this.x == ((Point) obj).x && this.y == ((Point) obj).y) {
                return true;
            }
        } else if (obj instanceof org.opencv.core.Point) {
            if (this.x == (int) ((org.opencv.core.Point) obj).x && this.y == (int) ((org.opencv.core.Point) obj).y) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        String message = "( " + x + "; " + y + ")";
        if (direction != null) {
            message += " DIRECTION: " + direction;
        }
        if (weight >= 0) {
            message += " WEIGHT: " + weight;
        }
        return message;
    }
}
