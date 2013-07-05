package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.convertScaleAbs;

public class LaplasianPlugin extends AbstractPlugin {
    public LaplasianPlugin() {
        title = "Laplasian";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("laplasian", 0, 100);

        Mat result = new Mat();
        Mat src_gray, lap = new Mat();

        int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

        Imgproc.GaussianBlur(image, image, new Size(3, 3), 0, 0, Imgproc.BORDER_DEFAULT);

        src_gray =   GrayscalePlugin.run(image, false);

        Imgproc.Laplacian(src_gray, lap, ddepth, kernel_size, scale, delta, Imgproc.BORDER_DEFAULT);
        convertScaleAbs( lap, result );

        DataCollector.INSTANCE.setLaplasianImg(result.clone());

        Linox.getInstance().getStatusBar().setProgress("laplasian", 100, 100);

        if(pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
