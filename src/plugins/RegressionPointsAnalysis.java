package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import plugins.approximation.Interpolacion;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RegressionPointsAnalysis {
    private final int THRESHOLD = 20;

    Mat image;

    public RegressionPointsAnalysis(Mat _image, ArrayList<Line> lines) {
        long start = System.nanoTime();
        this.image = _image;
        SimpleDirectedWeightedGraph graph = new SimpleDirectedWeightedGraph( DefaultWeightedEdge.class );
        for (int i = 0; i < lines.size(); i++) {
            Line line1 = lines.get(i);
            graph.addVertex(line1);
            L : for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);
                graph.addVertex(line2);

                for (Point point1 : line1.getBorderPoints()) {
                    for (Point point2 : line2.getBorderPoints()) {
                        if(Math.sqrt(point1.len(point2)) < THRESHOLD) {
                            DefaultWeightedEdge e = (DefaultWeightedEdge) graph.addEdge(line1, line2);

                            double weight = Math.abs(line1.getCurvature() - line2.getCurvature());
                            graph.setEdgeWeight(e, weight);

                            line1.addConnection(line2);
                            // line2.addConnection(line1);
                            continue L;
                        }
                    }
                }
            }
        }

        DijkstraShortestPath dejkstra;
        int k = 1;
        for (int i = 0; i < lines.size(); i++) {
            Line line1 = lines.get(i);
            L:
            for (int j = i + 1; j < lines.size(); j++) {
                Line line2 = lines.get(j);
                dejkstra = new DijkstraShortestPath(graph, line1, line2);
                double len = dejkstra.getPathLength();
                if(len != Double.POSITIVE_INFINITY) {
                    ArrayList<DefaultWeightedEdge> pathList = (ArrayList<DefaultWeightedEdge>) dejkstra.getPathEdgeList();

                    ListIterator<DefaultWeightedEdge> iterator = pathList.listIterator();
                    DefaultWeightedEdge edge = iterator.next();

                    Line source = (Line) graph.getEdgeSource(edge);
                    Line target = (Line) graph.getEdgeTarget(edge);
                    String label = " parh: " + source.label + " + " + target.label;
                    source =  this.mergeLines(source, target);

                    while(iterator.hasNext()) {
                        edge = iterator.next();
                        target = (Line) graph.getEdgeTarget(edge);
                        label += " + " + target.label;
                        source =  this.mergeLines(source, target);

                    }
                    interpolate(source, label);
                    /*Mat im_ = Mat.zeros(image.size(), image.type());
                    Random rand = new Random();
                    double b, g, r;
                    b = g = r = 0;
                    r = rand.nextInt(220);
                    g = rand.nextInt(220);
                    b = rand.nextInt(220);
                    double[] mcolor = new double[]{b, g, r};


                    if (source.points.size() > 1) {
                        for (Point p : source.points) {
                            im_.put(p.y, p.x, mcolor);
                        }
                    }

                    DataCollector.INSTANCE.addtoHistory("merge line " + k, im_);
                    AbstractPlugin.printMessage("path length " + pathList.size());*/
                    k++;
                }

            }
        }



/*
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
        ( Linox.getInstance().getImageStore() ).addImageTab("graph", matGraph);*/



       /* for (int i = 0; i < lines.size(); i++) {
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
        }*/
/*
        for (Line line : lines) {
            ArrayList<Line> conLines = line.getConnection();
            for (Line cLine : conLines) {
                Line mergeLine = this.mergeLines(line, cLine);
                  interpolate(mergeLine, line.label + " + " + cLine.label);
            }
        }

        Mat im_ = Mat.zeros(image.size(), image.type());
        Random rand = new Random();
        double b, g, r;
        b = g = r = 0;
        for (Line line : lines) {
            r = rand.nextInt(220);
            g = rand.nextInt(220);
            b = rand.nextInt(220);

            double[] mcolor = new double[]{b, g, r};

            if (line.points.size() > 1) {
                for (Point p : line.points) {
                    im_.put(p.y, p.x, mcolor);
                }
            }
            //line.extractBorderPoints();
        }
        DataCollector.INSTANCE.addtoHistory("after regr", im_);*/

        long end = System.nanoTime();
        long traceTime = end-start;
        AbstractPlugin.printMessage("regr point analysis finish: " + TimeUnit.MILLISECONDS.convert(traceTime, TimeUnit.NANOSECONDS));

    }

    private void interpolate(Line mergeLine, String label) {
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
        for(Point p : mergeLine.points) {
            x.add(new Point(i, p.x));
            y.add(new Point(i, p.y));
            i++;
        }

       /* Point bp11 = line1.getBorderPoints().get(0);
        Point bp12 = line1.getBorderPoints().get(1);
        Point bp21 = line2.getBorderPoints().get(0);
        Point bp22 = line2.getBorderPoints().get(1);
        double d11_21 = bp11.len(bp21);
        double d11_22 = bp11.len(bp22);
        double d12_21 = bp12.len(bp21);
        double d12_22  = bp12.len(bp22);

        if (d11_21 < d11_22 && d11_21 < d12_21 && d11_21 < d12_22) {
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
        } else if (d11_22 < d11_21 && d11_22 < d12_21 && d11_22 < d12_22) {
            for (Point p : line2.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }
            for (Point p : line1.points) {
                x.add(new Point(i, p.x));
                y.add(new Point(i, p.y));
                i++;
            }
        } else if (d12_21 < d12_22 && d12_21 < d11_21 && d12_21 < d11_22) {
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
        } else if (d12_22 < d12_21 && d12_22 < d11_21 && d12_22 < d11_22) {
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
        }*/




        double step = 0.05;
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

        DataCollector.INSTANCE.addtoHistory("inter " + label , interpolateImage);

    }

    private Line mergeLines(Line line1, Line line2) {
        Line mergeLine = new Line();
        int i = 0;
        Point bp11 = line1.getBorderPoints().get(0);
        Point bp12 = line1.getBorderPoints().get(1);
        Point bp21 = line2.getBorderPoints().get(0);
        Point bp22 = line2.getBorderPoints().get(1);
        double d11_21 = bp11.len(bp21);
        double d11_22 = bp11.len(bp22);
        double d12_21 = bp12.len(bp21);
        double d12_22  = bp12.len(bp22);

        if (d11_21 < d11_22 && d11_21 < d12_21 && d11_21 < d12_22) {
            for (int j = line1.points.size()-1; j >= 0; j--) {
                mergeLine.add(line1.points.get(j));
            }
            for (Point p : line2.points) {
                mergeLine.add(p);
            }
        } else if (d11_22 < d11_21 && d11_22 < d12_21 && d11_22 < d12_22) {
            for (Point p : line2.points) {
                mergeLine.add(p);
            }
            for (Point p : line1.points) {
                mergeLine.add(p);
            }
        } else if (d12_21 < d12_22 && d12_21 < d11_21 && d12_21 < d11_22) {
            for (Point p : line1.points) {
                mergeLine.add(p);
            }
            for (Point p : line2.points) {
                mergeLine.add(p);
            }
        } else if (d12_22 < d12_21 && d12_22 < d11_21 && d12_22 < d11_22) {
            for (Point p : line1.points) {
                mergeLine.add(p);
            }
            for (int j = line2.points.size()-1; j >= 0; j--) {
                mergeLine.add(line2.points.get(j));
            }
        }
        return mergeLine;
    }
}
