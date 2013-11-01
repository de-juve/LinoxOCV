package plugins;

import entities.Connection;
import entities.Line;
import entities.PixelsMentor;
import entities.Point;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LineCreator {
    private Mat image;
    public ArrayList<Point> points, edgePoints;
    public boolean[] points_b, epoints_b;
    public ArrayList<Line> lines;
    int lastId;

    public LineCreator( Mat img, ArrayList<Point> _points ) {
        image = img;
        points = _points;
        points_b = new boolean[( int ) image.total()];
        epoints_b = new boolean[( int ) image.total()];
        edgePoints = new ArrayList<>();
        lastId = 0;

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

    public void createLines() {
        lines = new ArrayList<>();
        boolean[] used = new boolean[points_b.length];
        boolean[] inQueue = new boolean[points_b.length];

        Point unusedPoint = getUnusedPoint( used );
        while ( unusedPoint != null ) {
            Line line = new Line();
            Queue<Point> queue = new LinkedList<>();
            queue.add( unusedPoint );
            inQueue[id( unusedPoint )] = true;

            while ( !queue.isEmpty() ) {
                Point point = queue.poll();
                used[id( point )] = true;
                line.add( point );
                for ( Connection connection : point.connections ) {
                    if ( !used[id( connection.p2 )] && !inQueue[id( connection.p2 )] ) {
                        inQueue[id( connection.p2 )] = true;
                        queue.add( connection.p2 );
                    }
                }

            }
            lines.add( line );
            unusedPoint = getUnusedPoint( used );
        }
    }

    private Point getUnusedPoint( boolean[] used ) {
        for ( int i = lastId; i < ( int ) image.total(); i++ ) {
            if ( isEdgePoint2( i ) && !used[i] ) {
                lastId = i + 1;
                for ( Point p : edgePoints ) {
                    if ( id( p ) == i ) {
                        return p;
                    }
                }
            }
        }
        /*for (Point p : edgePoints) {
            if (!used[id(p)]) {
                return p;
            }
        }*/
        return null;
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

    private boolean isEdgePoint2( int id ) {
        return epoints_b[id];
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

    private int id( Point p ) {
        return id( p.x, p.y );
    }

    private int id( int x, int y ) {
        return x + y * image.width();
    }
}
