package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class WatershedPlugin extends AbstractPlugin {
    public WatershedPlugin() {
        title = "Watershed";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Watershed", 0, 100);
        Mat src_gray = new Mat(), thrs = new Mat();

        if(image.channels() == 3 || image.channels() == 4) {
            Imgproc.cvtColor(image, src_gray,  Imgproc.COLOR_RGB2GRAY );
        } else {
            src_gray = image;
        }
        Imgproc.threshold(src_gray, thrs, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        Imgproc.watershed(image, result);

        Linox.getInstance().getStatusBar().setProgress("Watershed", 100, 100);

        if(pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
