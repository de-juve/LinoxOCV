package plugins.approximation;

import entities.Line;
import entities.Point;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.stat.StatUtils;
import org.math.plot.Plot2DPanel;

public class Optimizer {
    double[] xArr, yArr;
    Plot2DPanel plot;
    LevenbergMarquardtOptimizer optimizer;
    CurveFitter fitter;

    public Line optimize() {
        Line line = new Line();

        for (int i = 0; i < xArr.length; i++) {
            fitter.addObservedPoint(xArr[i], yArr[i]);
        }

        double[] initialguess = new double[]{1d, 2.5, 3d, -7d};
//        initialguess[0] = 1.0d;
//        initialguess[1] = 2.5d;
//        initialguess[2] = 1.0d;
//        initialguess[3] = 1.0d;

        //ParabolaFunction sif = new ParabolaFunction();
        Poly3Function sif = new Poly3Function();

        double[] bestCoefficients = fitter.fit(sif, initialguess);

        int n = (int) (Math.abs(StatUtils.max(xArr) - StatUtils.min(xArr)));
        double[] xc = new double[n + 1];
        double[] yc = new double[n + 1];
        double xi = StatUtils.min(xArr);
        for (int i = 0; i < xc.length; i++) {
            xc[i] = xi + i;
            yc[i] = sif.value(xc[i], bestCoefficients);
            line.add(new Point((int) xc[i], (int) yc[i]));
        }
        return line;
    }

    public void extractPointsFormLine(Line line) {
        xArr = new double[line.points.size()];
        yArr = new double[line.points.size()];
        optimizer = new LevenbergMarquardtOptimizer();
        fitter = new CurveFitter(optimizer);
        plot = new Plot2DPanel();
        int i = 0;
        for (Point point : line.points) {
            xArr[i] = point.x;
            yArr[i] = point.y;
            i++;

        }
    }

    /**
     * This is my implementation of ParametricRealFunction
     * Implements y = ax^-1 + b for use with an Apache CurveFitter implementation
     */
    /*private class SimpleInverseFunction2 implements ParametricUnivariateFunction
    {
        public double value(double x, double[] doubles) throws FunctionEvaluationException
        {
            //y = ax^-1 + b
            //"double[] must include at least 1 but not more than 2 coefficients."
            if(doubles == null || doubles.length ==0 || doubles.length > 2) throw new FunctionEvaluationException(doubles);
            double a = doubles[0];
            double b = 0;
            if(doubles.length >= 2) b = doubles[1];
            return a * Math.pow(x, -1d) + b;
        }
        public double[] gradient(double x, double[] doubles) throws FunctionEvaluationException
        {
            //derivative: -ax^-2
            //"double[] must include at least 1 but not more than 2 coefficients."
            if(doubles == null || doubles.length ==0 || doubles.length > 2) throw new FunctionEvaluationException(doubles);
            double a = doubles[0];
            double b = 0;
            if(doubles.length >= 2) b = doubles[1];
            double derivative = -a * Math.pow(x, -2d);
            double[]gradientVector = new double[1];
            gradientVector[0] = derivative;
            return gradientVector;
        }
    }*/

    private static class SimpleInverseFunction implements ParametricUnivariateFunction {
        @Override
        public double value(double x, double[] parameters) {
            return parameters[0] / x + (parameters.length < 2 ? 0 : parameters[1]);
        }

        @Override
        public double[] gradient(double x, double[] doubles) {
            double[] gradientVector = new double[doubles.length];
            gradientVector[0] = 1 / x;
            if (doubles.length >= 2) {
                gradientVector[1] = 1;
            }
            return gradientVector;
        }
    }

    private static class ParabolaFunction implements ParametricUnivariateFunction {
        @Override
        public double value(double x, double[] parameters) {
            return parameters[0] * x * x + parameters[1] * x + (parameters.length < 3 ? 0 : parameters[2]);
        }

        @Override
        public double[] gradient(double x, double[] doubles) {
            double[] gradientVector = new double[doubles.length];
            gradientVector[0] = x * x;
            gradientVector[1] = x;
            if (doubles.length >= 3) {
                gradientVector[2] = 1;
            }
            return gradientVector;
        }

        public double firstDerivative(double x, double[] parameters) {
            return 2 * parameters[0] * x + parameters[1];
        }

        public double secondDerivative(double x, double[] parameters) {
            return 2 * parameters[0];
        }
    }

    private static class Poly3Function implements ParametricUnivariateFunction {
        @Override
        public double value(double x, double[] parameters) {
            return parameters[0] * x * x * x + parameters[1] * x * x + parameters[2] * x + (parameters.length < 4 ? 0 : parameters[3]);
        }

        @Override
        public double[] gradient(double x, double[] doubles) {
            double[] gradientVector = new double[doubles.length];
            gradientVector[0] = x * x * x;
            gradientVector[1] = x * x;
            gradientVector[2] = x;
            if (doubles.length >= 4) {
                gradientVector[3] = 1;
            }
            return gradientVector;
        }

       /* public double firstDerivative( double x, double[] parameters ) {
            return 3 * parameters[0] * x * x + 2 * parameters[1] * x + parameters[2];
        }

        public double secondDerivative( double x, double[] parameters ) {
            return 6 * parameters[0] * x + 2 * parameters[1];
        }*/
    }

}
