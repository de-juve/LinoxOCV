package plugins;

import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CannyPlugin extends AbstractPlugin {
    ParameterSlider thresSlider, ratioSlider;
    private int lowThreshold;
    private int max_lowThreshold = 100;
    private int ratio;
    private int kernel_size = 3;

    public CannyPlugin() {
        title = "Canny";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);

        showParamsPanel("Choose params");
        if (exit) {
            return;
        }
    }

    public Mat canny(Mat image, int lowThreshold, int ratio, int kernel_size) {
        Mat detected_edges = new Mat(image.size(), image.type());
        Mat src_gray = new Mat(image.size(), image.type());

        if ((image.channels() == 3 || image.channels() == 4) && image.type() != CvType.CV_8UC1) {
            Imgproc.cvtColor(image, src_gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            src_gray = image.clone();
        }

        Imgproc.blur(src_gray, detected_edges, new Size(3, 3));

        Imgproc.Canny(detected_edges, detected_edges, lowThreshold, lowThreshold * ratio, kernel_size, false);

        return detected_edges;
    }

    @Override
    public void getParams(ParameterJPanel panel) {
        lowThreshold = panel.getValueSlider(thresSlider);
        ratio = panel.getValueSlider(ratioSlider);

        result = canny(image, lowThreshold, ratio, kernel_size);

        if (tabs == 0) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel(String name) {
        thresSlider = new ParameterSlider("Min Threshold:", 1, max_lowThreshold, 50);
        ratioSlider = new ParameterSlider("Upper:lower ratio:", 2, 4, 3);

        ParameterJPanel panel = new ParameterJPanel(name, this);
        panel.addParameterSlider(thresSlider);
        panel.addParameterSlider(ratioSlider);

        Linox.getInstance().addParameterJPanel(panel);
    }
}
