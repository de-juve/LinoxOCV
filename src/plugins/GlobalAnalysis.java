package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import gui.Linox;
import gui.dialog.ParameterJPanel;
import gui.dialog.ParameterSlider;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import plugins.approximation.Interpolacion;
import plugins.approximation.Optimizer;
import plugins.morphology.MorphologyPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GlobalAnalysis extends AbstractPlugin {
    private int area_size = 0, road_w, mask_w, mask_h, threshold = 250, minPoints = 20;
    boolean doWatershed;
    Mat  grey, invert, mresult, wresult, regressImage, interpolateImage;
    private HashMap<Integer, Point> wpoints;
    ArrayList<Point> regressPoints, interpolatePoints;
    ArrayList<Line> regressLines, interpolateLines;
    ParameterSlider roadWidth = new ParameterSlider("Road width:", 1, 15, 1);
    ParameterSlider maskWidth = new ParameterSlider("Mask width:", 3, 10, 3);
    ParameterSlider maskHeight = new ParameterSlider("Mask height:", 3, 10, 3);
    ParameterSlider kernelSize = new ParameterSlider("Size of area closing:", 1, 5000, 1000);

    public GlobalAnalysis() {
        title = "Global Analysis";
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress(title, 0, 100);
        grey = new Mat();
        //grey = image.clone();
        grey = GrayscalePlugin.run(image, true);
        invert = InvertPlugin.run(grey, true);

        /*if ((image.channels() == 3 || image.channels() == 4) && image.type() != CvType.CV_8UC1) {
            Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2Lab);
        }
        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(grey, channels);
        grey = channels.get(0);*/

        pluginListener.addImageTab("grey", grey);
        pluginListener.addImageTab("invert", invert);

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
            wresult = doWatershed(grey);
            pluginListener.addImageTab("watershed grey", wresult);
            HashMap<Integer, Point> watershedPoints = DataCollector.INSTANCE.getWatershedPoints();

            Mat wresult2 = doWatershed(invert);
            pluginListener.addImageTab("watershed invert", wresult2);
            HashMap<Integer, Point> watershedPoints2 = DataCollector.INSTANCE.getWatershedPoints();
            watershedPoints.putAll(watershedPoints2);
            wpoints.putAll(watershedPoints);

            Core.add(wresult, wresult2, wresult);
            pluginListener.addImageTab("watershed combine", wresult);

            LineCreator lineCreator = new LineCreator(wresult, new ArrayList<>(watershedPoints.values()));
            lineCreator.extractEdgePoints();
            lineCreator.createLines();

            ArrayList<Line> lines = new ArrayList<>(lineCreator.lines);
            ArrayList<Point> epoints = new ArrayList<>(lineCreator.edgePoints);

            interpolate(lines);
            analyzeLines(interpolateLines);

          //  regression(lines);
          //  interpolate(regressLines);
          //  RegressionPointsAnalysis analysis = new RegressionPointsAnalysis(interpolateImage, interpolateLines, image);
        }

       /* long start = System.nanoTime();
        print(this.title + " mask analyze begin");
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
        long end = System.nanoTime();
        long traceTime = end-start;
        print(this.title + " mask analyze finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));
*/
        result = image.clone();
        drawWatershed(result);
        print(this.title + " finish");
    }

    private ArrayList<Line> analyzeLines(ArrayList<Line> lines) {
        for(Line line : lines){
            for(Point p : line.points) {
                print("line "+line.label+" curv" + p.getCurvature())  ;
            }
        }
       /* int th = 20;
        for (int i = 0; i < lines.size(); i++) {
            Line line1 = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);
                Point[] points = RegressionPointsAnalysis.getClosesBorderPoints(line1.getBorderPoints(), line2.getBorderPoints());
                if(points[0].len(points[1]) < th) {

                }
            }
        }*/
        return null;
    }

    private Mat doWatershed(Mat image) {
        MorphologyPlugin morphology = new MorphologyPlugin();
        WatershedPlugin watershed = new WatershedPlugin();


        morphology.initImage(image);
        morphology.run("Closing", area_size);
        mresult = morphology.getResult(false);

        watershed.initImage(mresult);
        watershed.run();
        return watershed.getResult(false);
    }

    /**
     * Добавить точку, скорректировав ее кординаты относительно x, y
     * @param x
     * @param y
     * @param watershedPoints
     * @param cols
     */
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

    private void interpolate(ArrayList<Line> lines) {
        long start = System.nanoTime();
        print(this.title + " interpolation begin");

        double b, g, r;
        b = g = r = 0;
        Random rand = new Random();

        Interpolacion interpolacionX = new Interpolacion();
        Interpolacion interpolacionY = new Interpolacion();
        interpolateImage = new Mat(image.size(), image.type(), new Scalar(255, 255, 255));//Mat.zeros(image.size(), image.type());
        interpolateLines = new ArrayList<>();
        interpolatePoints = new ArrayList<>();

        int lineLabel = 0;
        for (Line line : lines) {
            if (line.points.size() < 3)
                continue;
            Line x = new Line();
            Line y = new Line();
            int i = 0;
            for (Point p : line.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }


            interpolacionX.extractPointsFormLine(x);
            interpolacionY.extractPointsFormLine(y);

            double step = 1;
            Line lix = interpolacionX.interpolate(step);
            Line liy = interpolacionY.interpolate(step);
            boolean bad = true;
            while(bad) {
                bad = false;
                for (i = 1; i < lix.points.size(); i++) {
                    if (Math.abs(lix.points.get(i).y - lix.points.get(i - 1).y) > 1 || Math.abs(liy.points.get(i).y - liy.points.get(i - 1).y) > 1) {
                        step /= 2;
                        lix = interpolacionX.interpolate(step);
                        liy = interpolacionY.interpolate(step);
                        bad = true;
                        break;
                    }
                }
            }

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

                //if (!interpolatePoints.contains(iPoint)) {
                    iPoint.curv[0] = lix.points.get(i).curvature;
                    iPoint.curv[1] = liy.points.get(i).curvature;
                    iPoint.lineLabel = lineLabel;

                    interpolatePoints.add(iPoint);
                    iLine.add(iPoint);

                    interpolateImage.put(iPoint.y, iPoint.x, mcolor);
               // }
            }
          //  if(iLine.points.size() >= minPoints) {
                interpolateLines.add(lineLabel, iLine);
                lineLabel++;
          //  }
        }

        /*for (Point p : epoints) {
            if (p.isCrossroad) {
                im_inter.put(p.y, p.x, new double[]{255, 255, 255});
            }
        }*/
        //  DataCollector.INSTANCE.addtoHistory("before interpolate", img);
        pluginListener.addImageTab("interpolate", interpolateImage);

        long end = System.nanoTime();
        long traceTime = end-start;
        print(this.title + " interpolation finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));
    }

    private void regression(ArrayList<Line> lines) {
        long start = System.nanoTime();
        print(this.title + " regression begin");

        double b, g, r;
        b = g = r = 0;
        Random rand = new Random();

        //OLSSimpleRegression regression = new OLSSimpleRegression();

        Optimizer optimizerX = new Optimizer();
        Optimizer optimizerY = new Optimizer();

        regressLines = new ArrayList<>();
        regressPoints = new ArrayList<>();
        regressImage = Mat.zeros(image.size(), image.type());


        for (Line line : lines) {
            if (line.points.size() < 3)
                continue;

            Line x = new Line();
            Line y = new Line();
            int i = 0;
            for (Point p : line.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }

            optimizerX.extractPointsFormLine(x);
            optimizerY.extractPointsFormLine(y);

            double step = 1;
            Line lrx = optimizerX.optimize(step);
            Line lry = optimizerY.optimize(step);
            boolean bad = true;
            while(bad) {
                bad = false;
                for (i = 1; i < lrx.points.size(); i++) {
                    if (Math.abs(lrx.points.get(i).y - lrx.points.get(i - 1).y) > 1 || Math.abs(lry.points.get(i).y - lry.points.get(i - 1).y) > 1) {
                        step /= 2;
                        lrx = optimizerX.optimize(step);
                        lry = optimizerY.optimize(step);
                        bad = true;
                        break;
                    }
                }
            }



            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            Line rLine = new Line();

            for (i = 0; i < lrx.points.size(); i++) {
                Point rPoint = new Point(lrx.points.get(i).y, lry.points.get(i).y);

                if (!regressPoints.contains(rPoint)) {

                    regressPoints.add(rPoint);
                    rLine.add(rPoint);

                    regressImage.put(rPoint.y, rPoint.x, mcolor);
                }
            }


           /* for (Point p : line.points) {
                int id = line.points.indexOf(p);
                p.x = lrx.points.get(id).y;
                p.y = lry.points.get(id).y;

                Point rPoint = new Point(p);
                regressPoints.add(rPoint);
                rLine.add(rPoint);

                regressImage.put(p.y, p.x, mcolor);
            }*/
            if(rLine.points.size() >= minPoints) {
                regressLines.add(rLine);
            }
        }
        long end = System.nanoTime();
        long traceTime = end-start;
        print(this.title + " regression finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));
/*
        for (Point p : epoints) {
            if (p.isCrossroad) {
                im_regr.put(p.y, p.x, new double[]{255, 255, 255});
            }
        }*/
        pluginListener.addImageTab("regression", regressImage);


    }

}
