package plugins.approximation;

import entities.Line;
import entities.Point;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.StatUtils;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Interpolacion {
    ArrayList<Double> xList, yList;
    double[] xArr, yArr;
    UnivariateInterpolator interpolator;
    Plot2DPanel plot;

    public void run() {
        UnivariateFunction polinom = interpolator.interpolate( xArr, yArr );
        int n = ( int ) Math.abs( StatUtils.max( xArr ) - StatUtils.min( xArr ) / 0.1 );
        double[] xc = new double[n];
        double[] yc = new double[n];
        double xi = StatUtils.min( xArr );
        for ( int i = 0; i < xc.length; i++ ) {
            xc[i] = xi + 0.1 * i;
            yc[i] = polinom.value( xc[i] );
        }
        plot.addLegend( "SOUTH" );
        plot.addScatterPlot( "Datos", xArr, yArr );
        plot.addLinePlot( "Interpolation Spline", xc, yc );
        BaseLabel title = new BaseLabel( "Interpolation Spline", Color.BLUE, 0.5, 1.1 );
        plot.addPlotable( title );

        JFrame frame = new JFrame( "Interpolacion Spline" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 500 );
        frame.add( plot, BorderLayout.CENTER );
        frame.setVisible( true );
    }

    public void extractPointsFormLine( Line line ) {
        xList = yList = new ArrayList<>();
        interpolator = new SplineInterpolator();
        plot = new Plot2DPanel();
        for ( Point point : line.points ) {
            xList.add( Double.valueOf( point.x ) );
            yList.add( Double.valueOf( point.y ) );
        }
        xArr = yArr = new double[xList.size()];
        for ( int i = 0; i < xList.size(); i++ ) {
            xArr[i] = xList.get( i );
            yArr[i] = yList.get( i );
        }
        System.out.println( Arrays.toString( xArr ) );
        Arrays.sort( xArr );
        System.out.println();
        System.out.println( Arrays.toString( xArr ) );
    }
}
