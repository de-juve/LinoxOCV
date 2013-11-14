package entities;

import java.util.ArrayList;

public class Point extends org.opencv.core.Point {
    public int x;
    public int y;
    public Direction direction;
    public ArrayList<Connection> connections;
    public int width;
    public double weight;
    public double fDegree, bDegree;
    public boolean isCrossroad;
    public boolean isExtreme;

    public Point( int _x, int _y ) {
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

    public void addConnection( Connection _conn ) {
        if ( _conn.p2.equals( this ) ) {
            _conn.p2 = _conn.p1;
            _conn.p1 = this;
        }
        connections.add( _conn );
        if ( connections.size() > 2 ) {
            isCrossroad = true;
        }
        if ( connections.size() > 1 ) {
            isExtreme = false;
        }
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Point || obj instanceof org.opencv.core.Point ) {
            if ( ( this.x == ( ( Point ) obj ).x && this.y == ( ( Point ) obj ).y ) ||
                    ( this.x == ( int ) ( ( org.opencv.core.Point ) obj ).x && this.y == ( int ) ( ( org.opencv.core.Point ) obj ).y ) ) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        String message = "( " + x + "; " + y + ")";
        if ( direction != null ) {
            message += " DIRECTION: " + direction;
        }
        if ( weight >= 0 ) {
            message += " WEIGHT: " + weight;
        }
        return message;
    }
}
