package plugins;

import entities.DataCollector;
import entities.HistogramCounter;
import entities.PixelsMentor;
import gui.Linox;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class MedianPlugin extends AbstractPlugin {
    int[] values;

    public MedianPlugin() {
        title = "Median";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Median", 0, 100);

        GrayscalePlugin.run(image, true);
        Mat gray = DataCollector.INSTANCE.getGrayImg();
        values = new int[(int) image.total()];

        HistogramCounter hist = new HistogramCounter();

        for (int i = 0; i < (int) image.total(); i++) {
            ArrayList<Integer> neigh = PixelsMentor.getNeighborsOfPixel(x(i), y(i), image, 1);
            Integer[] arr = new Integer[neigh.size()];
            int j = 0;
            for (Integer n : neigh) {
                arr[j] = (int) gray.get(y(n), x(n))[0];
                j++;
            }
            hist.count(arr);
            values[i] = (int) hist.getMedian();
        }

        result = new Mat(image.rows(), image.cols(), image.type());
        byte[] buff = new byte[(int) image.total() * image.channels()];

        int j = 0;
        for (int i = 0; i < values.length; i++) {
            for (int k = 0; k < image.channels(); k++) {
                buff[j] = (byte) values[i];
                j++;
            }
        }
        result.put(0, 0, buff);

        if (pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
