package entities;

import org.opencv.core.Mat;

import java.util.*;

public enum MassiveWorker {
    INSTANCE;

    private ArrayList<Integer> ids = new ArrayList<>();
    private TreeMap<Integer, ArrayList<Integer>> map = new TreeMap<>();
    private int max;
    private int min;

    public void sort( Mat image ) {
        ids = new ArrayList<>();
        map = new TreeMap<>();

        Integer[] id = new Integer[( int ) image.total()];
        Integer[] array = new Integer[id.length];
        int row, col;
        for ( int i = 0; i < id.length; i++ ) {
            id[i] = i;
            row = i / image.width();
            col = i % image.width();
            array[i] = ( int ) image.get( row, col )[0];
        }
        Arrays.sort( id, new MyComparator( array ) );
        findExtremums( array );
        generateMap( array );

        ids.clear();
        Collections.addAll( ids, id );
    }

    public void sort( Mat gray, int[] _lowercompletion ) {
        ids = new ArrayList<>();
        map = new TreeMap<>();

        Integer[] id = new Integer[( int ) gray.total()];
        Integer[] luminance = new Integer[id.length];
        int row, col;
        for ( int i = 0; i < id.length; i++ ) {
            id[i] = i;
            row = i / gray.width();
            col = i % gray.width();
            luminance[i] = ( int ) gray.get( row, col )[0];
        }

        Integer[] lowercompletion = new Integer[_lowercompletion.length];
        int i = 0;
        for ( int value : _lowercompletion ) {
            lowercompletion[i++] = Integer.valueOf( value );
        }

        Arrays.sort( id, new MyDifficultComparator( luminance, lowercompletion ) );
        findExtremums( luminance );
        ids.clear();
        Collections.addAll( ids, id );
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public TreeMap<Integer, ArrayList<Integer>> getMap() {
        return map;
    }

    private void generateMap( Integer[] array ) {
        map.clear();
        for ( int i = 0; i < array.length; i++ ) {
            if ( map.containsKey( array[i] ) ) {
                ArrayList<Integer> ar = map.get( array[i] );
                ar.add( i );
                map.put( array[i], ar );
            } else {
                ArrayList<Integer> ar = new ArrayList<Integer>();
                ar.add( i );
                map.put( array[i], ar );
            }
        }
    }

    public void findExtremums( Integer[] array ) {
        ArrayList<Integer> ar = new ArrayList<>();
        Collections.addAll( ar, array );
        max = Collections.max( ar );
        min = Collections.min( ar );
    }

    private class MyComparator implements Comparator<Integer> {
        Integer[] values;

        private MyComparator( Integer[] array ) {
            values = array;
        }

        public int compare( Integer i, Integer j ) {
            if ( values[i] > values[j] )
                return -1;
            else if ( values[i].equals( values[j] ) )
                return 0;
            else if ( values[i] < values[j] )
                return 1;
            else
                return i.compareTo( j );
        }
    }

    private class MyDifficultComparator implements Comparator<Integer> {
        Integer[] luminance;
        Integer[] lowerCompletion;

        private MyDifficultComparator( Integer[] array1, Integer[] array2 ) {
            luminance = array1;
            lowerCompletion = array2;
        }

        public int compare( Integer i, Integer j ) {
            if ( luminance[i] > luminance[j] ) {
                return -1;
            }
            if ( luminance[i] < luminance[j] ) {
                return 1;
            }
            if ( lowerCompletion[i] > lowerCompletion[j] ) {
                return -1;
            }
            if ( lowerCompletion[i] < lowerCompletion[j] ) {
                return 1;
            }
            return 0;
        }
    }
}
