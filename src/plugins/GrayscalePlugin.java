package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class GrayscalePlugin extends AbstractPlugin {
    public GrayscalePlugin() {
        title = "Grayscale";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );

        result = run( image, true );

        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    public static Mat run( Mat image, boolean addToCollector ) {


        Mat result = new Mat();

        if ( ( image.channels() == 3 || image.channels() == 4 ) && image.type() != CvType.CV_8UC1 ) {
            AbstractPlugin.printMessage("Convert image to Gray");
            Imgproc.cvtColor( image, result, Imgproc.COLOR_BGR2Lab );        //COLOR_BGR2GRAY
            ArrayList<Mat> channels = new ArrayList<Mat>();
            Core.split(result, channels);
            result = channels.get(0);
        } else {
            result = image;
        }

        if ( addToCollector ) {
            DataCollector.INSTANCE.setGrayImg( result.clone() );
        }
        return result;
    }
}
