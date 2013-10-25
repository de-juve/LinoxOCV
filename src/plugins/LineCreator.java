package plugins;

import entities.Connection;
import entities.PixelsMentor;
import entities.Point;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class LineCreator {
    private Mat image;
    public ArrayList<Point> points, edgePoints;
    public boolean[] points_b, epoints_b;

    public LineCreator( Mat img, ArrayList<Point> _points ) {
        image = img;
        points = _points;
        points_b = new boolean[( int ) image.total()];
        epoints_b = new boolean[( int ) image.total()];
        edgePoints = new ArrayList<>();

        for ( Point point : points ) {
            points_b[id( point.x, point.y )] = true;
        }
    }

    public void extractEdgePoints() {
        Mat img = image.clone();

        for ( Point point : points ) {
            if ( isEdgePoint( point ) ) {
                edgePoints.add( point );
                epoints_b[id( point.x, point.y )] = true;
                img.put( point.y, point.x, 0, 255, 0 );
            }
        }
        //   DataCollector.INSTANCE.addtoHistory( "edge points", img );
        for ( Point p : edgePoints ) {
            for ( Point n : edgePoints ) {
                if ( PixelsMentor.isNeighbours( p, n ) && !p.equals( n ) ) {
                    p.addConnection( new Connection( p, n, distance( p, n ) ) );
                }
            }
        }
    }

    public boolean isOneOfThePoints( Point p ) {
        return points_b[id( p.x, p.y )];
    }

    public boolean isEdgePoint( Point p ) {
        ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 1 );
        for ( Point n : neighs ) {
            if ( !isOneOfThePoints( n ) ) {
                return true;
            }
        }
        return false;
    }

    private double distance( Point p, Point n ) {
        return Math.sqrt( Math.pow( p.x - n.x, 2 ) + Math.pow( p.y - n.y, 2 ) );
    }

    public void addEdgePoint( Point p ) {
        points.add( p );
        points_b[id( p.x, p.y )] = true;

        if ( isEdgePoint2( p ) ) {
            ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 1 );
            for ( Point np : neighs ) {
                if ( isOneOfThePoints( np ) && !isEdgePoint2( np ) ) {
                    points.remove( np );
                }
            }
            points.add( p );
        }
    }

    private boolean isEdgePoint2( Point p ) {
        return epoints_b[id( p.x, p.y )];
    }

    public void removePoint( Point p ) {
        points.remove( p );
        points_b[id( p.x, p.y )] = false;
    }

    public Point getClosesPoint( Point p ) {
        Point cp = p;
        double d = Double.POSITIVE_INFINITY;
        ArrayList<Point> neighs = PixelsMentor.getNeighborhoodOfPixel( p.x, p.y, image, 2 );
        for ( Point ep : neighs ) {
            if ( isEdgePoint2( ep ) && distance( p, ep ) < d && ep.connections.size() > 0 ) {
                d = distance( p, ep );
                cp = ep;
            }
        }
        return cp;
    }

    private int id( int x, int y ) {
        return x + y * image.width();
    }
}
