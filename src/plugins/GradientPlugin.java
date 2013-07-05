package plugins;

import entities.DataCollector;
import gui.Linox;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.*;

public class GradientPlugin extends AbstractPlugin {
    public GradientPlugin() {
        title = "Gradient";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("gradient", 0, 100);

        Mat src_gray;
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

        Imgproc.GaussianBlur(image, image, new Size(3, 3), 0, 0, Imgproc.BORDER_DEFAULT);

        src_gray = GrayscalePlugin.run(image, false);

        /// Gradient X
        //Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, BORDER_DEFAULT );
        Imgproc.Sobel(src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, Imgproc.BORDER_DEFAULT);
        convertScaleAbs(grad_x, abs_grad_x);

        /// Gradient Y
        //Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, BORDER_DEFAULT );
        Imgproc.Sobel(src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, Imgproc.BORDER_DEFAULT);
        convertScaleAbs( grad_y, abs_grad_y );

        /// Total Gradient (approximate)
        addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, result );

        DataCollector.INSTANCE.setGradientImg(result.clone());

        Linox.getInstance().getStatusBar().setProgress("gradient", 100, 100);

        if(pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
