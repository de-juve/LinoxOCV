package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PointMentor {
    public static ArrayList<Point> extractPoints( Mat img ) {
        ArrayList<Point> points = new ArrayList<>();
        for ( int row = 0; row < img.height(); row++ ) {
            for ( int col = 0; col < img.width(); col++ ) {
                if ( ( int ) img.get( row, col )[0] == 255 ) {
                    points.add( new Point( col, row ) );
                }
            }
        }
        return points;
    }

    public static ArrayList<Point> extractExtreamPoints( Mat img, ArrayList<Point> points ) {
        ArrayList<Point> extremePoints = new ArrayList<>();
        for ( Point p : points ) {
            ArrayList<Integer> neighbours = PixelsMentor.defineNeighboursIdsWithSameValue(
                    p.x + p.y * img.width(), img );
            if ( neighbours.size() > 1 ) {
                continue;
            } else {
                extremePoints.add( p );
            }
        }
        return extremePoints;
    }

    public static ArrayList<Line> linkPoints( Mat img, ArrayList<Point> points, ArrayList<Point> exPoints ) {
        ArrayList<Line> res = new ArrayList<>();
        boolean[] visited = new boolean[( int ) img.total()];
        for ( Point p : exPoints ) {
            if ( !visited[p.x + p.y * img.width()] ) {
                Line line = new Line();
                Queue<Point> queue = new LinkedList<>();
                queue.add( p );

                while ( !queue.isEmpty() ) {
                    Point point = queue.remove();
                    if ( !visited[point.x + point.y * img.width()] ) {
                        visited[point.x + point.y * img.width()] = true;
                        line.add( point );
                        ArrayList<Integer> neighbours = PixelsMentor.defineNeighboursIdsWithSameValue(
                                point.x + point.y * img.width(), img );
                        int size = neighbours.size();
                        for ( int i = 0; i < size; i++ ) {
                            Point n = getNextPoint( point, neighbours, points, img );
                            neighbours.remove( ( Integer ) ( n.x + n.y * img.width() ) );
                            queue.add( n );
                        }
                    }
                }
                res.add( line );
            }
        }

        return res;
    }

    private static Point getNextPoint( Point p,
                                       ArrayList<Integer> neighbours,
                                       ArrayList<Point> points,
                                       Mat img ) {
        Point neighbor = new Point( -1, -1 );
        int i = 0;

        while ( i < 8 ) {
            switch ( i ) {
                case 0: {
                    neighbor = getUpNeighbor( p );
                    break;
                }
                case 1: {
                    neighbor = getRightNeighbor( p );
                    break;
                }
                case 2: {
                    neighbor = getUpRightNeighbor( p );
                    break;
                }
                case 3: {
                    neighbor = getDownNeighbor( p );
                    break;
                }
                case 4: {
                    neighbor = getDownRightNeighbor( p );
                    break;
                }
                case 5: {
                    neighbor = getLeftNeighbor( p );
                    break;
                }
                case 6: {
                    neighbor = getDownLeftNeighbor( p );
                    break;
                }
                case 7: {
                    neighbor = getUpLeftNeighbor( p );
                    break;
                }
            }
            if ( neighbor.x >= 0 &&
                    neighbours.contains( neighbor.x + neighbor.y * img.width() ) &&
                    points.contains( neighbor ) )
                break;
            i++;
        }
        if ( i == 8 ) {
            return new Point( -1, -1 );
        }

        return neighbor;
    }

    private static Point getUpLeftNeighbor( Point p ) {
        return new Point( p.x - 1, p.y - 1 );
    }

    private static Point getUpNeighbor( Point p ) {
        return new Point( p.x, p.y - 1 );
    }

    private static Point getUpRightNeighbor( Point p ) {
        return new Point( p.x + 1, p.y - 1 );
    }

    private static Point getRightNeighbor( Point p ) {
        return new Point( p.x + 1, p.y );
    }

    private static Point getDownRightNeighbor( Point p ) {
        return new Point( p.x + 1, p.y + 1 );
    }

    private static Point getDownNeighbor( Point p ) {
        return new Point( p.x, p.y + 1 );
    }

    private static Point getDownLeftNeighbor( Point p ) {
        return new Point( p.x - 1, p.y + 1 );
    }

    private static Point getLeftNeighbor( Point p ) {
        return new Point( p.x - 1, p.y );
    }

}
