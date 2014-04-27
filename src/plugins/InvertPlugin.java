package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class InvertPlugin extends AbstractPlugin {
    public InvertPlugin() {
        title = "Invert";
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
        AbstractPlugin.printMessage("Invert image");
        Core.bitwise_not(image, result);

        if ( addToCollector ) {
            DataCollector.INSTANCE.addtoHistory("Invert img", result.clone());
        }
        return result;
    }
}
