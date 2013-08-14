package plugins.approximation;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import org.opencv.core.Point;

import java.util.LinkedList;

public class Regression {
    LMAFunction lmaFunction;
    double[] fitParams;

    public Regression() {
        lmaFunction = new PolynomFunction();
    }

    public void calcFitParams(LinkedList<Point> line, String typeFunction, int polynomialDegree) {
        double[] xArr = new double[line.size()];
        double[] yArr = new double[line.size()];
        double[] params = new double[1];


        for (int i = 0; i < line.size(); i++) {
            xArr[i] = line.get(i).x;
            yArr[i] = line.get(i).y;
        }

        switch (typeFunction) {
            case "polynomial": {
                params = new double[polynomialDegree];
                for (int i = 0; i < polynomialDegree; i++) {
                    params[i] = 1;
                }
                lmaFunction = new PolynomFunction();
                break;
            }
            case "parabola": {
                params = new double[3];
                for (int i = 0; i < 3; i++) {
                    params[i] = 1;
                }
                lmaFunction = new ParabolaFunction();
                break;
            }
            case "sin": {
                params = new double[2];
                for (int i = 0; i < 2; i++) {
                    params[i] = 1;
                }
                lmaFunction = new SinFunction();
                break;
            }
        }

        LMA lmaPar = new LMA(
                lmaFunction,
                params.clone(),
                new double[][]{xArr, yArr}
        );

        lmaPar.fit();
        fitParams = lmaPar.parameters;
    }

    public int getY(double x) {
        return (int) lmaFunction.getY(x, fitParams);
    }

    private static class PolynomFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            Double accumulator = a[a.length - 1];
            for (int i = a.length - 2; i >= 0; i--) {
                accumulator = (accumulator * x) + a[i];
            }
            return accumulator;
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            return Math.pow(x, parameterIndex);
        }
    }

    private static class ParabolaFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            return a[2] * x * x + a[1] * x + a[0];
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            switch (parameterIndex) {
                case 0:
                    return 1;
                case 1:
                    return x;
                case 2:
                    return x * x;
            }
            throw new RuntimeException("No such parameter index: " + parameterIndex);
        }
    }

    private static class SinFunction extends LMAFunction {
        @Override
        public double getY(double x, double[] a) {
            return a[0] * Math.sin(x / a[1]);
        }

        @Override
        public double getPartialDerivate(double x, double[] a, int parameterIndex) {
            switch (parameterIndex) {
                case 0:
                    return Math.sin(x / a[1]);
                case 1:
                    return a[0] * Math.cos(x / a[1]) * (-x / (a[1] * a[1]));
            }
            throw new RuntimeException("No such fit parameter: " + parameterIndex);
        }
    }

    ;
}

