package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Random;

public class RegressionPointsAnalysis {
    private final int THRESHOLD = 20;

    public RegressionPointsAnalysis(Mat image, ArrayList<Line> lines) {
        for (int i = 0; i < lines.size(); i++) {
            Line line1 = lines.get(i);
            ArrayList<Point> borderPoints1 = line1.getBorderPoints();
            L:
            for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);
                ArrayList<Point> borderPoints2 = line2.getBorderPoints();
                for (Point point1 : borderPoints1) {
                    for (Point point2 : borderPoints2) {
                        if (Math.abs(point1.x - point2.x) < THRESHOLD &&
                                Math.abs(point1.y - point2.y) < THRESHOLD) {
                            line1.addConnection(line2);
                            // line2.addConnection(line1);
                            continue L;
                        }
                    }
                }
            }
        }

        for (Line line : lines) {
            ArrayList<Line> conLines = line.getConnection();
            for (Line cLine : conLines) {

            }
        }

        Mat im_ = Mat.zeros(image.size(), image.type());
        Random rand = new Random();
        double b, g, r;
        b = g = r = 0;
        for (Line line : lines) {
            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            if (line.points.size() > 1) {
                for (Point p : line.points) {
                    im_.put(p.y, p.x, mcolor);
                }
            }
            //line.extractBorderPoints();
        }
        DataCollector.INSTANCE.addtoHistory("after regr", im_);


    }
}
