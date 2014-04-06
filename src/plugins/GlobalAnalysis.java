package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Mask;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import plugins.approximation.Interpolacion;
import plugins.approximation.Optimizer;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GlobalAnalysis extends AbstractPlugin {
    private int area_size = 0, road_w, mask_w, mask_h, threshold = 250, minPoints = 20;
    boolean doWatershed;
    Mat grey, mresult, wresult, regressImage, interpolateImage;
    private HashMap<Integer, Point> wpoints;
    ArrayList<Point> regressPoints, interpolatePoints;
    ArrayList<Line> regressLines, interpolateLines;
    ParameterSlider roadWidth = new ParameterSlider("Road width:", 1, 15, 1);
    ParameterSlider maskWidth = new ParameterSlider("Mask width:", 3, 10, 3);
    ParameterSlider maskHeight = new ParameterSlider("Mask height:", 3, 10, 3);
    ParameterSlider kernelSize = new ParameterSlider("Size of area closing:", 1, 7000, 1000);

    public GlobalAnalysis() {
        title = "Global Analysis";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);
        grey = new Mat();
        //grey = image.clone();

        if ((image.channels() == 3 || image.channels() == 4) && image.type() != CvType.CV_8UC1) {
            Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2Lab);
        }

        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(grey, channels);
        grey = channels.get(0);
        pluginListener.addImageTab("grey", grey);

        showParamsPanel("Choose params");
        if (exit) {
            return;
        }
    }

    @Override
    public void getParams(ParameterJPanel panel) {
        road_w = panel.getValueSlider(roadWidth);
        int _area_size = panel.getValueSlider(kernelSize);
        doWatershed = area_size != _area_size;
        area_size = _area_size;
        mask_w = panel.getValueSlider(maskWidth);
        mask_h = panel.getValueSlider(maskHeight);

        analyse();

        if (tabs == 0) {
            pluginListener.addImageTab();
            tabs++;
        } else {
            pluginListener.replaceImageTab();
        }
    }

    protected void showParamsPanel(String name) {
        ParameterJPanel panel = new ParameterJPanel(name, this);
        panel.addParameterSlider(maskWidth);
        panel.addParameterSlider(maskHeight);
        panel.addParameterSlider(roadWidth);
        panel.addParameterSlider(kernelSize);

        Linox.getInstance().addParameterJPanel(panel);
    }

    private void analyse() {
        wpoints = new HashMap<>();
        if (doWatershed) {
            mresult = new Mat();
            wresult = new Mat();
            MorphologyPlugin morphology = new MorphologyPlugin();
            WatershedPlugin watershed = new WatershedPlugin();

            morphology.initImage(grey);
            morphology.run("Closing", area_size);
            mresult = morphology.getResult(true);

            watershed.initImage(mresult);
            watershed.run();
            wresult = watershed.getResult(true);

            HashMap<Integer, Point> watershedPoints = DataCollector.INSTANCE.getWatershedPoints();
            LineCreator lineCreator = new LineCreator(DataCollector.INSTANCE.getWatershedImg(), new ArrayList<>(watershedPoints.values()));
            lineCreator.extractEdgePoints();
            lineCreator.createLines();

            ArrayList<Line> lines = new ArrayList<>(lineCreator.lines);
            ArrayList<Point> epoints = new ArrayList<>(lineCreator.edgePoints);

            // lines = (ArrayList<Line>) lineCreator.lines.clone();
            // epoints = (ArrayList<Point>) lineCreator.edgePoints.clone();
            regression(lines, epoints);

            interpolate(regressLines, regressPoints);

            RegressionPointsAnalysis analysis = new RegressionPointsAnalysis(interpolateImage, interpolateLines);
        }


        Mask mask = new Mask(mask_w, mask_h, road_w, wresult.type(), threshold);
        L:
        for (int y = 0; y < wresult.rows(); y++) {
            if (y + mask_w > wresult.rows()) {
                break;
            }
            for (int x = 0; x < wresult.cols(); x++) {
                if (x + mask_h > wresult.cols()) {
                    break;
                }
                mask.fill(new Point(x, y), wresult);
                for (Mask.MaskType type : Mask.MaskType.values()) {
                    if (mask.analyze(type)) {
                        int id = x + y * wresult.cols();
                        wpoints.put(id, new Point(x, y));
                        // addWPoints(x, y, DataCollector.INSTANCE.getWatershedPoints(), wImage.cols());
                    }
                }
                if (mask.analyzeNew()) {
                    for (Point point : mask.getRoad()) {
                        int col = x + point.x;
                        int row = y + point.y;
                        int id = col + row * wresult.cols();
                        wpoints.put(id, new Point(col, row));
                    }
                }
                // break L;
            }
        }
        result = image.clone();
        drawWatershed(result);
    }

    private void addWPoints(int x, int y, HashMap<Integer, Point> watershedPoints, int cols) {
        for (Point point : watershedPoints.values()) {
            int col = x + point.x;
            int row = y + point.y;
            int id = col + row * cols;
            wpoints.put(id, new Point(col, row));
        }
    }

    private void drawWatershed(Mat image) {
        for (Point point : wpoints.values()) {
            image.put(point.y, point.x, 0, 0, 255);
        }
    }

    private void interpolate(ArrayList<Line> lines, ArrayList<Point> epoints) {
        double b, g, r;
        b = g = r = 0;
        Random rand = new Random();

        Interpolacion interpolacion = new Interpolacion();
        interpolateImage = Mat.zeros(image.size(), image.type());
        interpolateLines = new ArrayList<>();
        interpolatePoints = new ArrayList<>();

        int lineLabel = 0;
        for (Line line : lines) {
            if (line.points.size() < minPoints)
                continue;

            Line x = new Line();
            Line y = new Line();
            int i = 0;
            for (Point p : line.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }

            interpolacion.extractPointsFormLine(x);
            Line lix = interpolacion.interpolate();
            interpolacion.extractPointsFormLine(y);
            Line liy = interpolacion.interpolate();

            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            Line iLine = new Line();
            iLine.label = lineLabel;
/*
            for (Point p : line.points) {
                int id = line.points.indexOf(p);
                p.x = lix.points.get(id).y;
                p.y = liy.points.get(id).y;

                Point iPoint = new Point(p);
                regressPoints.add(iPoint);
                iLine.add(iPoint);

                im_inter.put(p.y, p.x, mcolor);
            }*/
            for (i = 0; i < lix.points.size(); i++) {
                Point iPoint = new Point(lix.points.get(i).y, liy.points.get(i).y);

                if (!interpolatePoints.contains(iPoint)) {
                    iPoint.curv[0] = lix.points.get(i).curvature;
                    iPoint.curv[1] = liy.points.get(i).curvature;
                    iPoint.lineLabel = lineLabel;

                    interpolatePoints.add(iPoint);
                    iLine.add(iPoint);

                    interpolateImage.put(iPoint.y, iPoint.x, mcolor);
                }
            }
            interpolateLines.add(lineLabel, iLine);
            lineLabel++;
        }

        /*for (Point p : epoints) {
            if (p.isCrossroad) {
                im_inter.put(p.y, p.x, new double[]{255, 255, 255});
            }
        }*/
        //  DataCollector.INSTANCE.addtoHistory("before interpolate", img);
        DataCollector.INSTANCE.addtoHistory("after interpolate", interpolateImage);
        pluginListener.addImageTab("interpolate", interpolateImage);
    }

    private void regression(ArrayList<Line> lines, ArrayList<Point> epoints) {
        double b, g, r;
        b = g = r = 0;
        Random rand = new Random();

        //OLSSimpleRegression regression = new OLSSimpleRegression();

        Optimizer optimizer = new Optimizer();

        regressLines = new ArrayList<>();
        regressPoints = new ArrayList<>();
        regressImage = Mat.zeros(image.size(), image.type());


        for (Line line : lines) {
            if (line.points.size() < minPoints)
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

            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            Line rLine = new Line();

            for (Point p : line.points) {
                int id = line.points.indexOf(p);
                p.x = lrx.points.get(id).y;
                p.y = lry.points.get(id).y;

                Point rPoint = new Point(p);
                regressPoints.add(rPoint);
                rLine.add(rPoint);

                regressImage.put(p.y, p.x, mcolor);
            }
            regressLines.add(rLine);
        }
/*
        for (Point p : epoints) {
            if (p.isCrossroad) {
                im_regr.put(p.y, p.x, new double[]{255, 255, 255});
            }
        }*/
        DataCollector.INSTANCE.addtoHistory("after regression", regressImage);
        pluginListener.addImageTab("regression", regressImage);
    }

}
