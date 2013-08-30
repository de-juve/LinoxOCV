package plugins;

import entities.DataCollector;
import entities.Line;
import entities.PointMentor;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class HoughTransformPlugin extends AbstractPlugin {
    ParameterSlider thresSlider, minLengthSlider, maxGapSlider;

    public HoughTransformPlugin() {
        title = "Hough Transform";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        showParamsPanel( "Choose params" );
        if ( exit ) {
            return;
        }
    }

    public static Mat transform( Mat image, int thres, int minLength, int maxGap ) {
        Mat result = new Mat();
        Mat lines = new Mat();

        image.copyTo( result );
        Core.extractChannel( result, result, 0 );

        Imgproc.HoughLinesP( result, lines, 1, Math.PI / 180, thres, minLength, maxGap );

        ArrayList<Mat> channels = new ArrayList<>();
        channels.add( result );
        channels.add( result );
        channels.add( result );

        Core.merge( channels, result );
        Mat img = Mat.zeros( result.size(), result.type() );

        for ( int x = 0; x < lines.cols(); x++ ) {
            double[] vec = lines.get( 0, x );

           /* double rho = vec[0], theta = vec[1];
            Point start = new Point(), end = new Point();
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
            start.x = Math.round( x0 + 1000*(-b));
            start.y = Math.round(y0 + 1000*(a));
            end.x = Math.round(x0 - 1000*(-b));
            end.y = Math.round(y0 - 1000*(a));
*/
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point( x1, y1 );
            Point end = new Point( x2, y2 );

            Core.line( result, start, end, new Scalar( 0, 75, 255 ), 1 );
            Core.line( img, start, end, new Scalar( 255, 255, 255 ), 1 );
        }
        DataCollector.INSTANCE.addtoHistory( "img", img );

        ArrayList<entities.Point> points = PointMentor.extractPoints( img );
        ArrayList<entities.Point> exPoints = PointMentor.extractExtreamPoints( img, points );
        DataCollector.INSTANCE.setLines( PointMentor.linkPoints( img, points, exPoints ) );
        for ( entities.Point p : points ) {
            double[] color = new double[]{ 0, 255, 0 };
            img.put( p.y, p.x, color );
        }
        DataCollector.INSTANCE.addtoHistory( "img2", img );
        for ( entities.Point p : exPoints ) {
            double[] color = new double[]{ 0, 255, 255 };
            img.put( p.y, p.x, color );
        }
        DataCollector.INSTANCE.addtoHistory( "img3", img );
        for ( Line l : DataCollector.INSTANCE.getLines() ) {
            double[] color = new double[]{ 0, 0, 255 };
            for ( entities.Point p : l.points ) {
                img.put( p.y, p.x, color );
            }
        }
        DataCollector.INSTANCE.addtoHistory( "img4", img );

        return result;
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        int thres = panel.getValueSlider( thresSlider );
        int minLength = panel.getValueSlider( minLengthSlider );
        int maxGap = panel.getValueSlider( maxGapSlider );
        result = transform( image, thres, minLength, maxGap );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        thresSlider = new ParameterSlider( "Thres :", 1, 100, 50 );
        minLengthSlider = new ParameterSlider( "Min line length :", 1, 100, 50 );
        maxGapSlider = new ParameterSlider( "Max gap line :", 1, 100, 10 );

        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( thresSlider );
        panel.addParameterSlider( minLengthSlider );
        panel.addParameterSlider( maxGapSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }
}
