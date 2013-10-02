package plugins.roadNetwork;

import entities.Ball;
import entities.DataCollector;
import entities.Point;
import gui.Linox;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import plugins.AbstractPlugin;

import java.util.ArrayList;

public class GraphBuilder extends AbstractPlugin {
    int[] shedlabels;
    boolean[] intersectionPoints;
    ArrayList<Point> points, interPoints, watershedPoints;
    // Mat watershedImg;

    public GraphBuilder() {
        title = "Graph builder";

    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        Core.extractChannel( image, image, 0 );
        shedlabels = DataCollector.INSTANCE.getShedLabels();
        watershedPoints = DataCollector.INSTANCE.getWatershedPoints();
        Mat wimg = DataCollector.INSTANCE.getWatershedImg();
        Ball ball = new Ball();
        for ( Point point : watershedPoints ) {
            Rect rect = new Rect( Math.max( 0, point.x - 1 ), Math.max( 0, point.y - 1 ), Math.min( 3, image.width() - point.x ), Math.min( 3, image.height() - point.y ) );
            Mat subimg = image.submat( rect );
            ball.ball = subimg.mul( ball.mask );
            System.out.println( ball.response() );
        }



       /* points = new ArrayList<>();
        for ( int row = 0; row < image.rows(); row++ ) {
            for ( int col = 0; col < image.cols(); col++ ) {
                if(image.get(row, col)[0] == 255) {
                    points.add( new Point( col, row ) );
                }
            }
        }





      //  watershedImg = DataCollector.INSTANCE.getWatershedImg();
//        DataCollection.INSTANCE.newNodeLabels(length);

//        used = new boolean[length];
        intersectionPoints = new boolean[shedlabels.length];
        interPoints = new ArrayList<>();

        findIntersectionPoints();
        result = image.clone();
        byte[] buff = new byte[( int ) image.total() * image.channels()];

        int j = 0;
        for ( int i = 0; i < ( int ) image.total(); i++ ) {
            for ( int k = 0; k < image.channels(); k++ ) {
                if(intersectionPoints[i]) {
                    if(k < 2) {
                        buff[j] = (byte) 0;
                    } else {
                        buff[j] = (byte) 255;
                    }
                } else {
                    buff[j] = (byte)image.get(y(i), x(i))[k];
                }
                j++;
            }
        }

        result.put( 0, 0, buff );
        DataCollector.INSTANCE.addtoHistory( "inter points", result );
       // createNodes();
//        correctNodes();
//        createEdges();*/
        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void findIntersectionPoints() {
        int radius = 10;
        for ( Point wp : points ) {
            Rect rect = new Rect( Math.max( 0, wp.x - radius / 2 ), Math.max( 0, wp.y - radius / 2 ), Math.min( radius, image.width() - wp.x ), Math.min( radius, image.height() - wp.y ) );
            int intersections = countIntersectionsLinesWithRectSide( rect, -1, ( int ) rect.tl().y );
            if ( intersections > 2 ) {
                intersectionPoints[id( wp.x, wp.y )] = true;
                interPoints.add( wp );
                continue;
            }

            intersections += countIntersectionsLinesWithRectSide( rect, -1, ( int ) rect.br().y );
            if ( intersections > 2 ) {
                intersectionPoints[id( wp.x, wp.y )] = true;
                interPoints.add( wp );
                continue;
            }

            intersections += countIntersectionsLinesWithRectSide( rect, ( int ) rect.tl().x, -1 );
            if ( intersections > 2 ) {
                intersectionPoints[id( wp.x, wp.y )] = true;
                interPoints.add( wp );
                continue;
            }

            intersections += countIntersectionsLinesWithRectSide( rect, ( int ) rect.br().x, -1 );
            if ( intersections > 2 ) {
                intersectionPoints[id( wp.x, wp.y )] = true;
                interPoints.add( wp );
                continue;
            }
        }
    }

    private int countIntersectionsLinesWithRectSide( Rect rect, int x, int y ) {
        int intersections = 0;
        boolean end = true;
        if ( x == -1 ) {
            for ( x = ( int ) rect.tl().x; x < ( int ) rect.br().x; x++ ) {
                if ( image.get( y, x )[0] == 255 ) {
                    end = false;
                    intersections++;
                }
                /*while(!end) {
                    if(image.get(y, x)[0] == 0 || x == (int)rect.br().x - 1) {
                        end = true;
                        intersections++;
                        x--;
                    }
                    x++;
                }*/
            }
        } else {
            for ( y = ( int ) rect.tl().y; y < ( int ) rect.br().y - 1; y++ ) {
                if ( image.get( y, x )[0] == 255 ) {
                    end = false;
                    intersections++;
                }
               /* while(!end) {
                    if(image.get(y, x)[0] == 0 || y == (int)rect.br().y - 2) {
                        end = true;
                        intersections++;
                        y--;
                    }
                    y++;
                }*/
            }
        }
        return intersections;
    }

    private void createNodes() {

    }


}
