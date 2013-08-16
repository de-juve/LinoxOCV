package plugins;

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
    ParameterSlider thresSlider;

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

    public static Mat transform( Mat image, int thres ) {
        Mat result = new Mat();
        Mat lines = new Mat();
       /* Imgproc.Canny( image, result, 50, 200 );
        DataCollector.INSTANCE.addtoHistory( "canny", result );*/
        image.copyTo( result );
        Core.extractChannel( result, result, 0 );

        Imgproc.HoughLinesP( result, lines, 1, Math.PI / 180, thres, 50, 10 );

        ArrayList<Mat> channels = new ArrayList<>();
        channels.add( result );
        channels.add( result );
        channels.add( result );

        Core.merge( channels, result );

        System.out.println( lines.width() );
        System.out.println( lines.height() );
        //System.out.println(lines.width());
        for ( int x = 0; x < lines.cols(); x++ ) {
            double[] vec = lines.get( 0, x );


            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point( x1, y1 );
            Point end = new Point( x2, y2 );

            Core.line( result, start, end, new Scalar( 255, 75, 75 ), 1 );
        }
        return result;
    }

    @Override
    public void getParams( ParameterJPanel panel ) {
        int thres = panel.getValueSlider( thresSlider );

        result = transform( image, thres );

        if ( tabs == 0 ) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel( String name ) {
        thresSlider = new ParameterSlider( "Thres :", 1, 100, 50 );
        ParameterJPanel panel = new ParameterJPanel( name, this );
        panel.addParameterSlider( thresSlider );

        Linox.getInstance().addParameterJPanel( panel );
    }
}
