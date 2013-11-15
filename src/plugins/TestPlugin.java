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
        ArrayList<Point> epoints = lineCreator.edgePoints;

        //interpolacion
        Interpolacion interpolacion = new Interpolacion();

        Mat img = Mat.zeros( image.size(), image.type() );
        for ( Line line : lines ) {
            if ( line.points.size() <= 2 )
                continue;
            double[] mcolor = new double[]{ b, g, r };
            r = rand.nextInt( 220 );
            g = rand.nextInt( 220 );
            b = rand.nextInt( 220 );

            for ( Point point : line.points ) {
                img.put( point.y, point.x, mcolor );
            }
            DataCollector.INSTANCE.addtoHistory( "before interpolate", img );
        }

        for ( Line line : lines ) {
            if ( line.points.size() <= 2 )
                continue;

            //System.out.println( line.points.toString() );


            Line x = new Line();
            Line y = new Line();
            int i = 0;
            for ( Point p : line.points ) {
                x.add( new Point( i, p.x ) );
                y.add( new Point( i, p.y ) );
                i++;
            }
            interpolacion.extractPointsFormLine( x );
            Line lx = interpolacion.interpolate();

            interpolacion.extractPointsFormLine( y );
            Line ly = interpolacion.interpolate();

            double[] mcolor = new double[]{ b, g, r };
            r = rand.nextInt( 220 );
            g = rand.nextInt( 220 );
            b = rand.nextInt( 220 );

            for ( Point p : line.points ) {
                int id = line.points.indexOf( p );
                p.x = lx.points.get( id ).y;
                p.y = ly.points.get( id ).y;

                result.put( p.y, p.x, mcolor );
            }

            //interpolacion.run();
            //break;


        }

        for ( Point p : epoints ) {
            if ( p.isCrossroad ) {
                img.put( p.y, p.x, new double[]{ 255, 255, 255 } );
                result.put( p.y, p.x, new double[]{ 255, 255, 255 } );
            }
        }
        DataCollector.INSTANCE.addtoHistory( "before interpolate", img );
        DataCollector.INSTANCE.addtoHistory( "after interpolate", result );


        Linox.getInstance().getStatusBar().setProgress( title, 100, 100 );

        if ( pluginListener != null ) {
            pluginListener.addImageTab();
            pluginListener.finishPlugin();
        }
    }

}
