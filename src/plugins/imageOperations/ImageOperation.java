package plugins.imageOperations;

import entities.Shed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public abstract class ImageOperation {
    protected ArrayList<Integer> values;
    protected int width, height;

    public void createImage( int[] closing, int[] opening, TreeMap<Integer, Shed> closingSheds, TreeMap<Integer, Shed> openingSheds, int[] result ) {
        values = new ArrayList<>();

        defineValues( closing, opening, closingSheds, openingSheds );

        int max = Collections.max( values );
        int min = Collections.min( values );
        for ( int i = 0; i < closing.length; i++ ) {

            result[i] = ( ( values.get( i ) - min ) * 255 / ( max - min ) );
        }
    }

    protected Integer getValue( int id ) {
        return values.get( id );
    }

    protected abstract void defineValues( int[] closing, int[] opening, TreeMap<Integer, Shed> closingSheds, TreeMap<Integer, Shed> openingSheds );

    protected abstract void defineValues( int[] closing, int[] opening );

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeight( int height ) {
        this.height = height;
    }
}
