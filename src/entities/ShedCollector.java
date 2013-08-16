package entities;

import java.util.TreeMap;

public enum ShedCollector {
    INSTANCE;

    private TreeMap<Integer, Shed> sheds;

    public void addShed( Shed shed ) {
        if ( sheds == null ) {
            sheds = new TreeMap<>();
        }
        sheds.put( shed.getLabel(), shed );
    }

    public void addElementToShed( Integer label, Point p ) {
        sheds.get( label ).addPoint( p );
    }

    public Shed getShed( Integer label ) {
        return sheds.get( label );
    }

    public int size() {
        if ( sheds == null )
            return 0;
        return sheds.size();
    }

    public void clear() {
        if ( sheds == null ) {
            sheds = new TreeMap<>();
        } else {
            sheds.clear();
        }
    }

    public TreeMap<Integer, Shed> getSheds() {
        return sheds;
    }
}
