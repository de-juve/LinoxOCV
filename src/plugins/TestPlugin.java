package plugins;

import entities.DataCollector;
import entities.Line;
import entities.Point;
import gui.Linox;
import org.opencv.core.Mat;
import plugins.approximation.Interpolacion;

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
        result = Mat.zeros( image.size(), image.type() );
        Random rand = new Random();
        Linox.getInstance().getStatusBar().setProgress( title, 0, 100 );
        HashMap<Integer, Point> wpoints = DataCollector.INSTANCE.getWatershedPoints();
        LineCreator lineCreator = new LineCreator( DataCollector.INSTANCE.getWatershedImg(), new ArrayList<>( wpoints.values() ) );
        lineCreator.extractEdgePoints();
        lineCreator.createLines();
        ArrayList<Line> lines = lineCreator.lines;
        //interpolacion
        Interpolacion interpolacion = new Interpolacion();

        for ( Line line : lines ) {
            System.out.println( line.points.toString() );
            double[] mcolor = new double[]{ b, g, r };
            r = rand.nextInt( 256 );
            g = rand.nextInt( 256 );
            b = rand.nextInt( 256 );

            for ( Point point : line.points ) {
                result.put( point.y, point.x, mcolor );
            }
            // interpolacion.extractPointsFormLine( line );
            // interpolacion.run();
            // break;
        }


        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

}
