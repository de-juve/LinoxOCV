package plugins;

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
    public Mat getResult(boolean addToStack) {
        if (result == null) {
            if (addToStack) {
                //DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("laplasian", 0, 100);


        Mat src_gray = new Mat(), lap = new Mat();

        int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;


        Imgproc.GaussianBlur(image, image, new Size(3, 3), 0, 0, Imgproc.BORDER_DEFAULT);

        if(image.channels() == 3 || image.channels() == 4) {
            Imgproc.cvtColor(image, src_gray,  Imgproc.COLOR_RGB2GRAY );
        } else {
            src_gray = image;
        }

        Imgproc.Laplacian(src_gray, lap, ddepth, kernel_size, scale, delta, Imgproc.BORDER_DEFAULT);
        convertScaleAbs( lap, result );

        DataCollector.INSTANCE.setLaplasiantImg(result.clone());

        Linox.getInstance().getStatusBar().setProgress("laplasian", 100, 100);
    }
}
