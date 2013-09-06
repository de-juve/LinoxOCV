package plugins.roadNetwork;

import entities.DataCollector;
import entities.Direction;
import entities.Line;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import plugins.AbstractPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Builder extends AbstractPlugin {
    ParameterSlider thresSlider, ratioSlider, maxRoadWidthSlider;
    private int lowThreshold;
    private int max_lowThreshold = 100;
    private int ratio;
    private int kernel_size = 3;
    private int maxRoadWidth;

    public Builder() {
        title = "Road builder";
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

        Direction[] directions = new Direction[]{ Direction.NORTH, Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH_WEST };
        ArrayList<Line> correctLines = new ArrayList<>();

        Loop:
        for ( Line line : lines ) {
            Line correctLine = new Line();

            Iterator<Point> iterator = line.points.iterator();
            while ( iterator.hasNext() ) {
                Point p = iterator.next();

                HashMap<Direction, Double> directionsMap = new HashMap<>();

                //Определяем лучшее направление до края дороги
                for ( Direction d : directions ) {
                    double w1 = letRay( p, d, detected_edges );
                    double w2 = letRay( p, d.opposite(), detected_edges );
                    if ( w1 >= maxRoadWidth || w2 >= maxRoadWidth ) {
                        directionsMap.put( d, maxRoadWidth * 2d );
                    } else {
                        directionsMap.put( d, Math.abs( w1 - w2 ) );
                    }
                }

                Direction bestDirection = Direction.NORTH;
                for ( Direction d : directions ) {
                    double w = directionsMap.get( d );
                    if ( w < directionsMap.get( bestDirection ) ) {
                        bestDirection = d;
                    }
                }
                p.direction = bestDirection;

                //Центрируем точку
                if ( directionsMap.get( bestDirection ) > 1 ) {
                    double w1 = letRay( p, bestDirection, detected_edges );
                    double w2 = letRay( p, bestDirection.opposite(), detected_edges );
                    if ( w1 >= maxRoadWidth || w2 >= maxRoadWidth ) {
                        continue Loop;
                    }
                    if ( w1 < w2 ) {
                        while ( w1 < w2 ) {
                            p = bestDirection.opposite().getNeighboure( p );
                            p.direction = bestDirection;
                            w1++;
                            w2--;
                        }
                    } else if ( w1 > w2 ) {
                        while ( w1 > w2 ) {
                            p = bestDirection.getNeighboure( p );
                            p.direction = bestDirection;
                            w1--;
                            w2++;
                        }
                    }
                }
                correctLine.add( p );
                double[] color = new double[]{ 0, 0, 255 };
                canny.put( p.y, p.x, color );
            }

            //Вычислить общее направлени прямой
            iterator = correctLine.points.iterator();
            Point current = iterator.next();
            while ( iterator.hasNext() ) {
                Point next = iterator.next();
                Direction dir = Direction.defineDirection( current, next, image.width() );
                current.direction = dir;
                next.direction = dir;
                current = next;
            }


            //Продолжить линию
            boolean can = true;
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

            }

            DataCollector.INSTANCE.addtoHistory( "img", canny );
            correctLines.add( correctLine );
        }
        return canny;
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
