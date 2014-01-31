package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import gui.Linox;
import org.opencv.core.Mat;
import plugins.approximation.Interpolacion;
import plugins.approximation.Optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TestPlugin extends AbstractPlugin {
    double b, g, r;

    public TestPlugin() {
        title = "Test";
    }

    @Override
    public void run() {


        b = g = r = 0;
        result = Mat.zeros(image.size(), image.type());
        Random rand = new Random();
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);


        HashMap<Integer, Point> wpoints = DataCollector.INSTANCE.getWatershedPoints();
        LineCreator lineCreator = new LineCreator(DataCollector.INSTANCE.getWatershedImg(), new ArrayList<>(wpoints.values()));
        lineCreator.extractEdgePoints();
        lineCreator.createLines();
        ArrayList<Line> lines = lineCreator.lines;
        ArrayList<Point> epoints = lineCreator.edgePoints;

        Interpolacion interpolacion = new Interpolacion();

        //OLSSimpleRegression regression = new OLSSimpleRegression();

        Optimizer optimizer = new Optimizer();

        Mat img = Mat.zeros(image.size(), image.type());
        Mat im_inter = Mat.zeros(image.size(), image.type());
        Mat im_regr = Mat.zeros(image.size(), image.type());

        for (Line line : lines) {
            if (line.points.size() <= 2)
                continue;
            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            for (Point point : line.points) {
                img.put(point.y, point.x, mcolor);
            }
            DataCollector.INSTANCE.addtoHistory("before interpolate", img);
        }

        for (Line line : lines) {
            if (line.points.size() <= 2)
                continue;


            Line x = new Line();
            Line y = new Line();
            int i = 0;
            for (Point p : line.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }

            optimizer.extractPointsFormLine(x);
            Line lrx = optimizer.optimize();

            optimizer.extractPointsFormLine(y);
            Line lry = optimizer.optimize();

            interpolacion.extractPointsFormLine(x);
            Line lix = interpolacion.interpolate();
            interpolacion.extractPointsFormLine(y);
            Line liy = interpolacion.interpolate();

            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            for (Point p : line.points) {
                int id = line.points.indexOf(p);
                p.x = lrx.points.get(id).y;
                p.y = lry.points.get(id).y;

                im_regr.put(p.y, p.x, mcolor);
            }

            for (Point p : line.points) {
                int id = line.points.indexOf(p);
                p.x = lix.points.get(id).y;
                p.y = liy.points.get(id).y;

                im_inter.put(p.y, p.x, mcolor);
            }

            //interpolacion.run();
            //break;
        }

        for (Point p : epoints) {
            if (p.isCrossroad) {
                img.put(p.y, p.x, new double[]{255, 255, 255});
                im_inter.put(p.y, p.x, new double[]{255, 255, 255});
                im_regr.put(p.y, p.x, new double[]{255, 255, 255});
            }
        }
        DataCollector.INSTANCE.addtoHistory("before interpolate", img);
        DataCollector.INSTANCE.addtoHistory("after interpolate", im_inter);
        DataCollector.INSTANCE.addtoHistory("after regression", im_regr);

        result = im_regr;

        Linox.getInstance().getStatusBar().setProgress(title, 100, 100);

        if (pluginListener != null) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

}
