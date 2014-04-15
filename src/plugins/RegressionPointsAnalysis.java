package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import gui.Linox;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import plugins.approximation.Interpolacion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RegressionPointsAnalysis {
    private final int THRESHOLD = 20;

    Mat image;

    public RegressionPointsAnalysis(Mat _image, ArrayList<Line> lines) {
        long start = System.nanoTime();
        this.image = _image;
        ListenableDirectedWeightedGraph graph = new ListenableDirectedWeightedGraph( DefaultWeightedEdge.class );
        for (int i = 0; i < lines.size(); i++) {
            Line line1 = lines.get(i);
            graph.addVertex(line1);
            for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);
                graph.addVertex(line2);
                DefaultWeightedEdge e = (DefaultWeightedEdge) graph.addEdge(line1, line2);
                double weight = 1;
                graph.setEdgeWeight(e, weight);

            }
        }



        JGraphModelAdapter m_jgAdapter;
        m_jgAdapter = new JGraphModelAdapter( graph );
        JGraph jgraph = new JGraph( m_jgAdapter );
        jgraph.setPreferredSize( new Dimension( 530, 320 ) );
        jgraph.setBackground( Color.decode("#FAFBFF") );
        BufferedImage ig = jgraph.getImage(null, 0);
        ig = new BufferedImage(ig.getWidth(), ig.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        byte[] pixels = ((DataBufferByte) ig.getRaster().getDataBuffer()).getData();
        Mat matGraph = new Mat(image.size(), image.type());
        matGraph.put(0, 0, pixels);
        ( Linox.getInstance().getImageStore() ).addImageTab("graph", matGraph);



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
                                Math.abs(point1.y - point2.y) < THRESHOLD )  {  //&&   Math.abs(line1.direction().minus(line2.direction())) < 10

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
                  interpolate(line, cLine);
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

        long end = System.nanoTime();
        long traceTime = end-start;
        AbstractPlugin.printMessage("regr point analysis finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));


    }

    private void interpolate(Line line1, Line line2) {
        double b, g, r;
        b = g = r = 0;
        Random rand = new Random();

        Interpolacion interpolacion = new Interpolacion();
        Mat interpolateImage = new Mat(image.size(), image.type(), new Scalar(255, 255, 255));//Mat.zeros(image.size(), image.type());
        ArrayList<Line> interpolateLines = new ArrayList<>();
        ArrayList<Point> interpolatePoints = new ArrayList<>();

        int lineLabel = 0;

            Line x = new Line();
            Line y = new Line();
            int i = 0;
        Point bp11 = line1.getBorderPoints().get(0);
        Point bp12 = line1.getBorderPoints().get(1);
        Point bp21 = line2.getBorderPoints().get(0);
        Point bp22 = line2.getBorderPoints().get(1);

         if (bp11.len(bp21) < bp11.len(bp22) && bp11.len(bp21) < bp12.len(bp21) && bp11.len(bp21) < bp12.len(bp22)) {
             for (Point p : line1.points) {
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
             for (Point p : line2.points) {
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
         } else if(bp11.len(bp21) > bp11.len(bp22) && bp11.len(bp22) < bp12.len(bp21) && bp11.len(bp22) < bp12.len(bp22)) {
             for (Point p : line1.points) {
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
             for (int j = line2.points.size()-1; j >= 0; j--) {
                 Point p = line2.points.get(j);
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
         } else if(bp12.len(bp21) < bp12.len(bp22) && bp12.len(bp21) < bp11.len(bp21) && bp12.len(bp21) < bp11.len(bp22)) {
             for (int j = line1.points.size()-1; j >= 0; j--) {
                 Point p = line1.points.get(j);
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
             for (Point p : line2.points) {
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
         } else {
             for (int j = line1.points.size()-1; j >= 0; j--) {
                 Point p = line1.points.get(j);
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
             for (int j = line2.points.size()-1; j >= 0; j--) {
                 Point p = line2.points.get(j);
                 x.add(new Point(i, p.x));
                 y.add(new Point(i, p.y));
                 i++;
             }
         }


        double step = 0.1;
            interpolacion.extractPointsFormLine(x);
            Line lix = interpolacion.interpolate(step);
            interpolacion.extractPointsFormLine(y);
            Line liy = interpolacion.interpolate(step);


            double[] mcolor = new double[]{b, g, r};
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            Line iLine = new Line();

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

                    interpolatePoints.add(iPoint);
                    iLine.add(iPoint);

                    interpolateImage.put(iPoint.y, iPoint.x, mcolor);
                }
            }
            interpolateLines.add(lineLabel, iLine);

        DataCollector.INSTANCE.addtoHistory("inter " + line1.label + " + " + line2.label , interpolateImage);


    }
}
