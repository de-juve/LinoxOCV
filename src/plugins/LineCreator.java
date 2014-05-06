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
    /**
     * points - точки водораздела
     * epoints - точки контура, границы области..в случае линий водораздела - это будут все точки линий водораздела
     */
    public ArrayList<Point> points, edgePoints;
    /**
     * points_b - точки водораздела
     * epoints_b - точки вдоль границ линий водораздела
     * extreme_b - точки разделения линий, перекрестки
     */
    public boolean[] points_b, epoints_b, crossroad_b, extreme_b;
    public ArrayList<Line> lines;
    int lastId;

    public LineCreator( Mat img, ArrayList<Point> _points ) {
        image = img;
        points = _points;
        points_b = new boolean[( int ) image.total()];
        epoints_b = new boolean[( int ) image.total()];
        crossroad_b = new boolean[( int ) image.total()];
        extreme_b = new boolean[( int ) image.total()];
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
                if ( !p.equals( n ) && PixelsMentor.is4Neighbours( p, n ) ) {
                    p.addConnection( new Connection( p, n, distance( p, n ) ) );
                }
            }
        }
        for ( Point p : edgePoints ) {
            for ( Point n : edgePoints ) {
                if ( !p.equals( n ) && PixelsMentor.isDiagonalNeighbours( p, n ) && !points_b[id( p.x, n.y )] && !points_b[id( n.x, p.y )] ) {
                    p.addConnection( new Connection( p, n, distance( p, n ) ) );
                }
            }
        }

     /*
        /**
         * Корректируем правильность определения перекрестков.
         * Считаем не только связи, но и анализируем их - можем ли по ним пройти больше чем на len пикселов
         * /
        int len = 10;
        for ( Point p : edgePoints ) {
            if ( !p.isCrossroad ) {
                continue;
            }
            boolean[] visited = new boolean[points_b.length];
            int[] paths = new int[p.connections.size()];
            visited[id( p )] = true;
            for(int i = 0; i < p.connections.size(); i++) {
                Point n = p.connections.get(i).p2;
                if(visited[id(n)]) {
                    continue;
                }

                visited[id(n)] = true;
                paths[i] = 1;


            }
        }*/
        for ( Point p : edgePoints ) {
            if ( p.isCrossroad ) {
                crossroad_b[id( p )] = true;
                for ( Connection connection : p.connections ) {
                    if(!connection.p2.isCrossroad) {
                        connection.p2.isExtreme = true;
                        extreme_b[id(p)] = true;
                    }
                }
            }
            if ( p.isExtreme ) {
                extreme_b[id( p )] = true;
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
                    if ( !used[id( connection.p2 )] && !inQueue[id( connection.p2 )] && !connection.p2.isCrossroad ) {
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
        for ( Point p : edgePoints ) {
            if ( p.isExtreme && !p.isCrossroad && !used[id( p )] ) {
                return p;
            }
        }
        return null;
    }

    /**
     * Является ли точка одной из множества заданных точек (точек водораздела)
     * @param p
     * @return
     */
    public boolean isOneOfThePoints( Point p ) {
        return points_b[id( p.x, p.y )];
    }

    /**
     * Является ли точка крайней (например, точка границы области - контура)
     * @param p
     * @return
     */
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
