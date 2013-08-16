package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class WatershedSegmenterPlugin extends AbstractPlugin {
    private Mat markers;

    public WatershedSegmenterPlugin() {
        title = "Watershed segmenter";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        Mat binary;// = new Mat( image.size(), image.type() );


        binary = GrayscalePlugin.run( image, false );
        Imgproc.threshold( binary, binary, 150, 255, Imgproc.THRESH_BINARY );

        DataCollector.INSTANCE.addtoHistory( "threshold binary", binary );

       /* // Eliminate noise and smaller objects
        Mat fg = new Mat( image.size(), image.type() );
        Imgproc.erode( binary, fg, new Mat(), new Point( -1, -1 ), 1 );

        DataCollector.INSTANCE.addtoHistory( "eliminate", fg );*/

        // Identify image pixels without objects
        Mat bg = new Mat( image.size(), image.type() );
        Imgproc.dilate( binary, bg, new Mat(), new Point( -1, -1 ), 3 );
        Imgproc.threshold( bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV );

        DataCollector.INSTANCE.addtoHistory( "dilate", bg );

        // Create markers image
        markers = new Mat( binary.size(), CvType.CV_8U, new Scalar( 0 ) );
        Core.add( binary, bg, markers );

        DataCollector.INSTANCE.addtoHistory( "markers", markers );

        // Create watershed segmentation object
        markers.convertTo( markers, CvType.CV_32S );

      /*  System.out.println(CvType.typeToString( markers.type() ));
        System.out.println(CvType.typeToString( image.type() ));*/


        result = process( image );
        result.convertTo( result, CvType.CV_8U );

        ArrayList<Mat> channels = new ArrayList<>();
        channels.add( result );
        channels.add( result );
        channels.add( result );

        Core.merge( channels, result );
       /* MorphologyPlugin mp = new MorphologyPlugin();
        mp.initImage( result );
        mp.run( "Closing", 1 );
        result = mp.result;
*/

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    public Mat process( Mat image ) {
        Imgproc.watershed( image, markers );
        markers.convertTo( markers, CvType.CV_8U );
        return markers;
    }


}
