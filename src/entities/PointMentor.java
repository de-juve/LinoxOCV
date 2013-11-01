package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;

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
}
