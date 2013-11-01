package plugins.roadNetwork;

public class CrossroadMask {
    protected int[] weight;
    protected int sumWeight;

    public CrossroadMask( int[] _weight ) {
        super();
        weight = _weight;
        countSumWeights();
    }

    public double R( int[] watersheds ) {
        double result = 0;
        for ( int i = 0; i < watersheds.length; i++ ) {
            result += weight[i] * watersheds[i];
        }
        return result / sumWeight;
    }

    private void countSumWeights() {
        for ( int aWeight : weight ) sumWeight += Math.abs( aWeight );
    }
}
