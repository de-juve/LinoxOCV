package plugins;

import entities.DataCollector;
import entities.Point;
import gui.Linox;
import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.Queue;

public class LowerCompletePlugin extends AbstractPlugin {
    Mat gray;
    Queue<Point> queue = new LinkedList<>();
    int[] level;
    int distination;

    public LowerCompletePlugin() {
        title = "Lower complete";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("Lower complete", 0, 100);

        gray =   GrayscalePlugin.run(image, false);

        InitQueue();

        distination = 1;
        queue.add(new Point(-1,-1));
        while (!queue.isEmpty()) {
            Point point = queue.remove();
            if (point.x == -1 && queue.size() > 0) {
                queue.add(new Point(-1,-1));
                distination++;
            } else if (point.x > -1) {
                level[id(point.x, point.y)] = distination;
                int lum =  (int)gray.get(point.y, point.x)[0];
                for(int j = Math.max(0, point.y-1); j <= Math.max(image.height(), point.y+1); j++) {
                    for(int i = Math.max(0, point.x-1); i <= Math.max(image.width(), point.x+1); i++) {
                        int nlum = (int)gray.get(j, i)[0];
                        if(point.x != i && point.y != j && lum == nlum && level[i+j*image.width()] == 0) {
                            level[id(i, j)] = -1;
                            queue.add(new Point(i, j));
                        }
                    }
                }
            }
        }

        DataCollector.INSTANCE.setLowerCompletion(level);
        Linox.getInstance().getStatusBar().setProgress("Lower complete", 100, 100);

        if(pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }

    }

    private void InitQueue() {
        for(int row = 0; row < image.height(); row++) {
            for(int col = 0; col < image.width(); col++) {
                int lum =  (int)gray.get(row, col)[0];
                level[id(col, row)] = 0;
                N : {
                    for(int j = Math.max(0, row-1); j <= Math.max(image.height(), row+1); j++) {
                        for(int i = Math.max(0, col-1); i <= Math.max(image.width(), col+1); i++) {
                            int nlum = (int)gray.get(j, i)[0];
                            if(col != i && row != j && lum < nlum) {
                                level[id(col, row)] = -1;
                                queue.add(new Point(col, row));
                                break N;

                            }
                        }
                    }
                }
            }
        }
    }
}
