package plugins.snake;

import java.util.LinkedList;

public class Penalty {
    private int threshold;
    private LinkedList<Double> penalties;
    private int penaltiesSize = 10;
    private double[] weights;

    public Penalty( int threshold ) {
        penalties = new LinkedList<>();
        weights = new double[]{ 2, 1.5, 1, 0.5, 0.3, 0.2, 0.15, 0.1, 0.05, 0.01 };
        this.threshold = threshold;
    }

    public void addPenalty( double value ) {
        if ( penalties.size() >= penaltiesSize ) {
            penalties.removeLast();
        }
        penalties.addFirst( value );
    }

    public double countPenalty() {
        double result = 0;
        for ( int i = 0; i < penalties.size(); i++ ) {
            result += weights[i] * penalties.get( i );
        }
        return result;
    }

    public int getThreshold() {
        return threshold;
    }
}
