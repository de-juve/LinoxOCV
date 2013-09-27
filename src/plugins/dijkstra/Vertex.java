package plugins.dijkstra;

import entities.Point;

public class Vertex implements Comparable<Vertex> {
    public final String name;
    public final Point point;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public boolean label;

    public Vertex( String argName ) {
        name = argName;
        point = new Point( -1, -1 );
    }

    public Vertex( Point argPoint ) {
        point = argPoint;
        name = argPoint.toString();
    }

    public String toString() {
        return name;
    }

    public int compareTo( Vertex other ) {
        return Double.compare( minDistance, other.minDistance );
    }
}
