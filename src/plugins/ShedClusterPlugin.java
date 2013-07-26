package plugins;

import entities.*;
import entities.Point;
import gui.Linox;
import org.opencv.core.Mat;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ShedClusterPlugin extends AbstractPlugin {
    private boolean[] analyzed;
    private int[] labels;
    int r, g, b;

    public ShedClusterPlugin() {
        title = "Shed cluster";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Shed cluster", 0, 100);
        ShedCollector.INSTANCE.clear();
        Mat gray = GrayscalePlugin.run(image, false);

        Random rand = new Random();
        analyzed = new boolean[image.width() * image.height()];
        labels = new int[analyzed.length];
        Queue<Point> queue = new LinkedList<>();

        for (int row = 0; row < image.height(); row++) {
            for (int col = 0; col < image.width(); col++) {
                if (analyzed[id(col, row)]) {
                    continue;
                }

                Shed shed = new Shed(id(col, row), new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                shed.addPoint(new Point(col, row));
                labels[id(col, row)] = id(col, row);
                int lum = (int) gray.get(row, col)[0];
                analyzed[id(col, row)] = true;
                ArrayList<Point> neighbors = PixelsMentor.getNeighborhoodOfPixel(col, row, image, 1);

                boolean[] inQueue = new boolean[(int) image.total()];

                for (Point p : neighbors) {
                    if (!analyzed[id(p.x, p.y)]) {
                        queue.add(p);
                        inQueue[id(p.x, p.y)] = true;
                    }
                }


                while (!queue.isEmpty()) {
                    Point p = queue.remove();
                    if (!analyzed[id(p.x, p.y)]) {
                        int lumn = (int) gray.get(p.y, p.x)[0];
                        if (lumn == lum) {
                            shed.addPoint(p);
                            labels[id(p.x, p.y)] = labels[id(col, row)];
                            analyzed[id(p.x, p.y)] = true;
                            neighbors.clear();
                            neighbors = PixelsMentor.getNeighborhoodOfPixel(p.x, p.y, image, 1);
                            for (Point n : neighbors) {
                                if (!analyzed[id(n.x, n.y)] && !inQueue[id(n.x, n.y)]) {
                                    queue.add(n);
                                    inQueue[id(n.x, n.y)] = true;
                                }
                            }
                        }
                    }
                }

                ShedCollector.INSTANCE.addShed(shed);
            }
        }
        DataCollector.INSTANCE.setShedLabels(labels);
        result = ShedPainterPlugin.run(image);

        Linox.getInstance().getStatusBar().setProgress("Shed cluster", 100, 100);

        if (pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

    private void nextColor() {
        if (b == 255) {
            if (g == 255) {
                r++;
                g = 0;
                b = 0;
            } else {
                g++;
                b = 0;
            }
        } else {
            b++;
        }
    }
}
