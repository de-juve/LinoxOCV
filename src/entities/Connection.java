package entities;

public class Connection {
    public Point p1, p2;
    public double weight;

    public Connection( Point _p1, Point _p2, double _weight ) {
        p1 = _p1;
        p2 = _p2;
        weight = _weight;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof Connection ) {
            if ( ( p1.equals( ( ( Connection ) obj ).p1 ) && p2.equals( ( ( Connection ) obj ).p2 ) ) ||
                    ( p1.equals( ( ( Connection ) obj ).p2 ) && p2.equals( ( ( Connection ) obj ).p1 ) ) ) {
                return true;
            }
        }
        return false;
    }
}
