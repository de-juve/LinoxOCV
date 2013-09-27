package plugins.roadNetwork;

import entities.DataCollector;
import entities.Direction;
import entities.Line;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import plugins.AbstractPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Builder extends AbstractPlugin {
    ParameterSlider thresSlider, ratioSlider, maxRoadWidthSlider;
    private int lowThreshold;
    private int max_lowThreshold = 100;
    private int ratio;
    private int kernel_size = 3;
    private int maxRoadWidth;
    ArrayList<Direction> diagonalDirections;


    public Builder() {
        title = "Road builder";
        diagonalDirections = new ArrayList<>();
        diagonalDirections.add( Direction.NORTH_EAST );
        diagonalDirections.add( Direction.NORTH_WEST );
        diagonalDirections.add( Direction.SOUTH_EAST );
        diagonalDirections.add( Direction.SOUTH_WEST );
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public Mat build( ArrayList<Line> lines ) {
        Mat canny = Mat.zeros( image.size(), image.type() );
        Mat detected_edges = new Mat( image.size(), image.type() );
        Mat src_gray = new Mat( image.size(), image.type() );
        Imgproc.cvtColor( image, src_gray, Imgproc.COLOR_BGR2GRAY );
        Imgproc.blur( src_gray, detected_edges, new Size( 3, 3 ) );
        Imgproc.Canny( detected_edges, detected_edges, lowThreshold, lowThreshold * ratio, kernel_size, false );
        image.copyTo( canny, detected_edges );
        DataCollector.INSTANCE.addtoHistory( "edges", detected_edges );
        DataCollector.INSTANCE.addtoHistory( "canny", canny );

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours( detected_edges, contours, hierarchy, 1, 3 );

        Direction[] directions = new Direction[]{ Direction.NORTH, Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH_WEST };
        ArrayList<Line> correctLines = new ArrayList<>();

        Mat contour = Mat.zeros( image.size(), image.type() );
        Random rand = new Random();
        for ( int i = 0; i < contours.size(); i++ ) {
            System.out.println( Imgproc.contourArea( contours.get( i ) ) );
            if ( Imgproc.contourArea( contours.get( i ) ) > 50 ) {
                Scalar color = new Scalar( rand.nextInt( 255 ), rand.nextInt( 255 ), rand.nextInt( 255 ) );
                Imgproc.drawContours( contour, contours, i, color, 1 );
            }
        }
        DataCollector.INSTANCE.addtoHistory( "contour", contour );

        Loop:
        for ( Line line : lines ) {
            Line correctLine = new Line();

            //Определить ширину дороги в каждой точке кривой
            Iterator<Point> iterator = line.points.iterator();
            while ( iterator.hasNext() ) {
                //Вычисляем ширину дороги в точке по всем направлениям
                Direction bestDirection = null;
                int bestWidth = 0;
                double bestWeight = 0;
                int width1 = 0, width2 = 0;

                Point current = iterator.next();
                for ( Direction d : directions ) {
                    double weight1 = letRay( current, d, detected_edges );
                    double weight2 = letRay( current, d.opposite(), detected_edges );
                    double weight = weight1 + weight2;
                    int width = ( int ) ( weight / d.weight() );

                    if ( width >= maxRoadWidth ) {
                        continue;
                    } else {
                        if ( bestDirection == null ) {
                            bestDirection = d;
                            bestWidth = width;
                            bestWeight = weight;
                            width1 = ( int ) ( weight1 / d.weight() );
                            width2 = ( int ) ( weight2 / d.weight() );
                        } else if ( weight < bestWeight ) {
                            bestDirection = d;
                            bestWidth = width;
                            bestWeight = weight;
                            width1 = ( int ) ( weight1 / d.weight() );
                            width2 = ( int ) ( weight2 / d.weight() );
                        }
                    }
                }

                /*//Центрируем точку - может привести к разрывам между точками
                if(!(bestWidth % 2 == 0 && Math.abs(width1 - width2) == 1)) {
                    if(width1 > width2) {
                        while(width1 > width2) {
                            current = bestDirection.getNeighboure(current);
                            width1--;
                            width2++;
                        }
                    } else {
                        while(width2 > width1) {
                            current = bestDirection.opposite().getNeighboure(current);
                            width1++;
                            width2--;
                        }
                    }
                }*/
                current.direction = bestDirection;
                current.width = bestWidth;
                correctLine.add( current );
                double[] color = new double[]{ 0, 0, 255 };
                canny.put( current.y, current.x, color );
            }

            //Вычислить направления в каждой точке кривой (кроме последней)
            iterator = correctLine.points.iterator();
            Point current = iterator.next();
            int avgWidth = current.width;
            System.out.println( "width " + current.width );
            while ( iterator.hasNext() ) {
                Point next = iterator.next();
                System.out.println( "width " + next.width );
                avgWidth += next.width;
                Direction dir = Direction.defineDirection( current, next, image.width() );
                System.out.println( "bdir " + current.direction + " dir " + dir );
                current.direction = dir;
                current = next;
            }
            correctLine.avgWidth = avgWidth / correctLine.points.size();
            System.out.println( "avg width " + correctLine.avgWidth );
            System.out.println();

            //Продолжить линию
            /*boolean can = true;
            while ( can ) {
                Point next = current.direction.getNeighboure( current );
                if ( isInBox( next ) ) {
                    if ( Math.abs( image.get( next.y, next.x )[0] - image.get( current.y, current.x )[0] ) < lowThreshold / 5 ) {
                        next.direction = current.direction;
                        correctLine.add( next );
                        double[] color = new double[]{ 255, 0, 0 };
                        canny.put( next.y, next.x, color );
                        current = next;
                    } else {
                        can = false;
                    }
                }

            }*/

            DataCollector.INSTANCE.addtoHistory( "img", canny );
            correctLines.add( correctLine );
        }
        result = image.clone();
        for ( Line line : correctLines ) {
            for ( Point point : line.points ) {
                double[] color = new double[]{ 0, 0, 255 };
                result.put( point.y, point.x, color );
            }
        }
        return result;
    }


    private double letRay( Point p, Direction direction, Mat canny ) {
        double weight = 0;

        if ( canny.get( p.y, p.x )[0] == 0 ) {
            int width = 1;
            boolean doing = true;
            while ( doing ) {
                Point n = direction.getNeighboure( p );
                if ( n.y < 0 || n.y >= image.height() || n.x < 0 || n.x >= image.width() || width >= maxRoadWidth ) {
                    doing = false;
                } else {
                    if ( diagonalDirections.contains( direction ) ) {
                        Point n1 = direction.collinear1().opposite().getNeighboure( n );
                        Point n2 = direction.collinear2().opposite().getNeighboure( n );
                        if ( n1.y < 0 || n1.y >= image.height() || n1.x < 0 || n1.x >= image.width() || n2.y < 0 || n2.y >= image.height() || n2.x < 0 || n2.x >= image.width() ) {
                            doing = false;
                        } else if ( canny.get( n1.y, n1.x )[0] > 0 && canny.get( n2.y, n2.x )[0] > 0 ) {
                            weight += direction.weight();
                            width++;
                            doing = false;
                        } else {
                            weight += direction.weight();
                            width++;
                            p = n;
                        }
                    } else {
                        if ( canny.get( n.y, n.x )[0] == 0 ) {
                            weight += direction.weight();
                            width++;
                            p = n;
                        } else {
                            weight += direction.weight();
                            width++;
                            doing = false;
                        }
                    }
                }
            }
        }
        return weight;
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        ArrayList<Line> lines = DataCollector.INSTANCE.getLines();
        if ( lines.isEmpty() ) {
            errMessage = "Empty lines!";
            return;
        }
        lowThreshold = panel.getValueSlider( thresSlider );
        ratio = panel.getValueSlider( ratioSlider );
        maxRoadWidth = panel.getValueSlider( maxRoadWidthSlider );
        result = build( lines );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        thresSlider = new ParameterSlider( "Min Threshold:", 1, max_lowThreshold, 50 );
        ratioSlider = new ParameterSlider( "Upper:lower ratio:", 2, 4, 3 );
        maxRoadWidthSlider = new ParameterSlider( "Max road width: ", 1, 100, 30 );

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( thresSlider );
        panel.addParameterSlider( ratioSlider );
        panel.addParameterSlider( maxRoadWidthSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }
}
