package plugins.imageOperations;

import entities.Shed;

import java.util.TreeMap;

public class ImageOperationMinus extends ImageOperation {
    @Override
    protected void defineValues( int[] closing, int[] opening ) {
        for ( int i = 0; i < closing.length; i++ ) {
            values.add( i, Math.abs( closing[i] - opening[i] ) );
        }
    }

    @Override
    protected void defineValues( int[] closing, int[] opening, TreeMap<Integer, Shed> closingSheds, TreeMap<Integer, Shed> openingSheds ) {
        defineValues( closing, opening );
    }
}
