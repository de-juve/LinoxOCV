package plugins.approximation;

import entities.Line;
import entities.Point;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.StatUtils;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import javax.swing.*;
import java.awt.*;

public class Interpolacion {
    double[] xArr, yArr;
    SplineInterpolator interpolator;
    PolynomialSplineFunction polynomial, firstDerivative, secondDerivative;
    Plot2DPanel plot;

    public void run() {
        PolynomialSplineFunction polinom = interpolator.interpolate(xArr, yArr);
        int n = (int) (Math.abs(StatUtils.max(xArr) - StatUtils.min(xArr)) / 0.1);
        double[] xc = new double[n];
        double[] yc = new double[n];
        double xi = StatUtils.min(xArr);
        for (int i = 0; i < xc.length; i++) {
            xc[i] = xi + 0.1 * i;
            yc[i] = polinom.value(xc[i]);
        }
        plot.addLegend("SOUTH");
        plot.addScatterPlot("Datos", xArr, yArr);
        plot.addLinePlot("Interpolation Spline", xc, yc);
        BaseLabel title = new BaseLabel("Interpolation Spline", Color.BLUE, 0.5, 1.1);
        plot.addPlotable(title);

        JFrame frame = new JFrame("Interpolacion Spline");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.add(plot, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public Line interpolate(double step) {
        Line line = new Line();

        int n = (int) (Math.abs(StatUtils.max(xArr) - StatUtils.min(xArr)) / step);
        double[] xc = new double[n + 1];
        double[] yc = new double[n + 1];
        double xi = StatUtils.min(xArr);
        for (int i = 0; i < xc.length; i++) {
            xc[i] = xi + step * i;
            yc[i] = polynomial.value(xc[i]);
            double k = Math.abs(secondDerivative.value(xc[i])) / Math.sqrt(Math.pow(1 + Math.pow(firstDerivative.value(xc[i]), 2), 3));
            line.add(new Point((int) xc[i], (int) yc[i], k));
        }
        return line;
    }

    public void extractPointsFormLine(Line line) {
        xArr = new double[line.points.size()];
        yArr = new double[line.points.size()];
        interpolator = new SplineInterpolator();
        plot = new Plot2DPanel();
        int i = 0;
        for (Point point : line.points) {
            xArr[i] = point.x;
            yArr[i] = point.y;
            i++;
        }
        polynomial = interpolator.interpolate(xArr, yArr);
        firstDerivative = polynomial.polynomialSplineDerivative();
        secondDerivative = firstDerivative.polynomialSplineDerivative();
    }
}
