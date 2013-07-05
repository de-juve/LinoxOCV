package plugins;

import entities.PixelsMentor;
import entities.Point;
import entities.Shed;
import gui.Linox;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ShedClusterPlugin extends AbstractPlugin {
    private boolean[] analyzed;

    public ShedClusterPlugin() {
        title = "Shed cluster";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Shed cluster", 0, 100);

        Mat gray = GrayscalePlugin.run(image, false);
        analyzed = new boolean[image.width()*image.height()];

        for(int row = 0; row < image.height(); row++) {
            for(int col = 0; col < image.width(); col++) {
                if(analyzed[id(col,row)]) {
                    continue;
                }
                Shed shed = new Shed(id(col, row));
                shed.addPoint(new Point(col, row));
                int lum =  (int)gray.get(row, col)[0];
                analyzed[id(col, row)] = true;
                ArrayList<Point> neighbors = PixelsMentor.getNeighborhoodOfPixel(col, row, image, 1);
                Queue<Point> queue = new LinkedList<>();
                queue.addAll(neighbors);
                while(!queue.isEmpty()) {
                    Point point = queue.remove();
                    if(analyzed[id(point.x, point.y)]) {
                        continue;
                    }
                    int lumn = (int)gray.get(point.y, point.x)[0];
                    if(lumn == lum) {
                        shed.addPoint(point);
                        analyzed[id(point.x, point.y)] = true;
                        queue.addAll(PixelsMentor.getNeighborhoodOfPixel(point.x, point.y, image, 1));
                    }
                }
            }
        }


        Linox.getInstance().getStatusBar().setProgress("Shed cluster", 100, 100);

        if(pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }
}
